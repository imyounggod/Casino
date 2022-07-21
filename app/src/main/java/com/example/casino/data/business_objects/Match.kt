package com.example.casino.data.business_objects

data class Match(
    val matchId: String,
    val leagueName: String,
    val date: String,
    val firstTeam: TeamInMatch,
    val secondTeam: TeamInMatch
)
