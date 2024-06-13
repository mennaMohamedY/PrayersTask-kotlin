package com.example.prayers_task.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NetworkConnectivityObserverImpl(
    private val context: Context
) :CheckConnectivityObserver{
    private val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    @RequiresApi(Build.VERSION_CODES.N)
    override fun observe(): Flow<CheckConnectivityObserver.Status> {
        return callbackFlow {

            val callback=object :ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch {
                        send(CheckConnectivityObserver.Status.Available)
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch {
                        send(CheckConnectivityObserver.Status.UnAvailable)
                    }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose{
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}