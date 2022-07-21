package com.example.casino.data.business_objects

import com.example.casino.utils.TimeUtils
import java.util.*

data class MatchesCompletedInfo(
    val hasMatchesToday: Boolean,
    val hasNotCompletedMatchesInPast: Boolean,
    val hasNotCompletedMatchesToday: Boolean,
    val lastTodaysMatchTime: Long?
) {
    fun isPassedEnoughTimeAfterLastMatchForSynch(): Boolean {
        if (lastTodaysMatchTime == null) return true
        val cLastTime = TimeUtils.parseTime(lastTodaysMatchTime)
        val cCurrTime = Calendar.getInstance()
        val threeHours = 1000 * 60 * 60 * 3
        val passedTimeAfterLastMatch =
            cCurrTime.timeInMillis - cLastTime.timeInMillis

        return cCurrTime.timeInMillis > cLastTime.timeInMillis && passedTimeAfterLastMatch > threeHours
    }
}