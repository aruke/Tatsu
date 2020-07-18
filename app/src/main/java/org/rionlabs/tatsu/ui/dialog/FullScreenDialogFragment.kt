package org.rionlabs.tatsu.ui.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.rionlabs.tatsu.R

abstract class FullScreenDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.apply {
            attributes.windowAnimations = R.style.FullScreenDialogAnimation
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true)
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true)
        }
    }
}