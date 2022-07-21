package com.example.casino.data.business_objects

import com.example.casino.R

enum class Sport(val sportName: String) {
    AMERICAN_FOOTBALL("American Football"),
    BASKETBALL("Basketball"),
    GOLF("Golf"),
    SOCCER("Soccer"),
    TENNIS("Tennis")
}

fun Sport.getIcon(): Int {
    return when (this) {
        Sport.AMERICAN_FOOTBALL -> R.drawable.ic_sport_american_football
        Sport.BASKETBALL -> R.drawable.ic_sport_basketbal
        Sport.GOLF -> R.drawable.ic_sport_golf
        Sport.SOCCER -> R.drawable.ic_sport_football
        Sport.TENNIS -> R.drawable.ic_sport_tennis
    }
}

fun Sport.getTeamLogoIcon(): Int {
    return when (this) {
        Sport.AMERICAN_FOOTBALL -> R.drawable.american_football
        Sport.BASKETBALL -> R.drawable.basketball
        Sport.GOLF -> R.drawable.golfing
        Sport.SOCCER -> R.drawable.soccer
        Sport.TENNIS -> R.drawable.tennis
    }
}