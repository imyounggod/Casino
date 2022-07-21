package com.example.casino.utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    private const val FORMAT_PATTERN_DEFAULT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val FORMAT_PATTERN_DAY_MONTH = "dd.MM"

    private fun getSDF(formatPattern: String? = null, locale: Locale? = null): SimpleDateFormat {
        return SimpleDateFormat(formatPattern ?: FORMAT_PATTERN_DEFAULT, locale ?: Locale.UK)
    }

    fun getCurrentTimeStr(): String {
        return getSDF().format(Date(Calendar.getInstance().timeInMillis))
    }

    fun parseTime(time: String): Calendar {
        val c = Calendar.getInstance()
        c.time = getSDF().parse(time) ?: throw Exception("Could not parse time: $time")
        return c
    }

    fun parseTime(unixTime: Long): Calendar {
        val c = Calendar.getInstance()
        c.timeInMillis = unixTime * 1000
        return c
    }

    fun formatTime(time: Calendar, formatPattern: String? = null, locale: Locale? = null): String {
        return getSDF(formatPattern, locale).format(Date(time.timeInMillis))
    }

    fun formatDayMonthDefaultLocale(time: Calendar): String {
        return formatTime(time, FORMAT_PATTERN_DAY_MONTH, Locale.getDefault())
    }
}