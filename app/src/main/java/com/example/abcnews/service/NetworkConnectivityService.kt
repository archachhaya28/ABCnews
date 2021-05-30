package com.example.abcnews.service

import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresApi

interface NetworkConnectivityService {
    interface ConnectivityStateListener {
        fun onStateChange(state: NetworkState)
    }

    fun addListener(listener: ConnectivityStateListener)
    fun removeListener(listener: ConnectivityStateListener)

    fun getNetworkState(): NetworkState

    @Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
    sealed class NetworkState {
        class NotConnectedState: NetworkState() {
            override fun isConnected(): Boolean {
                return false
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        data class Connected(val capabilities: NetworkCapabilities) : NetworkState() {
            override fun isConnected(): Boolean {
                return capabilities.hasCapability(NET_CAPABILITY_INTERNET)
            }
        }

        @Suppress("Deprecated")
        data class ConnectedLegacy(val networkInfo: NetworkInfo) : NetworkState() {
            override fun isConnected(): Boolean {
                return networkInfo.isConnectedOrConnecting
            }
        }

        abstract fun isConnected(): Boolean
    }
}