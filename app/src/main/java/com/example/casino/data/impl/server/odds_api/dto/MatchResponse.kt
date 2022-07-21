package com.example.casino.data.impl.server.odds_api.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class MatchResponse {
    val id: String = ""

    @SerializedName("sport_key")
    val sportKey: String = ""

    @SerializedName("sport_title")
    val sportTitle: String = ""

    @SerializedName("commence_time")
    val commenceTime: Long = 0

    val completed: Boolean = false

    var synchronised: Boolean = false

    @SerializedName("home_team")
    val homeTeam: String? = null

    @SerializedName("away_team")
    val awayTeam: String? = null

    val scores: List<Score>? = null

    @SerializedName("last_update")
    val lastUpdate: String? = null

    @Parcelize
    class Score : Parcelable {
        val name: String = ""
        val score: String = ""
    }
}
