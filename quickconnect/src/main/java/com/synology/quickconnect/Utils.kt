package com.synology.quickconnect

import com.squareup.moshi.Moshi
import okhttp3.Request
import okhttp3.Response

internal const val GLOBAL_QUICK_CONNECT_URL = "https://global.quickconnect.to/Serv.php"
internal const val QUICK_CONNECT_BASE_URL = "quickconnect.to"
internal const val PING_PONG_URL = "/webman/pingpong.cgi?action=cors&quickconnect=true"
internal const val JSON_MIM_TYPE = "application/json"

private const val COOKE_HEADER = "cookie"
private const val TUNNEL_TYPE_HEADER = "type=tunnel"

internal fun <T> Response.toDto(moshi: Moshi, type: Class<T>): T? {
    return moshi
        .adapter(type)
        .lenient()
        .fromJson(body()?.source() ?: return null)
}

internal fun Request.Builder.addTunnelTypeHeader(): Request.Builder {
    return addHeader(COOKE_HEADER, TUNNEL_TYPE_HEADER)
}