package com.example.casino.data.contract

import com.example.casino.data.business_objects.MatchesCompletedInfo
import com.example.casino.data.business_objects.base.ResultStatus
import com.example.casino.data.business_objects.base.ResultWithStatus
import com.example.casino.data.impl.server.odds_api.dto.MatchResponse
import com.example.casino.data.impl.server.odds_api.dto.SportResponse

interface FirebaseRepo {
    suspend fun getSports(): ResultWithStatus<List<SportResponse>>
    suspend fun saveSports(sports: List<SportResponse>): ResultStatus
    suspend fun isSportsSynchingNow(): ResultWithStatus<Boolean>
    suspend fun setIsSportsSynchingNow(isSportsSynchingNow: Boolean): ResultStatus
    suspend fun lastSportsSynchingTime(): ResultWithStatus<String>
    suspend fun setLastSportsSynchingTime(time: String): ResultStatus

    suspend fun getSavedMatches(
        sportKey: String,
        startTime: Long?,
        endTime: Long?
    ): ResultWithStatus<List<MatchResponse>>

    suspend fun saveMatches(matches: List<MatchResponse>): ResultStatus
    suspend fun isMatchesSynchingNow(sportKey: String): ResultWithStatus<Boolean>
    suspend fun setIsMatchesSynchingNow(
        sportKey: String,
        isMatchesSynchingNow: Boolean
    ): ResultStatus

    suspend fun lastMatchesSynchingTime(sportKey: String): ResultWithStatus<String>
    suspend fun setLastMatchesSynchingTime(sportKey: String, time: String): ResultStatus

    suspend fun getMatchesCompletedInfo(sportKey: String): ResultWithStatus<MatchesCompletedInfo>
}
