package com.caloriematewise

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.caloriematewise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navView = binding.bottomNavigationView

        val topLevelDestinations = setOf(
            com.app.home.R.id.homeFragment,
            com.app.home.R.id.searchFragment,
            com.app.home.R.id.searchInteractiveFragment,
            com.app.home.R.id.mealPlanningFragment,
            com.app.home.R.id.profileFragment
        )

        val appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations).build()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (topLevelDestinations.contains(destination.id)) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }

        // Set up BottomNavigationView with NavController
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}