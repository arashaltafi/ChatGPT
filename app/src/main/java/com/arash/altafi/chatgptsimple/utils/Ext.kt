package com.arash.altafi.chatgptsimple.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import kotlin.math.roundToInt

fun View.toShow() {
    this.visibility = View.VISIBLE
}

fun View.isShow(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.toHide() {
    this.visibility = View.INVISIBLE
}

fun View.isHide(): Boolean {
    return this.visibility == View.INVISIBLE
}

fun View.toGone() {
    this.visibility = View.GONE
}

fun View.isGone(): Boolean {
    return this.visibility == View.GONE
}


fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}


fun Int.toPx(): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return (this * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Int.toDp(): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return (this / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Float.toPx(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
}

fun TextView.setDrawable(
    start: Int = 0, end: Int = 0,
    top: Int = 0, bottom: Int = 0
) {
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(
        start, top,
        end, bottom
    )
}

fun TextView.setDrawableStart(res: Int) {
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(
        res, 0,
        0, 0
    )
}

fun TextView.setDrawableEnd(res: Int) {
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(
        0, 0,
        res, 0
    )
}

@ColorInt
fun Context.getAttrColor(@AttrRes attrID: Int): Int {
    val typedValue = TypedValue()
    val theme = this.theme
    theme.resolveAttribute(attrID, typedValue, true)
    return typedValue.data
}

fun Context.getAttr(attrID: Int): Int {
    val typedValue = TypedValue()
    val theme = this.theme
    theme.resolveAttribute(attrID, typedValue, true)
    return typedValue.data
}

fun Context.getDrawableCompat(res: Int): VectorDrawableCompat? {
    return VectorDrawableCompat.create(resources, res, theme)
}

fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels
fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels