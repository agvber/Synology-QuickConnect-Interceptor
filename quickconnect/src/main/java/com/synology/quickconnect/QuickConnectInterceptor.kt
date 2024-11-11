package com.synology.quickconnect

import com.synology.quickconnect.di.NetworkModule
import com.synology.quickconnect.model.ServerInfo
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class QuickConnectInterceptor : Interceptor {

    private val networkModule = NetworkModule()
    private val network = networkModule.getSynologyNetwork()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val quickConnectId = request.url().host()

        if (request.header(QUICK_CONNECT_HEADER) != "true") {
            val newRequest = request.newBuilder().removeHeader(QUICK_CONNECT_HEADER).build()
            return chain.proceed(newRequest)
        }

        if (!MemoryStorage.isInvalidData(quickConnectId)) {
            val newRequest = request.transformQuickConnect(MemoryStorage.serverHostName)
            return chain.proceed(newRequest)
        }

        val quickConnectUrl = getQuickConnectUrl(quickConnectId)
        saveMemoryStorage(quickConnectId, quickConnectUrl)
        return chain.proceed(request.transformQuickConnect(quickConnectUrl))
    }

    private fun getQuickConnectUrl(id: String): String {
        val serverInfo = getServerInfo(id)
        val baseUrl = "$id.${serverInfo.env!!.relayRegion}.$QUICK_CONNECT_BASE_URL"

        network.pinPongQuickConnectUrl("https://${baseUrl}" + PING_PONG_URL)
        return baseUrl
    }

    private fun saveMemoryStorage(id: String, host: String) {
        MemoryStorage.quickConnectId = id
        MemoryStorage.serverHostName = host
    }

    private fun getServerInfo(id: String): ServerInfo {
        val serverInfo = network.getServerInfo(GLOBAL_QUICK_CONNECT_URL, id)

        if (serverInfo.env?.relayRegion == null) {
            return network.getServerInfo("https://${serverInfo.sites!!.first()}/Serv.php", id)
        }
        return serverInfo
    }

    private fun Request.transformQuickConnect(host: String): Request {
        val url = url().newBuilder()
            .host(host)
            .build()
        return newBuilder()
            .addTunnelTypeHeader()
            .url(url)
            .removeHeader(QUICK_CONNECT_HEADER)
            .build()
    }

    companion object {
        const val QUICK_CONNECT_HEADER = "synology_quick_connect_id"
    }
}