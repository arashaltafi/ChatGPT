package com.arash.altafi.chatgptsimple.utils

import android.app.Application
import android.util.Log
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import io.sentry.android.fragment.FragmentLifecycleIntegration

object CrashlyticsUtils {

    fun setupCrashlytics(
        application: Application,
        isDebug: Boolean,
        versionName: String,
        versionCode: Int
    ) {
        SentryAndroid.init(application) { options ->
            options.dsn =
                "https://97cbe673747c4114af131d75acf0edfe@o1126595.ingest.sentry.io/4505098386604032"
            if (isDebug) {
                options.isDebug = true
                options.environment = "DEBUG"

                // enable "performance monitoring"
                options.tracesSampleRate = 1.0

                options.addIntegration(
                    FragmentLifecycleIntegration(
                        application,
                        enableFragmentLifecycleBreadcrumbs = true, // enabled by default
                        enableAutoFragmentLifecycleTracing = true  // disabled by default
                    )
                )
            } else {
                options.isDebug = false
                options.environment = "PRODUCTION"
                options.release = "$versionName - ($versionCode)"

                options.setDiagnosticLevel(SentryLevel.INFO)

                // enable "performance monitoring", lower pressure!
                options.tracesSampleRate = 0.2

                options.addIntegration(
                    FragmentLifecycleIntegration(
                        application,
                        enableFragmentLifecycleBreadcrumbs = true, // enabled by default
                        enableAutoFragmentLifecycleTracing = false  // disabled by default
                    )
                )
            }

            /*options.beforeSend =
                SentryOptions.BeforeSendCallback { event: SentryEvent, hint: Any? ->

                }*/
        }
    }

    fun captureException(e: Throwable, category: String? = null, message: String? = null) {
        e.printStackTrace()
        Log.e("TAG", e.stackTraceToString())

        Breadcrumb().apply {
            setCategory(category)
            setMessage(message)
        }

        Sentry.captureException(e)
    }
}