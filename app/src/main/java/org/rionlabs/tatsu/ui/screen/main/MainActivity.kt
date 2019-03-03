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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (it.itemId == R.id.navigation_menu_settings) {
                switchToSettings()
                return true
            } else if (it.itemId == android.R.id.home) {
                switchToStats()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun switchToStats() {
        navController.navigate(R.id.navigation_menu_stats)
        fab.setImageResource(R.drawable.ic_hourglass)
        fab.setOnClickListener {
            switchToMain()
        }
        fab.setOnLongClickListener { false }
        headerSubtitle.text = getString(R.string.title_stats)
    }

    private fun switchToSettings() {
        navController.navigate(R.id.navigation_menu_settings)
        fab.setImageResource(R.drawable.ic_hourglass)
        fab.setOnClickListener {
            switchToMain()
        }
        fab.setOnLongClickListener { false }
        headerSubtitle.text = getString(R.string.title_settings)
    }

    private fun switchToMain() {
        navController.navigate(R.id.navigation_menu_timer)
        fab.setOnClickListener {}
        headerSubtitle.text = getString(R.string.title_timer)
    }
}
