package com.arash.altafi.chatgptsimple.ext

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.arash.altafi.chatgptsimple.databinding.LayoutToastBinding
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.Locale
import java.util.concurrent.TimeUnit
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

fun Fragment.toast(msg: String) {
    requireContext().toast(msg)
}

fun Context.toastCustom(text: String, @DrawableRes icon: Int? = null, theView: View? = null) {
    val viewBinding = LayoutToastBinding.inflate(LayoutInflater.from(this)).apply {
        tv.text = text
        icon?.let {
            tv.setDrawable(icon, 0, 0, 0)
        }
    }

    Toast(this).apply {

        theView?.let {
            val absoluteLocation = IntArray(2)
            theView.getLocationInWindow(absoluteLocation)
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 200)
        } ?: run {
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 200)
        }

        view = viewBinding.root
        duration = Toast.LENGTH_SHORT
        show()
    }
}

fun Fragment.toastCustom(text: String, @DrawableRes icon: Int? = null, view: View? = null) =
    requireContext().toastCustom(text, icon, view)

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

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    this.requestFocus()
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    } catch (e: java.lang.Exception) {
        Log.e("showKeyboard", "showKeyboard failed, error: $e")
    }
}

fun Activity.isDarkTheme(): Boolean {
    return this.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        requestLayout()
    }
}

fun Context.copyTextToClipboard(textToCopy: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", textToCopy)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(this, "Text copied", Toast.LENGTH_LONG).show()
}

fun Context.shareContent(contentValue: String) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        contentValue
    )
    startActivity(Intent.createChooser(shareIntent, "Send to"))
}

fun <F> runAfter(
    delay: Long, total: Long, fn: (Long) -> F, fc: () -> F,
    unit: TimeUnit = TimeUnit.MILLISECONDS
): Disposable {
    return Flowable.interval(0, delay, unit)
        .observeOn(AndroidSchedulers.mainThread())
        .takeWhile { it != total }
        .doOnNext { fn(it) }
        .doOnComplete { fc() }
        .subscribe()
}

fun <F> runAfter(
    delay: Long, fx: () -> F, unit: TimeUnit = TimeUnit.MILLISECONDS
): Disposable {
    return Completable.timer(delay, unit)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete { fx() }
        .subscribe()
}

fun String.applyValue(vararg args: Any?): String {
    return String.format(Locale.US, this, *args)
}