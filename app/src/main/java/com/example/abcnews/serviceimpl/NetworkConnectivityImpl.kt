package com.example.abcnews.serviceimpl

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.abcnews.service.NetworkConnectivityService

@RequiresApi(Build.VERSION_CODES.N)
class NetworkConnectivityImpl(private val cm: ConnectivityManager) : NetworkConnectivityBaseImpl() {

    private val networkCallback = ConnectivityCallback()

    override fun subscribe() {
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    override fun unsubscribe() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    override fun getNetworkState(): NetworkConnectivityService.NetworkState {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return if (capabilities != null) {
            NetworkConnectivityService.NetworkState.Connected(capabilities)
        } else {
            NetworkConnectivityService.NetworkState.NotConnectedState()
        }
    }

    private inner class ConnectivityCallback : NetworkCallback() {

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            dispatchChange(NetworkConnectivityService.NetworkState.Connected(capabilities))
        }

        override fun onLost(network: Network) {
            dispatchChange(NetworkConnectivityService.NetworkState.NotConnectedState())
        }
    }
}