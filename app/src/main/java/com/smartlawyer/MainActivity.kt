package com.smartlawyer

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.smartlawyer.navigation.AppNavGraph
import com.smartlawyer.ui.theme.SmartLawyerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import android.os.LocaleList

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force Arabic (Egypt) locale for the entire app
        setAppLocale()

        setContent {
            SmartLawyerTheme {
                SmartLawyerApp()
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

@Composable
fun SmartLawyerApp() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController)
} 