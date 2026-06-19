package com.example.remindme.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object AboutUs : Screen("about_us")
    object PrivacyPolicy : Screen("privacy_policy")
    object TermsConditions : Screen("terms_conditions")
    object Settings : Screen("settings")
    object Archive : Screen("archive")
    object Starred : Screen("starred")
    object SetLock : Screen("set_lock")
}
