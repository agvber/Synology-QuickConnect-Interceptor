package com.synology.quickconnect

internal object MemoryStorage {

    var quickConnectId: String = ""
    var serverHostName: String = ""

    fun isInvalidData(id: String): Boolean {
        return id != quickConnectId && quickConnectId.isBlank() && serverHostName.isBlank()
    }

}