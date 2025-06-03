package com.satnamsinghmaggo.locationtracker

import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.satnamsinghmaggo.locationtracker.ui.theme.LocationTrackerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationTrackerTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            var location by remember { mutableStateOf<Location?>(null) }
            var isTracking by remember { mutableStateOf(false) }

            WelcomeScreen(
                isTracking = isTracking,
                onStartTracking = { loc ->
                    location = loc
                    isTracking = true
                    // Navigate with fake username and store location in ViewModel or Singleton
                    LocationHolder.location = loc
                    navController.navigate("location/User")
                }
            )
        }

        composable(
            route = "location/{userName}",
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            val location = LocationHolder.location

            if (location != null) {
                LocationScreen(
                    userName = userName,
                    location = location,
                    onStopTracking = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
