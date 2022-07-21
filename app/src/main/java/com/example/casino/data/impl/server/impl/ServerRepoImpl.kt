package com.example.casino.data.impl.server.impl

import com.example.casino.data.business_objects.base.ResultWithStatus
import com.example.casino.data.contract.ServerRepo
import com.example.casino.data.impl.server.impl.base.BaseNetworkRepository
import com.example.casino.data.impl.server.odds_api.OddsApi
import com.example.casino.data.impl.server.odds_api.dto.MatchResponse
import com.example.casino.data.impl.server.odds_api.dto.SportResponse

class ServerRepoImpl(
    private val oddsApi: OddsApi
) : BaseNetworkRepository(), ServerRepo {

    override suspend fun getSports(): ResultWithStatus<List<SportResponse>> {
        return enqueueCallResultWithStatusSuspended(oddsApi.getSports())
    }

    override suspend fun getMatchesForSport(sportKey: String): ResultWithStatus<List<MatchResponse>> {
        return enqueueCallResultWithStatusSuspended(oddsApi.getScores(sportKey))
    }
}