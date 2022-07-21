package com.example.casino.data.impl.server.odds_api

import com.example.casino.data.impl.server.odds_api.dto.MatchResponse
import com.example.casino.data.impl.server.odds_api.dto.SportResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OddsApi {
    @GET(ApiConstants.ENDPOINT_SPORTS)
    fun getSports(@Query("all") all: Boolean = true): Call<List<SportResponse>>

    @GET(ApiConstants.ENDPOINT_SCORES)
    fun getScores(@Path("sport_key") sportKey: String,
                  @Query("dateFormat") dateFormat: String = "unix",
                  @Query("daysFrom") daysFrom: Int = 3): Call<List<MatchResponse>>
}
