package com.example.abcnews

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.example.abcnews.service.NetworkConnectivityLegacyImpl
import com.example.abcnews.service.NetworkConnectivityService
import com.example.abcnews.service.ServiceFinder
import com.example.abcnews.serviceimpl.NetworkConnectivityImpl

class ABCNewsApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        initServiceFinder()
    }

    private fun initServiceFinder() {
        ServiceFinder.networkService = createNetworkConnectivityProvider(this)
    }

    private fun createNetworkConnectivityProvider(context: Context): NetworkConnectivityService {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkConnectivityImpl(cm)
        } else {
            NetworkConnectivityLegacyImpl(context, cm)
        }
    }
}