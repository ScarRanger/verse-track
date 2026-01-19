package com.rhinepereira.versetrack.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Themes : Screen("themes", "Themes", Icons.Default.List)
    object Daily : Screen("daily", "Daily", Icons.Default.DateRange)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.Notifications) // Using Notifications icon as a placeholder for Calendar if needed, but DateRange is already used for Daily. Let's swap.
}

@Composable
fun MainContainer(
    sharedText: String? = null,
    onSharedTextConsumed: () -> Unit = {}
) {
    val navController = rememberNavController()
    // Swapping icons for better representation: List for Themes, Edit/Check for Daily, DateRange for Calendar
    val items = listOf(Screen.Themes, Screen.Daily, Screen.Calendar)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val icon = when(screen) {
                        Screen.Themes -> Icons.Default.List
                        Screen.Daily -> Icons.Default.Edit // Let's use Edit for Daily
                        Screen.Calendar -> Icons.Default.DateRange
                    }
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Themes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Themes.route) {
                VerseScreen(
                    sharedText = sharedText,
                    onSharedTextConsumed = onSharedTextConsumed
                )
            }
            composable(Screen.Daily.route) {
                DailyScreen()
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
        }
    }
}

// Placeholder for Edit icon if not available, but standard material icons should have it.
// Actually, Icons.Default.Edit is standard.
