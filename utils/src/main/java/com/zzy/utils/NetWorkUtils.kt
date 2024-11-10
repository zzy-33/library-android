package com.zzy.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration

/**
 *    author : Jordan
 *    time   : 2024/04/17
 *    desc   : 网络相关工具类
 */
class NetWorkUtils {

    /**
     * 获取局域网的网关地址
     */
    fun getGatewayIP(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.dhcpInfo
        val gateway = info.gateway
        return intToIp(gateway)
    }

    /**
     * 获取wifi ip地址
     */
    fun getIPAddress(context: Context): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return intToIp(wifiInfo.ipAddress)
    }

    /**
     * 获取内网ip地址
     */
    fun getIPAddress(): String {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }


    /**
     * 获取mac地址
     */
    fun getMacAddress(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.macAddress
    }

    fun intToIp(ipInt: Int): String {
        if (ipInt == 0) return ""
        return try {
            val sb = StringBuilder()
            sb.append(ipInt and 0xFF).append('.')
            sb.append(ipInt shr 8 and 0xFF).append('.')
            sb.append(ipInt shr 16 and 0xFF).append('.')
            sb.append(ipInt shr 24 and 0xFF)
            sb.toString()
        } catch (e: Exception) {
            ""
        }
    }

}