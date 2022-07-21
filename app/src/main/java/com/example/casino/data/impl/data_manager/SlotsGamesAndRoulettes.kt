package com.example.casino.data.impl.data_manager

import com.example.casino.R
import com.example.casino.app.App
import com.example.casino.data.business_objects.Roulette
import com.example.casino.data.business_objects.Slot
import com.example.casino.data.business_objects.TableGame
import com.example.casino.utils.AssetReaderUtils

object SlotsGamesAndRoulettes {
    private val slotNames = listOf(
        R.string.slot_1_name,
        R.string.slot_2_name,
        R.string.slot_3_name,
        R.string.slot_4_name,
        R.string.slot_5_name,
        R.string.slot_6_name,
        R.string.slot_7_name,
        R.string.slot_8_name,
        R.string.slot_9_name,
        R.string.slot_10_name,
        R.string.slot_11_name,
        R.string.slot_12_name,
        R.string.slot_13_name,
        R.string.slot_14_name,
        R.string.slot_15_name,
        R.string.slot_16_name,
        R.string.slot_17_name,
        R.string.slot_18_name,
        R.string.slot_19_name,
        R.string.slot_20_name
    )
    private val tableGameNames = listOf(
        R.string.table_game_1_name,
        R.string.table_game_2_name,
        R.string.table_game_3_name,
        R.string.table_game_4_name,
        R.string.table_game_5_name,
        R.string.table_game_6_name,
        R.string.table_game_7_name,
        R.string.table_game_8_name,
        R.string.table_game_9_name,
        R.string.table_game_10_name,
        R.string.table_game_11_name,
        R.string.table_game_12_name,
        R.string.table_game_13_name,
        R.string.table_game_14_name,
        R.string.table_game_15_name,
        R.string.table_game_16_name,
        R.string.table_game_17_name,
        R.string.table_game_18_name,
        R.string.table_game_19_name,
        R.string.table_game_20_name
    )
    private val rouletteNames = listOf(
        R.string.roulette_1_name,
        R.string.roulette_2_name
    )
    private val slotIcons = listOf(
        R.drawable.slot_1,
        R.drawable.slot_2,
        R.drawable.slot_3,
        R.drawable.slot_4,
        R.drawable.slot_5,
        R.drawable.slot_6,
        R.drawable.slot_7,
        R.drawable.slot_8,
        R.drawable.slot_9,
        R.drawable.slot_10,
        R.drawable.slot_11,
        R.drawable.slot_12,
        R.drawable.slot_13,
        R.drawable.slot_14,
        R.drawable.slot_15,
        R.drawable.slot_16,
        R.drawable.slot_17,
        R.drawable.slot_18,
        R.drawable.slot_19,
        R.drawable.slot_20
    )
    private val tableGameIcons = listOf(
        R.drawable.table_game_1,
        R.drawable.table_game_2,
        R.drawable.table_game_3,
        R.drawable.table_game_4,
        R.drawable.table_game_5,
        R.drawable.table_game_6,
        R.drawable.table_game_7,
        R.drawable.table_game_8,
        R.drawable.table_game_9,
        R.drawable.table_game_10,
        R.drawable.table_game_11,
        R.drawable.table_game_12,
        R.drawable.table_game_13,
        R.drawable.table_game_14,
        R.drawable.table_game_15,
        R.drawable.table_game_16,
        R.drawable.table_game_17,
        R.drawable.table_game_18,
        R.drawable.table_game_19,
        R.drawable.table_game_20
    )
    private val rouletteIcons = listOf(
        R.drawable.roulette_1,
        R.drawable.roulette_2
    )

    fun getSlots(): List<Slot> {
        val result = mutableListOf<Slot>()
        for (i in 0 until 20) {
            result.add(
                Slot(
                    App.getString(slotNames[i]),
                    AssetReaderUtils.readTextFromFile("slots_desc_${i + 1}.txt"),
                    slotIcons[i]
                )
            )
        }
        return result
    }

    fun getTableGames(): List<TableGame> {
        val result = mutableListOf<TableGame>()
        for (i in 0 until 20) {
            result.add(
                TableGame(
                    App.getString(tableGameNames[i]),
                    AssetReaderUtils.readTextFromFile("games_desc_${i + 1}.txt"),
                    tableGameIcons[i]
                )
            )
        }
        return result
    }

    fun getRoulettes(): List<Roulette> {
        val result = mutableListOf<Roulette>()
        for (i in 0 until 2) {
            result.add(
                Roulette(
                    App.getString(rouletteNames[i]),
                    AssetReaderUtils.readTextFromFile("roulette_desc_${i + 1}.txt"),
                    rouletteIcons[i]
                )
            )
        }
        return result
    }
}