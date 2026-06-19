package com.example.remindme.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.remindme.screens.DashboardScreen
import com.example.remindme.screens.ProfileScreen
import com.example.remindme.screens.AboutUsScreen
import com.example.remindme.screens.PrivacyPolicyScreen
import com.example.remindme.screens.TermsConditionsScreen
import com.example.remindme.screens.ArchiveScreen
import com.example.remindme.screens.StarredScreen
import com.example.remindme.ui.DashboardViewModel
import com.example.remindme.ui.ProfileViewModel
import com.example.remindme.ui.SettingsViewModel
import com.example.remindme.screens.SettingsScreen
import com.example.remindme.screens.SetLockScreen

private val PurplePrimary = Color(0xFF5338D5)
private val GrayMuted = Color(0xFF9090A5)
private val CardBackground = Color(0xFFFFFFFF)

@Composable
fun AppNavigation(
    dashboardViewModel: DashboardViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    
    // Collect profile states dynamically from the ProfileViewModel
    val savedName by profileViewModel.savedName.collectAsState()
    val savedPhone by profileViewModel.savedPhone.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    settingsViewModel = settingsViewModel,
                    savedPhone = savedPhone,
                    savedName = savedName,
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToArchive = {
                        dashboardViewModel.loadArchivedReminders()
                        navController.navigate(Screen.Archive.route)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    savedName = savedName,
                    savedPhone = savedPhone,
                    onSave = { name, phone ->
                        profileViewModel.saveProfile(name, phone)
                        navController.navigateUp()
                    },
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToAboutUs = {
                        navController.navigate(Screen.AboutUs.route)
                    },
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    },
                    onNavigateToTermsConditions = {
                        navController.navigate(Screen.TermsConditions.route)
                    },
                    onNavigateToStarred = {
                        dashboardViewModel.loadStarredReminders()
                        navController.navigate(Screen.Starred.route)
                    },
                    onNavigateToArchive = {
                        dashboardViewModel.loadArchivedReminders()
                        navController.navigate(Screen.Archive.route)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.AboutUs.route) {
                AboutUsScreen(
                    onNavigateBack = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onNavigateBack = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.TermsConditions.route) {
                TermsConditionsScreen(
                    onNavigateBack = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    dashboardViewModel = dashboardViewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToSetLock = {
                        navController.navigate(Screen.SetLock.route)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.SetLock.route) {
                SetLockScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.Archive.route) {
                ArchiveScreen(
                    viewModel = dashboardViewModel,
                    settingsViewModel = settingsViewModel,
                    onNavigateBack = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Screen.Starred.route) {
                StarredScreen(
                    viewModel = dashboardViewModel,
                    settingsViewModel = settingsViewModel,
                    onNavigateBack = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
