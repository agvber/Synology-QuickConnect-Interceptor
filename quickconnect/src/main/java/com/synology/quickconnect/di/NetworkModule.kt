package com.synology.quickconnect.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.synology.quickconnect.network.SynologyNetwork
import okhttp3.OkHttpClient

internal class NetworkModule {

    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun getSynologyNetwork(): SynologyNetwork {
        return SynologyNetwork(client, moshi)
    }
}