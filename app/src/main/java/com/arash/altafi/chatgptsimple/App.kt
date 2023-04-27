package com.arash.altafi.chatgptsimple

import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.arash.altafi.chatgptsimple.domain.provider.local.ObjectBox
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : MultiDexApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        ObjectBox.init(this)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
    .setWorkerFactory(workerFactory)
    .build()

}