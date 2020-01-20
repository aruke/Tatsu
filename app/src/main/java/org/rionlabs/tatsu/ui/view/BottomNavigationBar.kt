package org.rionlabs.tatsu.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.android.material.bottomappbar.BottomAppBar
import org.rionlabs.tatsu.R

class BottomNavigationBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomAppBar(context, attrs, defStyleAttr) {

    private val startDrawable: Drawable?
    private val endDrawable: Drawable?

    var onStateChangeListener: OnStateChangeListener? = null

    init {
        val attrsArray = intArrayOf(
            com.google.android.material.R.attr.menu,
            com.google.android.material.R.attr.navigationIcon,
            com.google.android.material.R.attr.navigationContentDescription
        )
        val materialTypedArray = context.obtainStyledAttributes(attrs, attrsArray)

        // Invalid attributes
        materialTypedArray.getString(0)?.let {
            throw IllegalArgumentException("'com.google.android.material.R.attr.menu' is not supported by ${javaClass.name}")
        }
        materialTypedArray.getString(1)?.let {
            throw IllegalArgumentException("'com.google.android.material.R.attr.navigationIcon' is not supported by ${javaClass.name}")
        }
        materialTypedArray.getString(2)?.let {
            throw IllegalArgumentException("'com.google.android.material.R.attr.navigationContentDescription' is not supported by ${javaClass.name}")
        }

        materialTypedArray.recycle()

        val customTypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationBar)

        // Required Attributes
        startDrawable = customTypedArray.getDrawable(R.styleable.BottomNavigationBar_navStartIcon)
        startDrawable ?: run {
            throw IllegalArgumentException("Must provide 'R.attr.startIcon' to ${javaClass.name}")
        }
        endDrawable = customTypedArray.getDrawable(R.styleable.BottomNavigationBar_navEndIcon)
        endDrawable ?: run {
            throw IllegalArgumentException("Must provide 'R.attr.endIcon' to ${javaClass.name}")
        }

        // Optional Attribute
        val startDrawableCD =
            customTypedArray.getString(R.styleable.BottomNavigationBar_navStartIconCD)
        startDrawableCD ?: run {
            Log.w("CustomAppBar", "ContentDescription for navStartIcon is not provided")
        }
        val endDrawableCD = customTypedArray.getString(R.styleable.BottomNavigationBar_navEndIconCD)
        endDrawableCD ?: run {
            Log.w("CustomAppBar", "ContentDescription for navEndIcon is not provided")
        }

        customTypedArray.recycle()

        // Setup
        resetNavigation()
    }

    private fun onClickStartIcon() {
        // Move FAB to opposite direction
        fabAlignmentMode = FAB_ALIGNMENT_MODE_END
        // StartNav is navigation Icon. So Hide the menu.
        replaceMenu(R.menu.bottom_nav_bar_menu_empty)
        // Remove clickListeners from startIcon
        setNavigationOnClickListener(null)
        onStateChangeListener?.onNavigatedToStart()
    }

    private fun onClickEndIcon() {
        // Reverse the layout direction
        reverseLayoutDirection()
        // Now the visible icon should be endDrawable only
        navigationIcon = endDrawable
        replaceMenu(R.menu.bottom_nav_bar_menu_empty)

        fabAlignmentMode = FAB_ALIGNMENT_MODE_END

        setNavigationOnClickListener(null)
        onStateChangeListener?.onNavigatedToEnd()
    }

    private fun reverseLayoutDirection() {
        layoutDirection = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            View.LAYOUT_DIRECTION_LTR
        } else {
            View.LAYOUT_DIRECTION_RTL
        }
    }

    fun resetNavigation() {

        // Center the FAB
        fabAlignmentMode = FAB_ALIGNMENT_MODE_CENTER

        // Set startNavIcon
        navigationIcon = startDrawable
        // navigationContentDescription = startDrawableCD

        // Set endNavIcon
        replaceMenu(R.menu.bottom_nav_bar_menu_end)
        menu.findItem(R.id.action_end_nav).apply {
            icon = endDrawable
            // setContentDescription(endDrawableCD)
        }

        // Reset layout direction
        // TODO Get real value
        val ltr = true
        @Suppress("ConstantConditionIf")
        val newLayoutDirection = (if (ltr) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL)
        if (layoutDirection != newLayoutDirection) {
            layoutDirection = newLayoutDirection
        }

        // Set Menu onClickListener. There is going to be one menu only.
        setOnMenuItemClickListener {
            onClickEndIcon()
            return@setOnMenuItemClickListener true
        }

        setNavigationOnClickListener {
            onClickStartIcon()
        }

        onStateChangeListener?.onNavigationReset()
    }

    interface OnStateChangeListener {
        fun onNavigatedToStart()
        fun onNavigatedToEnd()
        fun onNavigationReset()
    }

}