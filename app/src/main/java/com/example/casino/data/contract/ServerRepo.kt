package com.example.casino.data.contract

import com.example.casino.data.business_objects.base.ResultWithStatus
import com.example.casino.data.impl.server.odds_api.dto.MatchResponse
import com.example.casino.data.impl.server.odds_api.dto.SportResponse

interface ServerRepo {
    suspend fun getSports(): ResultWithStatus<List<SportResponse>>
    suspend fun getMatchesForSport(sportKey: String): ResultWithStatus<List<MatchResponse>>
}
