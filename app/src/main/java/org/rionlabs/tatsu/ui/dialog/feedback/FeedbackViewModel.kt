package org.rionlabs.tatsu.ui.dialog.feedback

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.rionlabs.tatsu.BuildConfig
import timber.log.Timber

class FeedbackViewModel(val app: Application) : ViewModel() {

    private val _viewStateData = MutableLiveData(FeedbackScreen.initialState)
    val viewStateData: LiveData<FeedbackScreen.ViewState>
        get() = _viewStateData

    private val _viewEffectData = LiveEvent<FeedbackScreen.ViewEffect>()
    val viewEffectData: LiveData<FeedbackScreen.ViewEffect>
        get() = _viewEffectData

    fun setEmotion(emotion: Emotion) {
        _viewStateData.value = _viewStateData.value?.copy(emotion = emotion)
    }

    fun updateMessage(message: String) {
        _viewStateData.value = _viewStateData.value?.copy(message = message)
    }

    fun trySendingFeedback() = viewModelScope.launch(IO) {
        _viewStateData.value?.let {
            _viewStateData.postValue(it.copy(isLoading = true))
        } ?: return@launch


        val reCaptchaToken = runCatching<String> {
            verifyWithReCaptcha()
        }.getOrElse {
            handleReCaptchaException(it)
            return@launch
        }

        val viewState = _viewStateData.value ?: return@launch
        val emotion = viewState.emotion ?: return@launch
        val message = viewState.message ?: return@launch

        try {
            sendFeedback(emotion, message, reCaptchaToken)
            _viewEffectData.postValue(FeedbackScreen.ViewEffect.Success)
        } catch (exception: Exception) {
            _viewEffectData.postValue(FeedbackScreen.ViewEffect.ServerError)
        }
        _viewStateData.postValue(_viewStateData.value?.copy(isLoading = false))
    }

    private suspend fun verifyWithReCaptcha(): String {
        val response = SafetyNet.getClient(app)
            .verifyWithRecaptcha(BuildConfig.SAFETY_NET_SITE_KEY).await()
        if (response.tokenResult?.isNotEmpty() == true) {
            return response.tokenResult
        } else throw IllegalArgumentException("SafetyNet token is Null or Empty")
    }

    private fun handleReCaptchaException(throwable: Throwable) {
        val networkErrorCodes = listOf(ConnectionResult.NETWORK_ERROR, ConnectionResult.TIMEOUT)
        if (throwable is ApiException && throwable.statusCode in networkErrorCodes) {
            _viewEffectData.postValue(FeedbackScreen.ViewEffect.NetworkError)
            val statusCodeString = CommonStatusCodes.getStatusCodeString(throwable.statusCode)
            Timber.e(throwable, "ApiException: $statusCodeString")
        } else {
            _viewEffectData.postValue(FeedbackScreen.ViewEffect.ReCaptchaError)
            Timber.e(throwable, "Error: ${throwable.message}")
        }

        _viewStateData.postValue(_viewStateData.value?.copy(isLoading = false))
    }

    private fun sendFeedback(emotion: Emotion, message: String, reCaptchaToken: String) {
        val requestParams = JSONObject().apply {
            put("emotion", emotion.value)
            put("message", message)
            put("reCaptchaToken", reCaptchaToken)
        }.toString()

        val request = Request.Builder()
            .method("POST", RequestBody.create(MediaType.get("application/json"), requestParams))
            .url(BuildConfig.FEEDBACK_POST_URL)
            .build()

        val response = OkHttpClient().newCall(request).execute()
        if (response.code() != 200) {
            throw RuntimeException("Request did not succeed. ${response.code()} ${response.message()}")
        }
    }
}