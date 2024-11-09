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

        if (request.header(QUICK_CONNECT_BASE_URL) != "true") {
            return chain.proceed(request)
        }

        if (!MemoryStorage.isInvalidData(quickConnectId)) {
            val newRequest = request.transformQuickConnect(MemoryStorage.serverHostName)
            return chain.proceed(newRequest)
        }

        return chain.proceed(request.rebuildQuickConnect(quickConnectId))
    }

    private fun Request.rebuildQuickConnect(id: String): Request {
        val serverInfo = checkServerInfo(network.getServerInfo(GLOBAL_QUICK_CONNECT_URL, id), id)
        val host = "$id.${serverInfo.env!!.relayRegion!!}.$QUICK_CONNECT_BASE_URL"
        saveMemoryStorage(id, host)
        return transformQuickConnect(host)
    }

    private fun saveMemoryStorage(id: String, host: String) {
        MemoryStorage.quickConnectId = id
        MemoryStorage.serverHostName = host
    }

    private fun checkServerInfo(serverInfo: ServerInfo, id: String): ServerInfo {
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
            .addHeader("Cookie", "type=tunnel")
            .url(url).build()
    }
}