package com.example.casino.data.impl.data_manager

import android.net.Uri
import android.util.Log
import com.example.casino.data.business_objects.*
import com.example.casino.data.business_objects.base.ResultStatus
import com.example.casino.data.business_objects.base.ResultWithStatus
import com.example.casino.data.contract.DataManager
import com.example.casino.data.contract.FirebaseRepo
import com.example.casino.data.contract.ServerRepo
import com.example.casino.data.impl.firebase.FirebaseConstants
import com.example.casino.data.impl.server.odds_api.dto.MatchResponse
import com.example.casino.data.impl.server.odds_api.dto.SportResponse
import com.example.casino.utils.TimeUtils
import kotlinx.coroutines.delay
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

class DataManagerImpl(
    private val serverRepo: ServerRepo,
    private val firebaseRepo: FirebaseRepo
) : DataManager {

    private val cachedMatches: MutableMap<Sport, List<Match>> = mutableMapOf()
    private val cachedLogos: MutableMap<String, Uri?> = mutableMapOf()

    override suspend fun getMatches(sport: Sport): ResultWithStatus<List<Match>> {
        val cachedMatchesForSport = cachedMatches[sport]
        if (cachedMatchesForSport != null) {
            return ResultWithStatus(cachedMatchesForSport, ResultStatus.success())
        }

        val sportKeysResult = getSportKeys(sport)
        if (sportKeysResult.isFailure()) {
            return ResultWithStatus(null, sportKeysResult.status)
        }
        if (sportKeysResult.data.isNullOrEmpty()) return ResultWithStatus(
            null, ResultStatus.failureNull()
        )
        val sportKeys = sportKeysResult.data.orEmpty()

        val matches = mutableListOf<Match>()

        val c = Calendar.getInstance()
        val endUnixMatchesQueryTime = c.timeInMillis / 1000
        c.add(Calendar.DAY_OF_YEAR, -10)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val startUnixMatchesQueryTime = c.timeInMillis / 1000

        sportKeys.forEach { sportKey ->
            var matchesForSportResult =
                getMatchesForSport(
                    sportKey,
                    startUnixMatchesTime = startUnixMatchesQueryTime,
                    endUnixMatchesTime = endUnixMatchesQueryTime
                )
            if (matchesForSportResult.isSuccess()) {
                //check if there are outdated matches that need to update

                val matchesCompletedInfoResponse = firebaseRepo.getMatchesCompletedInfo(sportKey)
                if (matchesCompletedInfoResponse.isFailure()) {
                    // something get wrong, but we have matches already,
                    // so just ignore it in this case
                } else {
                    matchesCompletedInfoResponse.data?.let { info ->
                        if (
                            (!info.hasMatchesToday && info.hasNotCompletedMatchesInPast) ||
                            (info.hasNotCompletedMatchesToday && info.isPassedEnoughTimeAfterLastMatchForSynch()) ||
                            (info.hasNotCompletedMatchesInPast && info.hasMatchesToday && !info.hasNotCompletedMatchesToday)
                        ) {
                            // synch
                            matchesForSportResult =
                                getMatchesForSport(
                                    sportKey, true, true,
                                    startUnixMatchesTime = startUnixMatchesQueryTime,
                                    endUnixMatchesTime = endUnixMatchesQueryTime
                                )
                        } else {
                            //check also if passed 2 weeks after last synch
                            val lastSynchTimeResponse =
                                firebaseRepo.lastMatchesSynchingTime(sportKey)
                            if (lastSynchTimeResponse.isSuccess()) {
                                if (lastSynchTimeResponse.data == null) {
                                    //synch
                                    matchesForSportResult =
                                        getMatchesForSport(
                                            sportKey, true, true,
                                            startUnixMatchesTime = startUnixMatchesQueryTime,
                                            endUnixMatchesTime = endUnixMatchesQueryTime
                                        )
                                } else {
                                    val cLastTime = TimeUtils.parseTime(lastSynchTimeResponse.data)
                                    val cCurrTime = Calendar.getInstance()
                                    val twoWeeks = 1000 * 60 * 60 * 24 * 7 * 2
                                    val passedTimeAfterLastMatchSynch =
                                        cCurrTime.timeInMillis - cLastTime.timeInMillis

                                    if (cCurrTime.timeInMillis > cLastTime.timeInMillis && passedTimeAfterLastMatchSynch > twoWeeks) {
                                        //synch
                                        matchesForSportResult =
                                            getMatchesForSport(
                                                sportKey, true, true,
                                                startUnixMatchesTime = startUnixMatchesQueryTime,
                                                endUnixMatchesTime = endUnixMatchesQueryTime
                                            )
                                    }
                                }
                            }
                        }
                    }
                }

                matchesForSportResult.data.orEmpty().forEach {
                    if (it.homeTeam != null && it.awayTeam != null) {
                        matches.add(
                            Match(
                                it.id,
                                it.sportTitle,
                                TimeUtils.formatDayMonthDefaultLocale(TimeUtils.parseTime(it.commenceTime)),
                                TeamInMatch(
                                    it.homeTeam,
                                    it.scores.orEmpty()
                                        .find { score -> score.name == it.homeTeam }?.score.orEmpty(),
                                    sport.getTeamLogoIcon()
                                ),
                                TeamInMatch(
                                    it.awayTeam,
                                    it.scores.orEmpty()
                                        .find { score -> score.name == it.awayTeam }?.score.orEmpty(),
                                    sport.getTeamLogoIcon()
                                )
                            )
                        )
                    }

                }
            }
        }

        cachedMatches[sport] = matches
        return ResultWithStatus(matches, ResultStatus.success())
    }

    private suspend fun getSportKeys(sport: Sport): ResultWithStatus<List<String>> {
        val sportsResult = getSports()
        if (sportsResult.isFailure()) {
            return ResultWithStatus(null, sportsResult.status)
        }
        val sportKeys = mutableListOf<String>()
        sportsResult.data?.forEach {
            if (it.group == sport.sportName) {
                sportKeys += it.key
            }
        }

        return ResultWithStatus(sportKeys.toList(), ResultStatus.success())
    }

    private suspend fun getSports(): ResultWithStatus<List<SportResponse>> {
        var sportsFromFirebaseResult = firebaseRepo.getSports()
        if (sportsFromFirebaseResult.isFailure()) return sportsFromFirebaseResult

        if (!sportsFromFirebaseResult.data.isNullOrEmpty()) {
            // firebase has saved sports
            return sportsFromFirebaseResult
        } else {
            // firebase has no saved sports
            // need load sports from api and save to firebase
            // and also prevent loading sports several times

            var isSportsSynchingNowResult = firebaseRepo.isSportsSynchingNow()
            if (isSportsSynchingNowResult.isFailure()) {
                return ResultWithStatus(null, isSportsSynchingNowResult.status)
            }
            var isSportsSynchingNow = (isSportsSynchingNowResult.data ?: false)

            if (isSportsSynchingNow) {
                // someone is synching sports now
                // wait 3 second
                delay(1000 * 3)

                isSportsSynchingNowResult = firebaseRepo.isSportsSynchingNow()
                if (isSportsSynchingNowResult.isFailure()) {
                    return ResultWithStatus(null, isSportsSynchingNowResult.status)
                }

                isSportsSynchingNow = (isSportsSynchingNowResult.data ?: false)

                if (isSportsSynchingNow) {
                    // it is still synching
                    // wait another 8 seconds for last time
                    delay(1000 * 8)
                    isSportsSynchingNowResult = firebaseRepo.isSportsSynchingNow()
                    if (isSportsSynchingNowResult.isFailure()) {
                        return ResultWithStatus(null, isSportsSynchingNowResult.status)
                    }

                    isSportsSynchingNow = (isSportsSynchingNowResult.data ?: false)
                    return if (isSportsSynchingNow) {
                        // it is still synching, too long
                        // check may be there are some problem in last synch, and other user just
                        // does not change isSportsSynchingNow variable

                        val lastSynchTimeResponse = firebaseRepo.lastSportsSynchingTime()
                        if (lastSynchTimeResponse.isFailure()) ResultWithStatus(
                            null, lastSynchTimeResponse.status
                        )

                        if (lastSynchTimeResponse.data == null) {
                            // if we have no saved last synch time, it means that synch process was interrupted in start,
                            // and we just need synch data with ourself
                            synchAndGetSports()
                        } else {
                            // check last synch time, and if it was oldest that 5 minutes
                            // it is definitely was something wrong, and synch process was not fully finished
                            val savedLastSynchTime = lastSynchTimeResponse.data.orEmpty()
                            val lastSynchTime = TimeUtils.parseTime(savedLastSynchTime)
                            val currTime = Calendar.getInstance()
                            val fiveMinutes = 1000 * 60 * 5

                            if (abs(lastSynchTime.timeInMillis - currTime.timeInMillis) > fiveMinutes) {
                                // synch process was not fully finished by other device,
                                // we need to do it with ourself
                                synchAndGetSports()
                            } else {
                                // synch process is still working on other device,
                                // but not longest allowed time so we do not reinitialising synch
                                // just return an exception that data synching too long
                                ResultWithStatus(
                                    null, ResultStatus.failure("Sports synching too long")
                                )
                            }
                        }
                    } else {
                        // someone finish synching sports, we can try to load sports again
                        sportsFromFirebaseResult = firebaseRepo.getSports()
                        if (sportsFromFirebaseResult.isFailure()) sportsFromFirebaseResult
                        else sportsFromFirebaseResult
                    }
                } else {
                    // someone finish synching sports, we can try to load sports again
                    sportsFromFirebaseResult = firebaseRepo.getSports()
                    return if (sportsFromFirebaseResult.isFailure()) sportsFromFirebaseResult
                    else sportsFromFirebaseResult
                }
            } else {
                // no one is synch, we need to synch sports
                return synchAndGetSports()
            }
        }
    }

    private suspend fun synchAndGetSports(): ResultWithStatus<List<SportResponse>> {
        val isSportsSynchingResult = firebaseRepo.setIsSportsSynchingNow(true)
        val result = if (isSportsSynchingResult.isFailure()) {
            ResultWithStatus<List<SportResponse>>(null, isSportsSynchingResult)
        } else {
            // set last synch time to firebase
            val setLastSynchTimeResult =
                firebaseRepo.setLastSportsSynchingTime(TimeUtils.getCurrentTimeStr())
            if (setLastSynchTimeResult.isFailure()) {
                ResultWithStatus<List<SportResponse>>(null, setLastSynchTimeResult)
            } else {
                // load sports from api
                val sportsFromApiResult = serverRepo.getSports()
                if (sportsFromApiResult.isFailure()) {
                    ResultWithStatus<List<SportResponse>>(null, sportsFromApiResult.status)
                } else {
                    if (sportsFromApiResult.data.isNullOrEmpty()) {
                        ResultWithStatus<List<SportResponse>>(
                            null, ResultStatus.failureNull()
                        )
                    } else {
                        val sportsFromApi = sportsFromApiResult.data.orEmpty()
                        firebaseRepo.saveSports(sportsFromApi)
                        ResultWithStatus(sportsFromApi, ResultStatus.success())
                    }
                }
            }
        }
        setIsSportsSynchingNow(false)
        return result
    }

    private suspend fun setIsSportsSynchingNow(isSynchNow: Boolean): ResultStatus {
        val isSportSynchNowResult = firebaseRepo.setIsSportsSynchingNow(isSynchNow)
        if (isSportSynchNowResult.isFailure()) {
            // just try again second time
            return firebaseRepo.setIsSportsSynchingNow(isSynchNow)
        }
        return isSportSynchNowResult
    }

    private suspend fun getMatchesForSport(
        sportKey: String,
        skipFirebase: Boolean = false,
        synchAndSetAsSynchronised: Boolean = false,
        startUnixMatchesTime: Long?,
        endUnixMatchesTime: Long?
    ): ResultWithStatus<List<MatchResponse>> {
        var matchesFromFirebaseResult: ResultWithStatus<List<MatchResponse>>? = null

        if (!skipFirebase) {
            matchesFromFirebaseResult =
                firebaseRepo.getSavedMatches(sportKey, startUnixMatchesTime, endUnixMatchesTime)
            if (matchesFromFirebaseResult.isFailure()) return matchesFromFirebaseResult
        }

        if (matchesFromFirebaseResult?.isSuccess() != true) {
            Log.d("", "")
        }

        if (!skipFirebase && matchesFromFirebaseResult?.isSuccess() == true) {
            // firebase has saved matches
            return matchesFromFirebaseResult
        } else {
            // firebase has no saved matches
            // need load matches from api and save to firebase
            // and also prevent loading matches several times

            var isMatchesSynchingNowResult = firebaseRepo.isMatchesSynchingNow(sportKey)
            if (isMatchesSynchingNowResult.isFailure()) {
                return ResultWithStatus(null, isMatchesSynchingNowResult.status)
            }
            var isMatchesSynchingNow = (isMatchesSynchingNowResult.data ?: false)

            if (isMatchesSynchingNow) {
                // someone is synching matches now
                // wait 3 second
                delay(1000 * 3)

                isMatchesSynchingNowResult = firebaseRepo.isMatchesSynchingNow(sportKey)
                if (isMatchesSynchingNowResult.isFailure()) {
                    return ResultWithStatus(null, isMatchesSynchingNowResult.status)
                }

                isMatchesSynchingNow = (isMatchesSynchingNowResult.data ?: false)

                if (isMatchesSynchingNow) {
                    // it is still synching
                    // wait another 8 seconds for last time
                    delay(1000 * 8)
                    isMatchesSynchingNowResult = firebaseRepo.isMatchesSynchingNow(sportKey)
                    if (isMatchesSynchingNowResult.isFailure()) {
                        return ResultWithStatus(null, isMatchesSynchingNowResult.status)
                    }

                    isMatchesSynchingNow = (isMatchesSynchingNowResult.data ?: false)
                    return if (isMatchesSynchingNow) {
                        // it is still synching, too long
                        // check may be there are some problem in last synch, and other user just
                        // does not change isMatchesSynchingNow variable

                        val lastSynchTimeResponse = firebaseRepo.lastMatchesSynchingTime(sportKey)
                        if (lastSynchTimeResponse.isFailure()) ResultWithStatus(
                            null, lastSynchTimeResponse.status
                        )

                        if (lastSynchTimeResponse.data == null) {
                            // if we have no saved last synch time, it means that synch process was interrupted in start,
                            // and we just need synch data with ourself
                            return synchAndGetMatches(
                                sportKey,
                                synchAndSetAsSynchronised,
                                startUnixMatchesTime,
                                endUnixMatchesTime
                            )
                        } else {
                            // check last synch time, and if it was oldest that 5 minutes
                            // it is definitely was something wrong, and synch process was not fully finished
                            val savedLastSynchTime = lastSynchTimeResponse.data.orEmpty()
                            val lastSynchTime = TimeUtils.parseTime(savedLastSynchTime)
                            val currTime = Calendar.getInstance()
                            val fiveMinutes = 1000 * 60 * 5

                            if (abs(lastSynchTime.timeInMillis - currTime.timeInMillis) > fiveMinutes) {
                                // synch process was not fully finished by other device,
                                // we need to do it with ourself
                                return synchAndGetMatches(
                                    sportKey,
                                    synchAndSetAsSynchronised,
                                    startUnixMatchesTime,
                                    endUnixMatchesTime
                                )
                            } else {
                                // synch process is still working on other device,
                                // but not longest allowed time so we do not reinitialising synch
                                // just return an exception that data synching too long
                                ResultWithStatus(
                                    null, ResultStatus.failure("Matches synching too long")
                                )
                            }
                        }
                    } else {
                        // someone finish synching matches, we can try to load it again
                        matchesFromFirebaseResult =
                            firebaseRepo.getSavedMatches(
                                sportKey,
                                startUnixMatchesTime,
                                endUnixMatchesTime
                            )
                        if (matchesFromFirebaseResult.isFailure()) matchesFromFirebaseResult
                        else matchesFromFirebaseResult
                    }
                } else {
                    // someone finish synching matches, we can try to load it again
                    matchesFromFirebaseResult =
                        firebaseRepo.getSavedMatches(
                            sportKey,
                            startUnixMatchesTime,
                            endUnixMatchesTime
                        )
                    return if (matchesFromFirebaseResult.isFailure()) matchesFromFirebaseResult
                    else matchesFromFirebaseResult
                }
            } else {
                // no one is synch, we need to synch matches
                return synchAndGetMatches(
                    sportKey,
                    synchAndSetAsSynchronised,
                    startUnixMatchesTime,
                    endUnixMatchesTime
                )
            }
        }
    }

    private suspend fun synchAndGetMatches(
        sportKey: String,
        synchAndSetAsSynchronised: Boolean,
        startUnixMatchesTime: Long?,
        endUnixMatchesTime: Long?
    ): ResultWithStatus<List<MatchResponse>> {
        val isMatchesSynchingResult = setIsMatchesSynchingNow(sportKey, true)
        val result = if (isMatchesSynchingResult.isFailure()) {
            ResultWithStatus<List<MatchResponse>>(null, isMatchesSynchingResult)
        } else {
            // set last synch time to firebase
            val setLastSynchTimeResult =
                firebaseRepo.setLastMatchesSynchingTime(sportKey, TimeUtils.getCurrentTimeStr())
            if (setLastSynchTimeResult.isFailure()) {
                ResultWithStatus<List<MatchResponse>>(null, setLastSynchTimeResult)
            } else {
                // load matches from api
                val matchesFromApiResult = serverRepo.getMatchesForSport(sportKey)
                if (matchesFromApiResult.isFailure()) {
                    ResultWithStatus<List<MatchResponse>>(null, matchesFromApiResult.status)
                } else {
                    val matchesFromApi = matchesFromApiResult.data.orEmpty()
                    if (synchAndSetAsSynchronised) {
                        matchesFromApi.forEach {
                            it.synchronised = true
                        }
                    }
                    var filteredMatches = listOf<MatchResponse>()
                    if (startUnixMatchesTime != null && endUnixMatchesTime != null) {
                        filteredMatches = matchesFromApi.filter {
                            it.commenceTime >= startUnixMatchesTime &&
                                    it.commenceTime <= endUnixMatchesTime
                        }
                    }
                    firebaseRepo.saveMatches(matchesFromApi)
                    if (filteredMatches.isNotEmpty()) {
                        ResultWithStatus(filteredMatches, ResultStatus.success())
                    } else {
                        ResultWithStatus(matchesFromApi, ResultStatus.success())
                    }

                }
            }
        }
        setIsMatchesSynchingNow(sportKey, false)
        return result
    }

    private suspend fun setIsMatchesSynchingNow(
        sportKey: String,
        isSynchNow: Boolean
    ): ResultStatus {
        val isMatchesSynchNowResult = firebaseRepo.setIsMatchesSynchingNow(sportKey, isSynchNow)
        if (isMatchesSynchNowResult.isFailure()) {
            // just try again second time
            return firebaseRepo.setIsMatchesSynchingNow(sportKey, isSynchNow)
        }
        return isMatchesSynchNowResult
    }

    override fun getSlots(): List<Slot> {
        return SlotsGamesAndRoulettes.getSlots()
    }

    override fun getTableGames(): List<TableGame> {
        return SlotsGamesAndRoulettes.getTableGames()
    }

    override fun getRoulettes(): List<Roulette> {
        return SlotsGamesAndRoulettes.getRoulettes()
    }
}