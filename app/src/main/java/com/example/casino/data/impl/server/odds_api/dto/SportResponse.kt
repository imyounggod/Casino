package com.example.casino.data.impl.server.odds_api.dto

import com.google.gson.annotations.SerializedName

class SportResponse {
    val key: String = ""
    val group: String = ""
    val title: String = ""
    val description: String = ""
    val active: Boolean = false
    @SerializedName("has_outrights")
    val hasOutRights: Boolean = false
}
