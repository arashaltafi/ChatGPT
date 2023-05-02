package com.arash.altafi.chatgptsimple.utils

import android.content.Context
import android.graphics.Point
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.core.view.ViewCompat
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.LayoutPopupMenuBinding
import com.arash.altafi.chatgptsimple.ext.getScreenHeight
import com.arash.altafi.chatgptsimple.ext.getScreenWidth
import com.arash.altafi.chatgptsimple.ext.setDrawableStart
import com.arash.altafi.chatgptsimple.ext.toPx
import com.google.android.material.textview.MaterialTextView

object PopupUtil {

    private fun preparePopup(context: Context, popupItem: List<PopupItem>): PopupWindow {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = LayoutPopupMenuBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            popupView.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        attachItems(popupWindow, popupView, popupItem)

        popupWindow.apply {
            animationStyle = R.style.animation_popup_window
            isOutsideTouchable = true
            isTouchable = true
            isFocusable = true
        }

        popupView.root.measure(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.width = popupView.root.measuredWidth
        popupWindow.height = popupView.root.measuredHeight

        return popupWindow

    }

    private fun attachItems(
        popupWindow: PopupWindow,
        popupView: LayoutPopupMenuBinding,
        popupItem: List<PopupItem>
    ) =
        popupItem.forEachIndexed { index, item ->
            val tv = MaterialTextView(popupView.root.context, null, R.style.txt_h2_medium)
                .apply {
                    text = item.text
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setPadding(
                            8.toPx(),
                            if (index == 0) 16.toPx() else 4.toPx(),
                            8.toPx(),
                            if (index == popupItem.lastIndex) 16.toPx() else 4.toPx(),
                        )
                    }
                    compoundDrawablePadding = 16.toPx()
                    ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LOCALE)

                    setDrawableStart(item.icon)

                    val outValue = TypedValue()
                    context.theme.resolveAttribute(
                        android.R.attr.selectableItemBackground,
                        outValue,
                        true
                    )
                    setBackgroundResource(outValue.resourceId)

                    setOnClickListener {
                        item.onclick.invoke()
                        popupWindow.dismiss()
                    }
                }


            popupView.ll.addView(tv)
        }

    private fun dimScreen(context: Context, popupWindow: PopupWindow) {
        //dim behind
        val container = popupWindow.contentView.rootView
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags =
            p.flags or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or WindowManager.LayoutParams.FLAG_DIM_BEHIND
//    p.screenBrightness = 0.1f.also { p.dimAmount = it }
        p.dimAmount = 0.2f
        wm.updateViewLayout(container, p)
    }

    private fun correctY(currentY: Int, popupWindow: PopupWindow): Int {
        var result = currentY

        val bottomScreenOffset = getScreenHeight() - currentY

        if (bottomScreenOffset < popupWindow.height) {//getMaxAvailableHeight() not work properly
            result -= popupWindow.height
        }

        return result
    }

    private fun correctX(currentX: Int, popupWindow: PopupWindow): Int {
        var result = currentX

        val endScreenOffset = getScreenWidth() - currentX

        if (endScreenOffset < popupWindow.width) {
            result = (result - popupWindow.width + endScreenOffset) - 10
        }

        return result
    }

    fun showPopup(onView: View, popupItem: List<PopupItem>, pointOnScreen: Point) {
        val context = onView.context
        preparePopup(context, popupItem).apply {
            val x = correctX(pointOnScreen.x, this)
            val y = correctY(pointOnScreen.y, this)

            showAtLocation(onView, Gravity.NO_GRAVITY, x, y)

            dimScreen(context, this)
        }
    }

    fun showPopup(onView: View, popupItem: List<PopupItem>, gravity: Int) {
        val context = onView.context

        preparePopup(context, popupItem).apply {
            showAsDropDown(onView, 0, 0, gravity)

            dimScreen(context, this)
        }
    }

    @Keep
    data class PopupItem(
        @DrawableRes
        val icon: Int,
        val text: String,
        val onclick: () -> Unit
    )
}