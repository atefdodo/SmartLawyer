package com.smartlawyer.navigation

sealed class Screens(val route: String) {
    object Splash : Screens("splash_screen")
    object Login : Screens("login_screen")
    object Register : Screens("register_screen")
    object Dashboard : Screens("dashboard_screen")
    object BiometricLogin : Screens("biometric_login_screen")
    object Intro : Screens("intro_screen")
    
    // Client management
    object ClientRegistration : Screens("client_registration_screen")
    object ClientList : Screens("client_list_screen")
    
    // Case management
    object CaseRegistration : Screens("case_registration_screen")
    object CaseList : Screens("case_list_screen")
} 