package com.synology.quickconnect.model

import com.squareup.moshi.Json

data class QuickConnectPingPong(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "szid")
    val eZid: String,
)
