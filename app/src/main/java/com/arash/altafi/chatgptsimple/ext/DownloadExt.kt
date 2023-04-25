package com.arash.altafi.chatgptsimple.ext

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

fun Context.openDownloadURL(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    when {
        this.isInstalled("com.android.chrome") -> intent.setPackage("com.android.chrome")
        this.isInstalled("org.mozilla.firefox") -> intent.setPackage("org.mozilla.firefox")
        this.isInstalled("com.opera.mini.android") -> intent.setPackage("com.opera.mini.android")
        this.isInstalled("com.opera.mini.android.Browser") -> intent.setPackage("com.opera.mini.android.Browser")
        else -> this.openURL(url)
    }
    startActivity(intent)
}

private fun Context.isInstalled(packageName: String): Boolean {
    return try {
        this.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.openURL(url: String) {
    try {
        val fullUrl = if (url.startsWith("http")) url else "http://$url"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("Browser Not Found!")
    }
}