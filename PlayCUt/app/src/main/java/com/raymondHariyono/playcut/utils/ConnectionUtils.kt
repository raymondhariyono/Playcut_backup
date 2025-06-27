// File: app/src/main/java/com/raymondHariyono/playcut/utils/ConnectionUtils.kt
package com.raymondHariyono.playcut.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object ConnectionUtils {
    fun isOnline(context: Context): Boolean {
        // Dapatkan layanan sistem untuk konektivitas
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}