package com.synology.quickconnect

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.synology.quickconnect.model.ServerInfo
import com.synology.quickconnect.model.ServerInfoRequestBody
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class QuickConnectInterceptor : Interceptor {

    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val quickConnectId = request.url().host()

        if (request.header(QUICK_CONNECT_HEADER) != "true") {
            return chain.proceed(request)
        }

        if (!MemoryStorage.isInvalidData(quickConnectId)) {
            val newRequest = request.transformQuickConnect(MemoryStorage.serverHostName)
            return chain.proceed(newRequest)
        }

        return chain.proceed(request.rebuildQuickConnect(quickConnectId))
    }

    private fun Request.rebuildQuickConnect(id: String): Request {
        val globalQuickConnectRequest = buildServerInfoRequest(id, GLOBAL_QUICK_CONNECT_URL)
        val serverInfo = checkServerInfo(getServerInfo(globalQuickConnectRequest), id)
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
            val request = buildServerInfoRequest(
                id = id,
                baseUrl = "https://${serverInfo.sites!!.first()}/Serv.php"
            )
            return getServerInfo(request)
        }
        return serverInfo
    }

    private fun getServerInfo(request: Request): ServerInfo {
        val result: ServerInfo
        client.newCall(request).execute().use {
            result = moshi
                .adapter(ServerInfo::class.java)
                .lenient()
                .fromJson(it.body()!!.source())!!
        }
        return result
    }

    private fun buildServerInfoRequest(id: String, baseUrl: String): Request =
        Request.Builder()
            .url(baseUrl)
            .post(
                RequestBody.create(
                    MediaType.get(JSON_MIM_TYPE),
                    moshi.adapter(ServerInfoRequestBody::class.java)
                        .toJson(ServerInfoRequestBody.init(id))
                )
            )
            .build()

    private fun Request.transformQuickConnect(host: String): Request {
        val url = url().newBuilder()
            .host(host)
            .build()
        return newBuilder()
            .addHeader("Cookie", "type=tunnel")
            .url(url).build()
    }

    companion object {
        const val QUICK_CONNECT_HEADER = "synology_quick_connect_id"

        private const val GLOBAL_QUICK_CONNECT_URL = "https://global.quickconnect.to/Serv.php"
        private const val QUICK_CONNECT_BASE_URL = "quickconnect.to"
        private const val JSON_MIM_TYPE = "application/json"
    }
}