package com.evo.security.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.nio.ByteOrder

object NetworkUtils {

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in interfaces) {
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        val hostAddress = address.hostAddress
                        if (hostAddress?.startsWith("192.168") == true ||
                            hostAddress?.startsWith("10.") == true ||
                            hostAddress?.startsWith("172.") == true) {
                            Log.d("NetworkUtils", "Found local IP: $hostAddress")
                            return hostAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error getting local IP address", e)
        }
        return null
    }

    fun getWifiIpAddress(context: Context): String? {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = wifiInfo.ipAddress

            // Convert little-endian to big-endian if needed
            val ip = if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                Integer.reverseBytes(ipAddress)
            } else {
                ipAddress
            }

            val ipString = String.format(
                "%d.%d.%d.%d",
                ip and 0xff,
                ip shr 8 and 0xff,
                ip shr 16 and 0xff,
                ip shr 24 and 0xff
            )

            Log.d("NetworkUtils", "WiFi IP: $ipString")
            return if (ipString != "0.0.0.0") ipString else null
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error getting WiFi IP address", e)
        }
        return null
    }

    // For development - you can manually set your PC's IP here
    fun getServerIpAddress(context: Context): String {
        // Manual IP for development - CHANGE THIS TO YOUR PC'S IP
        val manualIp = "192.168.0.175" // <-- Your PC's local IP address

        Log.d("NetworkUtils", "Using manual server IP: $manualIp")
        return manualIp

        // Alternative: Try automatic detection (commented out for reliability)
        /*
        // Try to get local network IP first
        getLocalIpAddress()?.let { return it }

        // Fallback to WiFi IP detection
        getWifiIpAddress(context)?.let {
            // If we're on 192.168.x.x, assume PC is on same network
            if (it.startsWith("192.168")) {
                // Replace last octet with common router gateway
                val parts = it.split(".")
                return "${parts[0]}.${parts[1]}.${parts[2]}.1"
            }
        }

        // Final fallback
        return manualIp
        */
    }
}