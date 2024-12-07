package com.example.psyche.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.psyche.R
import com.example.psyche.databinding.ActivityMainBinding
import com.example.psyche.helpers.ThemeUtils
import com.example.psyche.views.chat.ChatFragment
import com.example.psyche.views.home.HomeFragment
import com.example.psyche.views.nearbypsychiatrist.NearbyPsychiatristFragment
import com.example.psyche.views.settings.SettingsFragment
import com.example.psyche.views.testhistory.TestHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import com.example.psyche.helpers.ContextUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("selected_language", "English")
        setLocale(language)

        ThemeUtils.setDarkModeEnabled(this, ThemeUtils.isDarkModeEnabled(this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_test_history -> {
                    loadFragment(TestHistoryFragment())
                    true
                }
                R.id.navigation_chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.navigation_nearby -> {
                    loadFragment(NearbyPsychiatristFragment())
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun setLocale(language: String?) {
        val locale = when (language) {
            "Indonesia" -> Locale("in")
            else -> Locale("en")
        }
        val context = ContextUtils.updateLocale(this, locale)
        resources.updateConfiguration(context.resources.configuration, resources.displayMetrics)
    }
}