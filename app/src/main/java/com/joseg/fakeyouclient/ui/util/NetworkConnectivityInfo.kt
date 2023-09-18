package com.joseg.fakeyouclient.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

class NetworkConnectivityInfo @Inject constructor(@ApplicationContext private val context: Context) {

    val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        if (connectivityManager == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            val networks = mutableSetOf<Network>()
            override fun onAvailable(network: Network) {
                networks += network
                trySend(true)
            }

            override fun onLost(network: Network) {
                networks -= network
                trySend(false)
            }
        }

        connectivityManager.requestNetwork(networkRequest, callback)

        trySend(isCurrentlyConnected())
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.conflate()

    fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        val networkCapabilities = connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false

        return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isCurrentlyConnected(): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        val networkCapabilities =
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return when {
            VERSION.SDK_INT >= VERSION_CODES.Q -> {
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
            else -> connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}