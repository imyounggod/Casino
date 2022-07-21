package com.example.casino.ui.casino

import androidx.lifecycle.ViewModel
import com.example.casino.R
import com.example.casino.ui.views.Banner

class CasinoViewModel : ViewModel() {

    fun getBanners(): List<Banner> {
        return listOf(
            Banner(
                R.drawable.casino_banner_1,
                0
            ),
            Banner(
                R.drawable.casino_banner_2,
                1
            ),
            Banner(
                R.drawable.casino_banner_3,
                2
            )
        )
    }
}