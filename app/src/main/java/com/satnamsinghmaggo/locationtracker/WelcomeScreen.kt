@file:OptIn(ExperimentalPermissionsApi::class)

package com.satnamsinghmaggo.locationtracker

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.satnamsinghmaggo.locationtracker.WelcomeScreen
import com.satnamsinghmaggo.locationtracker.ui.theme.LocationTrackerTheme
import kotlinx.coroutines.launch


@Composable
fun WelcomeScreen(
    isTracking: Boolean,
    onStartTracking: (Location) -> Unit
) {
    val backgroundColor = Color(0xFF4C62E7)
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val locationManager = remember {
        com.satnamsinghmaggo.locationtracker.LocationManager(
            context = context,
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        )
    }
    val locationPermission = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION

        )
    )
    var location by remember {
        mutableStateOf<Location?>(null)
    }

    var isTracking by remember {
        mutableStateOf(
            context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getBoolean("isTracking", false)
        )
    }

    fun saveTrackingState(isTracking: Boolean) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit {
                putBoolean("isTracking", isTracking)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.location_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.location_animation)
            )
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )

            val dynamicProperties = rememberLottieDynamicProperties(
                LottieDynamicProperty(
                    value = Color.White.toArgb(),
                    property = LottieProperty.COLOR,
                    keyPath = KeyPath("")
                ),
                LottieDynamicProperty(
                    property = LottieProperty.STROKE_COLOR,
                    value = Color.White.toArgb(),
                    keyPath = KeyPath("")
                )
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                dynamicProperties = dynamicProperties,
                modifier = Modifier.size(400.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Track Location",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Real-time location tracking.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (!locationPermission.allPermissionsGranted || locationPermission.shouldShowRationale) {
                        locationPermission.launchMultiplePermissionRequest()
                    } else {
                        coroutineScope.launch {
                            location = locationManager.getLocation()
                            location?.let {
                                Log.d("LOCATION", "Lat: ${it.latitude}, Lon: ${it.longitude}")
                                onStartTracking(it)
                            } ?: Log.e("LOCATION", "Location is null")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (isTracking) "Stop Tracking" else "Start Tracking",
                    color = backgroundColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


