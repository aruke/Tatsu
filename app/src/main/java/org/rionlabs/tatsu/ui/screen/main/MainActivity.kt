package org.rionlabs.tatsu.ui.screen.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.rionlabs.tatsu.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainBottomNavigation)

        navController = (mainNavHostFragment as NavHostFragment).navController
        ViewModelProviders.of(this).get(MainViewModel::class.java)

        switchToMain()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Actually, Stats icon
            android.R.id.home -> {
                switchToStats()
                true
            }
            // Settings
            R.id.navigation_menu_settings -> {
                switchToSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchToStats() {
        navController.navigate(R.id.action_navigation_timer_to_stats)
        fab.setImageResource(R.drawable.ic_hourglass)
        fab.setOnClickListener {
            popToMain()
        }
        fab.setOnLongClickListener { false }
        headerSubtitle.text = getString(R.string.title_stats)
    }

    private fun switchToSettings() {
        navController.navigate(R.id.action_navigation_timer_to_settings)
        fab.setImageResource(R.drawable.ic_hourglass)
        fab.setOnClickListener {
            popToMain()
        }
        fab.setOnLongClickListener { false }
        headerSubtitle.text = getString(R.string.title_settings)
    }

    private fun switchToMain() {
        fab.setOnClickListener {}
        headerSubtitle.text = getString(R.string.title_timer)
    }

    private fun popToMain() {
        navController.popBackStack()
        fab.setOnClickListener {}
        headerSubtitle.text = getString(R.string.title_timer)
    }
}
