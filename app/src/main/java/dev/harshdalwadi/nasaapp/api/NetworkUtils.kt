package dev.harshdalwadi.nasaapp.api

import android.content.Context
import android.net.ConnectivityManager
import java.util.*

object NetworkUtils {
    private val NETWORK_STATUS_NOT_CONNECTED = 0
    private val NETWORK_STAUS_WIFI = 1
    private val NETWORK_STATUS_MOBILE = 2
    private val TYPE_WIFI = 1
    private val TYPE_MOBILE = 2
    private val TYPE_NOT_CONNECTED = 0

    private fun getConnectivityStatus(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = Objects.requireNonNull(cm).activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI

            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    private fun getConnectivityStatusString(context: Context): Int {
        val conn = getConnectivityStatus(context)
        var status = 0
        if (conn == TYPE_WIFI) {
            status = NETWORK_STAUS_WIFI
        } else if (conn == TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED
        }
        return status
    }

    fun isNetworkConnected(context: Context): Boolean {
        return getConnectivityStatusString(context) != NETWORK_STATUS_NOT_CONNECTED
    }
}
