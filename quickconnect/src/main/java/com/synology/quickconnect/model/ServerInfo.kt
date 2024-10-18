package com.synology.quickconnect.model

import com.squareup.moshi.Json

internal data class ServerInfo(
    @Json(name = "version")
    val version: Int?,
    @Json(name = "sites")
    val sites: List<String>?,
    @Json(name = "env")
    val env: Env?,
) {
    data class Env(
        @Json(name = "relay_region")
        val relayRegion: String?
    )
}