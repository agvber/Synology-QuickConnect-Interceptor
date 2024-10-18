package com.synology.quickconnect.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ServerInfoRequestBody(
    @field:Json(name = "version")
    val version: Int = 1,
    @field:Json(name = "command")
    val command: String = "get_server_info",
    @field:Json(name = "stop_when_error")
    val stopWhenError: Boolean = false,
    @field:Json(name = "stop_when_success")
    val stopWhenSuccess: Boolean = false,
    @field:Json(name = "id")
    val id: String = "mainapp_https",
    @field:Json(name = "serverID")
    val serverID: String,
    @field:Json(name = "is_gofile")
    val isGoFile: Boolean = false
) {

    companion object {
        fun init(serverID: String) = ServerInfoRequestBody(serverID = serverID)
    }

}