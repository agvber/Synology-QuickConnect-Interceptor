package com.synology.quickconnect.network

import com.squareup.moshi.Moshi
import com.synology.quickconnect.JSON_MIM_TYPE
import com.synology.quickconnect.model.QuickConnectPingPong
import com.synology.quickconnect.model.ServerInfo
import com.synology.quickconnect.model.ServerInfoRequestBody
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

internal class SynologyNetwork(
    private val client: OkHttpClient,
    private val moshi: Moshi
) {

    fun getServerInfo(
        url: String,
        quickConnectId: String
    ): ServerInfo {
        val request = Request.Builder()
            .url(url)
            .post(
                RequestBody.create(
                    MediaType.get(JSON_MIM_TYPE),
                    moshi.adapter(ServerInfoRequestBody::class.java)
                        .toJson(ServerInfoRequestBody.init(quickConnectId))
                )
            )
            .build()

        val response: ServerInfo
        client.newCall(request).execute().use {
            response = it.toDto(moshi, ServerInfo::class.java)!!
        }

        return response
    }

    fun pinPongQuickConnectUrl(
        url: String
    ): QuickConnectPingPong {
        val request = Request.Builder().url(url).build()
        val response: QuickConnectPingPong

        client.newCall(request).execute().use {
            response = it.toDto(moshi, QuickConnectPingPong::class.java)!!
        }

        return response
    }

    private fun <T> Response.toDto(moshi: Moshi, type: Class<T>): T? {
        return moshi
            .adapter(type)
            .lenient()
            .fromJson(body()?.source() ?: return null)
    }

}