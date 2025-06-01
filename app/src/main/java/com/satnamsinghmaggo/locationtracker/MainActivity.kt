package com.satnamsinghmaggo.locationtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.*
import com.airbnb.lottie.model.KeyPath
import com.satnamsinghmaggo.locationtracker.ui.theme.LocationTrackerTheme
import kotlinx.coroutines.launch

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

@Composable
fun LocationTrackerScrollScreen(modifier: Modifier = Modifier) {
    val backgroundColor = Color(0xFF4C62E7)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()


    val userName = "Satnam Singh Maggo"
    val latitude = "30.7333"
    val longitude = "76.7794"


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 🔹 Screen 1 - Welcome with Animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp)
                .background(backgroundColor)
        ) {
            Image(
                painter = painterResource(id = R.drawable.location_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f),
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

                // Lottie Animation
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

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Start Location",
                        color = backgroundColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // 🔹 Screen 2 - Coordinates Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .height(180.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "User: $userName", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Latitude: $latitude", fontSize = 16.sp)
                    Text(text = "Longitude: $longitude", fontSize = 16.sp)
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
