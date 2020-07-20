package org.rionlabs.tatsu.ui.dialog.feedback

object FeedbackScreen {

    val initialState = ViewState()

    data class ViewState(
        val isLoading: Boolean = false,
        val emotion: Emotion? = null,
        val message: String? = null
    ) {
        val isSendEnabled: Boolean = !isLoading &&
                emotion != null && message?.isNotBlank() == true && message.length > 5
    }

    sealed class ViewEffect {
        object Success : ViewEffect()
        object ReCaptchaError : ViewEffect()
        object NetworkError : ViewEffect()
        object ServerError : ViewEffect()
    }
}