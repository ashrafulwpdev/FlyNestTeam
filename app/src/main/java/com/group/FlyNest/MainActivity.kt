package com.group.FlyNest

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomBar: AnimatedBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow drawing behind the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        // Set status bar appearance for API 21-22
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(R.color.white, theme)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

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