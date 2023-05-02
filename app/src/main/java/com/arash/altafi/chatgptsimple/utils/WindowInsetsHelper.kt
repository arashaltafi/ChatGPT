package com.arash.altafi.chatgptsimple.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.arash.altafi.chatgptsimple.ext.setMargins
import kotlin.math.abs

/**
 * please attention to [setupSoftInput] doc.
 */
class WindowInsetsHelper(private val window: Window, private val rootView: View?) {

    var onKeyboardChangeLive = MutableLiveData<KeyboardState>()

    var isFullScreen: Boolean = false
        set(value) {
            field = value
            if (value)
                insetTypes.remove(WindowInsetsCompat.Type.systemBars())
            else
                insetTypes.add(WindowInsetsCompat.Type.systemBars())
        }
    var isAutoResizeKeyboard: Boolean = false
        set(value) {
            field = value
            if (value)
                insetTypes.add(WindowInsetsCompat.Type.ime())
            else
                insetTypes.remove(WindowInsetsCompat.Type.ime())
        }

    var keyboardHeight: Int = 0
        private set

    private val insetTypes = mutableSetOf<Int>()
    private var currentWindowInsets: WindowInsetsCompat = WindowInsetsCompat.Builder().build()

    init {
        handleWindowInsets()
    }

    /**
     * returning [WindowInsetsCompat.CONSUMED] is wrong! but because of a bug, temporally using it
     * the bug is: when [rootView.setMargins(insets)], an unwanted bottom-padding is applied
     * to [BottomNavigationView] in [MainActivity]
     */
    private fun handleWindowInsets() = rootView?.let {
        setupSoftInput()

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, windowInsets ->

            val currentInsetTypeMask = insetTypes.fold(0) { accumulator, type ->
                accumulator or type
            }
            currentWindowInsets = windowInsets
            val insets = currentWindowInsets.getInsets(currentInsetTypeMask)

            handleKeyboard(windowInsets, insets)

            rootView.setMargins(insets.left, insets.top, insets.right, insets.bottom)

            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
            /*return@setOnApplyWindowInsetsListener WindowInsetsCompat.Builder()
                .setInsets(currentInsetTypeMask, insets)
                .build()*/
        }
    }

    /**
     * when [isAutoResizeKeyboard] is true, [keyboardHeight] result will be 0!
     */
    private fun handleKeyboard(
        windowInsetsCompat: WindowInsetsCompat,
        currentInsets: Insets
    ) {
        val keyboardBottom = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.ime()).bottom
        val systemBottom = if (isFullScreen)
            0
        else
            windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

        val keyboardShow = windowInsetsCompat.isVisible(WindowInsetsCompat.Type.ime())
        if (keyboardShow == onKeyboardChangeLive.value?.isVisible)
            return

        if (keyboardShow) {
            keyboardHeight = abs(keyboardBottom - systemBottom /*- currentInsets.bottom*/)

            onKeyboardChangeLive.postValue(KeyboardState(true, keyboardHeight))
        } else {
            onKeyboardChangeLive.postValue(KeyboardState(false, 0))
        }
    }

    /**
     * to work correctly, [Window.setSoftInputMode] should be
     * [WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING]
     * [WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE] (for api < 30)
     */
    private fun setupSoftInput() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        rootView?.fitsSystemWindows = true
    }

    data class KeyboardState(
        val isVisible: Boolean,
        val height: Int
    )
}