package com.arash.altafi.chatgptsimple.utils.liveData

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * single fire LiveData
 * [Observer]s only get new value once!
 *
 * @param <T> The type of data hold by this instance
</T> */
class VolatileLiveData<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        mPending.set(false)
        super.observe(owner) { t: T ->
            if (mPending.get()) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(value: T?) {
        mPending.set(true)
        super.setValue(value)
    }
}