package com.example.casino.utils

import android.util.DisplayMetrics
import com.example.casino.app.App

object MetricUtils {
    fun convertDpToPixel(dp: Float): Float {
        val densityDpi = App.instance.resources.displayMetrics.densityDpi
        return dp * (densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}
