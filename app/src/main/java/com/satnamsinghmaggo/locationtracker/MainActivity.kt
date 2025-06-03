package com.satnamsinghmaggo.locationtracker

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
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
    fun checkAndPromptEnableGPS(
        activity: Activity,
        launcher: androidx.activity.result.ActivityResultLauncher<IntentSenderRequest>
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    launcher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity

    val gpsDialogLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // GPS enabled – you can retry fetching location
        } else {
            // User declined – handle accordingly
        }
    }

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            var location by remember { mutableStateOf<Location?>(null) }
            var isTracking by remember { mutableStateOf(false) }

            WelcomeScreen(
                isTracking = isTracking,
                onStartTracking = { loc ->
                    location = loc
                    isTracking = true
                    LocationHolder.location = loc
                    navController.navigate("location/User")
                },
                onRequestEnableGPS = {
                    activity?.let {
                        checkAndPromptEnableGPS(it, gpsDialogLauncher) // ✅ Now both args are passed
                    }
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
