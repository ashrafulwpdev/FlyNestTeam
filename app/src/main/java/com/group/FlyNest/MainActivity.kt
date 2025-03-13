package com.group.FlyNest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomBar: AnimatedBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Get the AnimatedBottomBar
        bottomBar = findViewById(R.id.BNV)

        setupBottomBarNavigation()

        // Sync bottom bar with NavController
        bottomBar.selectTabById(R.id.homeFragment, true)
    }

    private fun setupBottomBarNavigation() {
        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                if (lastTab != null && lastTab.id == newTab.id) {
                    navController.popBackStack(navController.graph.startDestinationId, false)
                } else {
                    when (newTab.id) {
                        R.id.homeFragment -> navController.navigate(R.id.homeFragment)
                        R.id.bookingFragment2 -> navController.navigate(R.id.bookingFragment2)
                        R.id.offerFragment -> navController.navigate(R.id.offerFragment)
                        R.id.profileFragment -> navController.navigate(R.id.profileFragment)
                    }
                }
            }
        })
    }
}