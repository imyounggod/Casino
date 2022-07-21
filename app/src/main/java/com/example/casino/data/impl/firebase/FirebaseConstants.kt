package com.example.casino.data.impl.firebase

object FirebaseConstants {
    const val COLLECTION_SYNCH_INFO = "synch_info"
    const val DOCUMENT_SPORTS_SYNCH_INFO = "sports_synch_info"
    const val PROPERTY_SPORTS_IS_SYNCHING = "sports_is_synching"
    const val PROPERTY_LAST_SPORTS_SYNCHING_TIME = "last_sports_synching_time"
    const val DOCUMENT_MATCHES_SYNCH_INFO = "matches_synch_info"
    const val COLLECTION_MATCHES_SYNCH_INFO_SPORTS = "sports"
    const val PROPERTY_MATCHES_IS_SYNCHING = "matches_is_synching"
    const val PROPERTY_MATCHES_LAST_SYNCHING_TIME = "matches_last_synching_time"

    const val COLLECTION_SPORTS = "sports"
    const val COLLECTION_MATCHES = "matches"
    const val INNER_COLLECTION_MATCHES_SPORTS = "sports"
    const val INNER_COLLECTION_MATCHES_SPORTS_MATCHES = "matches"
}