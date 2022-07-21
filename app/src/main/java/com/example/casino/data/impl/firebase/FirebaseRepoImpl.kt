package com.example.casino.data.impl.firebase

import android.text.format.DateUtils
import com.example.casino.data.business_objects.MatchesCompletedInfo
import com.example.casino.data.business_objects.base.ResultStatus
import com.example.casino.data.business_objects.base.ResultWithStatus
import com.example.casino.data.contract.FirebaseRepo
import com.example.casino.data.impl.server.odds_api.dto.MatchResponse
import com.example.casino.data.impl.server.odds_api.dto.SportResponse
import com.example.casino.utils.TimeUtils
import com.google.firebase.firestore.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseRepoImpl(
    private val db: FirebaseFirestore
) : FirebaseRepo {

    //region FirebaseRepo methods implementation

    override suspend fun getSports(): ResultWithStatus<List<SportResponse>> {
        return getObjects(getSportsColRef())
    }

    override suspend fun saveSports(sports: List<SportResponse>): ResultStatus {
        return saveAllObjects(sports, ::saveSport)
    }

    private suspend fun saveSport(sport: SportResponse): ResultStatus {
        return saveObject(getSportsColRef(), sport.key, sport)
    }

    override suspend fun isSportsSynchingNow(): ResultWithStatus<Boolean> {
        return getPropertyFromDocument(
            getSportSynchDocRef(),
            FirebaseConstants.PROPERTY_SPORTS_IS_SYNCHING,
            false
        )
    }

    override suspend fun setIsSportsSynchingNow(isSportsSynchingNow: Boolean): ResultStatus {
        val documentData =
            mapOf(FirebaseConstants.PROPERTY_SPORTS_IS_SYNCHING to isSportsSynchingNow)
        return saveData(getSportSynchDocRef(), documentData)
    }

    override suspend fun lastSportsSynchingTime(): ResultWithStatus<String> {
        return getPropertyFromDocument(
            getSportSynchDocRef(),
            FirebaseConstants.PROPERTY_LAST_SPORTS_SYNCHING_TIME,
            null
        )
    }

    override suspend fun setLastSportsSynchingTime(time: String): ResultStatus {
        val documentData = mapOf(FirebaseConstants.PROPERTY_LAST_SPORTS_SYNCHING_TIME to time)
        return saveData(getSportSynchDocRef(), documentData)
    }

    override suspend fun getSavedMatches(
        sportKey: String,
        startTime: Long?,
        endTime: Long?
    ): ResultWithStatus<List<MatchResponse>> {
        return suspendCoroutine { continuation ->
            val collRef = getMatchesColRef(sportKey)
            collRef.let {
                var q: Query = collRef.whereNotEqualTo("commenceTime", 0)
                if (startTime != null) {
                    q = q.whereGreaterThanOrEqualTo("commenceTime", startTime)
                }
                if (endTime != null) {
                    q = q.whereLessThanOrEqualTo("commenceTime", endTime)
                }
                q
            }
                .get()
                .addOnSuccessListener { documents ->
                    val objects = mutableListOf<MatchResponse>()
                    for (document in documents) {
                        objects.add(document.toObject(MatchResponse::class.java))
                    }
                    continuation.resume(ResultWithStatus(objects.toList(), ResultStatus.success()))
                }.addOnFailureListener { e ->
                    continuation.resume(ResultWithStatus(null, ResultStatus.failure(e)))
                }
        }
    }

    override suspend fun saveMatches(matches: List<MatchResponse>): ResultStatus {
        return saveAllObjects(matches, ::saveMatch)
    }

    private suspend fun saveMatch(match: MatchResponse): ResultStatus {
        return saveObject(getMatchesColRef(match.sportKey), match.id, match)
    }

    override suspend fun isMatchesSynchingNow(sportKey: String): ResultWithStatus<Boolean> {
        return getPropertyFromDocument(
            getMatchesSynchDocRef(sportKey),
            FirebaseConstants.PROPERTY_MATCHES_IS_SYNCHING,
            false
        )
    }

    override suspend fun setIsMatchesSynchingNow(
        sportKey: String,
        isMatchesSynchingNow: Boolean
    ): ResultStatus {
        val documentData =
            mapOf(FirebaseConstants.PROPERTY_MATCHES_IS_SYNCHING to isMatchesSynchingNow)
        return saveData(getMatchesSynchDocRef(sportKey), documentData)
    }

    override suspend fun lastMatchesSynchingTime(sportKey: String): ResultWithStatus<String> {
        return getPropertyFromDocument(
            getMatchesSynchDocRef(sportKey),
            FirebaseConstants.PROPERTY_MATCHES_LAST_SYNCHING_TIME,
            null
        )
    }

    override suspend fun setLastMatchesSynchingTime(sportKey: String, time: String): ResultStatus {
        val documentData = mapOf(FirebaseConstants.PROPERTY_MATCHES_LAST_SYNCHING_TIME to time)
        return saveData(getMatchesSynchDocRef(sportKey), documentData)
    }

    override suspend fun getMatchesCompletedInfo(sportKey: String): ResultWithStatus<MatchesCompletedInfo> {
        val allMatchesForSportResult = getSavedMatches(sportKey, null, null)
        if (allMatchesForSportResult.isFailure()) return ResultWithStatus(
            null,
            allMatchesForSportResult.status
        )

        val hasMatchesToday = allMatchesForSportResult.data.orEmpty()
            .filter { !it.synchronised }
            .any {
                val matchTime = TimeUtils.parseTime(it.commenceTime).timeInMillis
                DateUtils.isToday(matchTime)
            }

        val hasNotCompletedMatchesToday = allMatchesForSportResult.data.orEmpty()
            .filter { !it.synchronised }
            .any {
                val matchTime = TimeUtils.parseTime(it.commenceTime).timeInMillis
                DateUtils.isToday(matchTime) && !it.completed
            }
        val hasNotCompletedMatchesInPast = allMatchesForSportResult.data.orEmpty()
            .filter { !it.synchronised }
            .any {
                val matchTime = TimeUtils.parseTime(it.commenceTime).timeInMillis
                !DateUtils.isToday(matchTime) && matchTime < Calendar.getInstance().timeInMillis && !it.completed
            }

        val lastTodaysMatchTime = allMatchesForSportResult.data.orEmpty()
            .filter { !it.synchronised }
            .lastOrNull {
                val matchTime = TimeUtils.parseTime(it.commenceTime).timeInMillis
                DateUtils.isToday(matchTime)
            }
            ?.commenceTime

        return ResultWithStatus(
            MatchesCompletedInfo(
                hasMatchesToday,
                hasNotCompletedMatchesToday,
                hasNotCompletedMatchesInPast,
                lastTodaysMatchTime
            ), ResultStatus.success()
        )
    }

    //endregion

    //region document and collection references

    private fun getSportSynchDocRef(): DocumentReference {
        return db.collection(FirebaseConstants.COLLECTION_SYNCH_INFO)
            .document(FirebaseConstants.DOCUMENT_SPORTS_SYNCH_INFO)
    }

    private fun getSportsColRef(): CollectionReference {
        return db.collection(FirebaseConstants.COLLECTION_SPORTS)
    }

    private fun getMatchesColRef(sportKey: String): CollectionReference {
        return db.collection(FirebaseConstants.COLLECTION_MATCHES)
            .document(FirebaseConstants.COLLECTION_MATCHES)
            .collection(FirebaseConstants.INNER_COLLECTION_MATCHES_SPORTS)
            .document(sportKey)
            .collection(FirebaseConstants.INNER_COLLECTION_MATCHES_SPORTS_MATCHES)
    }

    private fun getMatchesSynchDocRef(sportKey: String): DocumentReference {
        return db.collection(FirebaseConstants.COLLECTION_SYNCH_INFO)
            .document(FirebaseConstants.DOCUMENT_MATCHES_SYNCH_INFO)
            .collection(FirebaseConstants.COLLECTION_MATCHES_SYNCH_INFO_SPORTS)
            .document(sportKey)
    }

    //endregion

    //region helpful getters and setters

    private suspend fun saveData(
        docRef: DocumentReference,
        documentData: Map<String, Any>
    ): ResultStatus {
        return suspendCoroutine { continuation ->
            docRef
                .update(documentData)
                .addOnSuccessListener {
                    continuation.resume(ResultStatus.success())
                }.addOnFailureListener { e ->
                    if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                        // document for synch data does not exists, so need create it firstly
                        docRef
                            .set(documentData)
                            .addOnSuccessListener {
                                continuation.resume(ResultStatus.success())
                            }.addOnFailureListener { err ->
                                continuation.resume(ResultStatus.failure(err))
                            }
                    } else {
                        continuation.resume(ResultStatus.failure(e))
                    }
                }
        }
    }

    private suspend fun saveObject(
        collRef: CollectionReference,
        docId: String?,
        obj: Any
    ): ResultStatus {
        return suspendCoroutine { continuation ->
            val docRefSetRequest =
                if (docId != null) collRef.document(docId).set(obj)
                else collRef.add(obj)
            docRefSetRequest.addOnSuccessListener {
                continuation.resume(ResultStatus.success())
            }.addOnFailureListener { e ->
                continuation.resume(ResultStatus.failure(e))
            }
        }
    }

    private suspend fun <T> saveAllObjects(
        objects: List<T>,
        saverForOneObject: suspend (obj: T) -> ResultStatus
    ): ResultStatus {
        var failedCount = 0
        objects.forEach {
            val result = saverForOneObject.invoke(it)
            if (result.isFailure()) {
                failedCount += 1
            }
        }

        return if (objects.isNotEmpty() && failedCount == objects.size) {
            ResultStatus.failure("Failed save all objects")
        } else {
            ResultStatus.success()
        }
    }

    private suspend fun <T : Any> getPropertyFromDocument(
        docRef: DocumentReference,
        property: String,
        defaultValue: T? = null
    ): ResultWithStatus<T> {
        return suspendCoroutine { continuation ->
            docRef
                .get()
                .addOnSuccessListener { document ->
                    if (document.data.isNullOrEmpty() || document.data?.containsKey(property) != true) {
                        continuation.resume(ResultWithStatus(defaultValue, ResultStatus.success()))
                    } else {
                        @Suppress("UNCHECKED_CAST") val data = document.data!![property] as T?
                        continuation.resume(
                            ResultWithStatus(data, ResultStatus.success())
                        )
                    }
                }.addOnFailureListener { exception ->
                    continuation.resume(ResultWithStatus(null, ResultStatus.failure(exception)))
                }
        }
    }

    private suspend inline fun <reified T : Any> getObjects(
        collRef: CollectionReference
    ): ResultWithStatus<List<T>> {
        return suspendCoroutine { continuation ->
            collRef.get()
                .addOnSuccessListener { documents ->
                    val objects = mutableListOf<T>()
                    for (document in documents) {
                        objects.add(document.toObject(T::class.java))
                    }
                    continuation.resume(ResultWithStatus(objects.toList(), ResultStatus.success()))
                }.addOnFailureListener { e ->
                    continuation.resume(ResultWithStatus(null, ResultStatus.failure(e)))
                }
        }
    }

    //endregion

}
