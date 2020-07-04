package org.rionlabs.tatsu.ui.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewModelProviders.of(this).get(MainViewModel::class.java)

        val fragmentContainer = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment)
        navController = (fragmentContainer as NavHostFragment).navController

        binding.mainBottomNavigation.setupWithNavController(navController)
    }
}