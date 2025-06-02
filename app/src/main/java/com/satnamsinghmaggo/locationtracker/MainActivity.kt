package com.satnamsinghmaggo.locationtracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.satnamsinghmaggo.locationtracker.ui.theme.LocationTrackerTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationTrackerTheme {
                val navController = rememberNavController()

                var isTracking by remember {
                    mutableStateOf(
                        getSharedPreferences("prefs", Context.MODE_PRIVATE)
                            .getBoolean("isTracking", false)
                    )
                }

                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") {
                        WelcomeScreen(
                            isTracking = isTracking,
                            onStartTracking = {
                                isTracking = true
                                getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                                    .putBoolean("isTracking", true).apply()
                                navController.navigate("location")
                            }
                        )
                    }
                    composable("location") {
                        LocationScreen(
                            userName = "Satnam Singh Maggo",
                            latitude = "30.7333",
                            longitude = "76.7794",
                            onStopTracking = {
                                isTracking = false
                                getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                                    .putBoolean("isTracking", false).apply()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    LocationTrackerTheme {
        WelcomeScreen(
            isTracking = true,
            onStartTracking = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLocationScreen() {
    LocationTrackerTheme {
        LocationScreen(
            userName = "Satnam Singh Maggo",
            latitude = "30.7333",
            longitude = "76.7794",
            onStopTracking = {}
        )
    }
}


