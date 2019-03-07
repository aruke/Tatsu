package org.rionlabs.tatsu.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import org.rionlabs.tatsu.R

class FullScreenDialogFragment : DialogFragment() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_fullscreen, container, false)
        toolbar = rootView.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            dismissAllowingStateLoss()
        }

        arguments?.apply {
            val titleResId = getInt(KEY_TITLE)
            toolbar.setTitle(titleResId)
            val contentLayoutId = getInt(KEY_LAYOUT)
            rootView.findViewById<FrameLayout>(R.id.content).apply {
                addView(inflater.inflate(contentLayoutId, this, false))
            }
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.apply {
            attributes.windowAnimations = R.style.FullScreenDialogAnimation
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true)
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true)
        }
    }

    companion object {

        private const val TAG = "FullScreenDialogFragment"

        private const val KEY_TITLE = "title"
        private const val KEY_LAYOUT = "layout"

        fun show(activity: FragmentActivity, @StringRes titleResId: Int, @LayoutRes layoutReId: Int) {
            val dialog = FullScreenDialogFragment()
            dialog.arguments = Bundle().apply {
                putInt(KEY_TITLE, titleResId)
                putInt(KEY_LAYOUT, layoutReId)
            }
            dialog.show(activity.supportFragmentManager, TAG)
        }
    }
}