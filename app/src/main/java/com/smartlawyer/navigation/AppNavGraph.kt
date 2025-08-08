package com.smartlawyer.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartlawyer.ui.screens.auth.*
import com.smartlawyer.ui.screens.case.CaseEditScreen
import com.smartlawyer.ui.screens.case.CaseListScreen
import com.smartlawyer.ui.screens.case.CaseRegistrationScreen
import com.smartlawyer.ui.screens.client.ClientListScreen
import com.smartlawyer.ui.screens.client.ClientRegistrationScreen
import com.smartlawyer.ui.screens.dashboard.DashboardScreen
import com.smartlawyer.ui.screens.intro.IntroScreen
import com.smartlawyer.ui.screens.splash.SplashScreen
import com.smartlawyer.ui.viewmodels.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(Screens.Splash.route) {
            SplashScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Screens.Intro.route) {
            IntroScreen(navController = navController)
        }

        composable(Screens.Login.route) {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Screens.Register.route) {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Screens.BiometricLogin.route) {
            BiometricLoginScreen(navController = navController)
        }

        composable(Screens.Dashboard.route) {
            DashboardScreen(navController = navController, viewModel = authViewModel)
        }

        // Client management screens
        composable(Screens.ClientRegistration.route) {
            ClientRegistrationScreen(navController = navController)
        }

        composable(Screens.ClientList.route) {
            ClientListScreen(navController = navController)
        }

        // Case management screens
        composable(Screens.CaseRegistration.route) {
            CaseRegistrationScreen(navController = navController)
        }

        composable(Screens.CaseList.route) {
            CaseListScreen(navController = navController)
        }

        composable(
            route = Screens.CaseEdit.route,
            arguments = listOf(
                navArgument("caseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getLong("caseId") ?: 0L
            CaseEditScreen(
                navController = navController,
                caseId = caseId
            )
        }
    }
}
