@file:OptIn(ExperimentalPermissionsApi::class)

package com.satnamsinghmaggo.locationtracker

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.satnamsinghmaggo.locationtracker.ui.theme.LocationTrackerTheme
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationTrackerScrollScreen(Modifier.padding(innerPadding))

                }
            }
        }
    }
}

class LocationManager(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
){
    suspend fun getLocation(): Location? {
        val hasGrantedFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

       val hasGrantedCoarseLocationPermission = ContextCompat.checkSelfPermission(
           context,
           android.Manifest.permission.ACCESS_COARSE_LOCATION
       ) == PackageManager.PERMISSION_GRANTED
        val locationManager = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!isGpsEnabled && !(hasGrantedCoarseLocationPermission || hasGrantedFineLocationPermission)){
            return null
        }
        return suspendCancellableCoroutine { cont->
            fusedLocationProviderClient.lastLocation.apply {
                if (isComplete){
                    if(isSuccessful){
                        cont.resume(result)
                    }else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    cont.resume(result)
                }
                addOnFailureListener {
                    cont.resume(null)
                }

                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }

    }
}

@Composable
fun LocationTrackerScrollScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val backgroundColor = Color(0xFF4C62E7)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

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

    val userName = "Satnam Singh Maggo"
    val latitude = "30.7333"
    val longitude = "76.7794"

    LaunchedEffect(Unit) {
        if (isTracking) scrollState.animateScrollTo(scrollState.maxValue)
    }
    val locationManager = remember {
        LocationManager(
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


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 🔹 Screen 1 - Welcome with Animation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.location_background),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .alpha(0.2f),
//                    contentScale = ContentScale.Crop
//                )

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
                            keyPath = KeyPath("**")
                        ),
                        LottieDynamicProperty(
                            property = LottieProperty.STROKE_COLOR,
                            value = Color.White.toArgb(),
                            keyPath = KeyPath("**")
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

                    Spacer(modifier = Modifier.weight(1.5f))

                    Button(
                        onClick = {
                            if (!locationPermission.allPermissionsGranted || locationPermission.shouldShowRationale) {
                                locationPermission.launchMultiplePermissionRequest()
                            }else {
                                coroutineScope.launch {
                                    location = locationManager.getLocation()
                                }

                            }
                            isTracking = !isTracking
                            saveTrackingState(isTracking)
                            coroutineScope.launch {
                                if (isTracking) scrollState.animateScrollTo(scrollState.maxValue)
                                else scrollState.animateScrollTo(0)
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

// 🔹 Screen 2 - Profile, Animation, Coordinates Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.map_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Lottie Animation
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.Asset("location_map_animation.json")
                            )
                            val progress by animateLottieCompositionAsState(
                                composition,
                                iterations = LottieConstants.IterateForever
                            )

                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier.fillMaxSize()
                            )

                            // Profile Image over animation
                            val profileImg = painterResource(id = R.drawable.profile)

                            Image(
                                painter = profileImg,
                                contentDescription = "User Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(80.dp))

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECECEE))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "User: $userName",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF222222)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                               location?.let {
                                   Text(
                                       text = "Coordinates: ${it.latitude}, ${it.longitude}",
                                       fontSize = 16.sp,
                                       color = Color(0xFF222222)
                                   )
                               }
                               }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {

                                        isTracking = false
                                        saveTrackingState(false)
                                        coroutineScope.launch {
                                            scrollState.animateScrollTo(0)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text(
                                        text = "Stop Tracking",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
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
        LocationTrackerScrollScreen()
    }
}

