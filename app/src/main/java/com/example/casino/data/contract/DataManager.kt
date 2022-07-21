package com.example.casino.data.contract

import com.example.casino.data.business_objects.*
import com.example.casino.data.business_objects.base.ResultWithStatus

interface DataManager {
    suspend fun getMatches(sport: Sport): ResultWithStatus<List<Match>>
    fun getSlots(): List<Slot>
    fun getTableGames(): List<TableGame>
    fun getRoulettes(): List<Roulette>
}
