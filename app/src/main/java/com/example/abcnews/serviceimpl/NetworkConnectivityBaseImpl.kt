package com.example.abcnews.serviceimpl

import android.os.Handler
import android.os.Looper
import com.example.abcnews.service.NetworkConnectivityService

abstract class NetworkConnectivityBaseImpl : NetworkConnectivityService {
    private val handler = Handler(Looper.getMainLooper())
    private val listeners = mutableSetOf<NetworkConnectivityService.ConnectivityStateListener>()
    private var subscribed = false

    override fun addListener(listener: NetworkConnectivityService.ConnectivityStateListener) {
        listeners.add(listener)
        listener.onStateChange(getNetworkState()) // propagate an initial state
        verifySubscription()
    }

    override fun removeListener(listener: NetworkConnectivityService.ConnectivityStateListener) {
        listeners.remove(listener)
        verifySubscription()
    }

    private fun verifySubscription() {
        if (!subscribed && listeners.isNotEmpty()) {
            subscribe()
            subscribed = true
        } else if (subscribed && listeners.isEmpty()) {
            unsubscribe()
            subscribed = false
        }
    }

    protected fun dispatchChange(state: NetworkConnectivityService.NetworkState) {
        handler.post {
            for (listener in listeners) {
                listener.onStateChange(state)
            }
        }
    }

    protected abstract fun subscribe()
    protected abstract fun unsubscribe()
}