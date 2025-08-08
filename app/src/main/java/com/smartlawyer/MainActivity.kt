package com.smartlawyer

import android.os.Bundle
import android.content.res.Configuration
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.smartlawyer.navigation.AppNavGraph
import com.smartlawyer.ui.theme.SmartLawyerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import android.os.LocaleList
import androidx.core.view.WindowCompat

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Force Arabic (Egypt) locale for the entire app
        setAppLocale()

        setContent {
            SmartLawyerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }

    private fun setAppLocale() {
        // Arabic (Egypt) locale - using Locale.Builder (recommended)
        val locale = Locale.Builder()
            .setLanguage("ar")
            .setRegion("EG")
            .build()
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        config.setLocales(LocaleList(locale))

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}