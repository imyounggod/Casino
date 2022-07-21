package com.example.casino.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.casino.R
import com.example.casino.ui.view_extensions.setVisible
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNavController()
        initBottomTabsListener()
    }

    private fun initNavController() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val showBottomBar = when (destination.id) {
                R.id.casinoFragment -> true
                R.id.sportFragment -> true
                else -> false
            }
            bottom_bar.setVisible(showBottomBar)
        }

    }

    private fun initBottomTabsListener() {
        val navController = findNavController(R.id.nav_host_fragment)
        tab_casino.setOnClickListener {
            if (navController.currentDestination?.id != R.id.casinoFragment) {
                navController.navigateUp()
            }
        }
        tab_sport.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.sportFragment, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build()

            if (navController.currentDestination?.id != R.id.sportFragment) {
                navController.navigate(R.id.sportFragment, null, navOptions)
            }
        }
    }
}