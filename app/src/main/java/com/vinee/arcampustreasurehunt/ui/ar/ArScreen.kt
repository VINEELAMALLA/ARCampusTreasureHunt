package com.vinee.arcampustreasurehunt.ui.ar

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.google.android.gms.location.LocationServices
import com.vinee.arcampustreasurehunt.data.CollegeLocations
import com.vinee.arcampustreasurehunt.location.LocationUpdatesLiveData
import com.vinee.arcampustreasurehunt.ui.common.VideoPlayer
import com.vinee.arcampustreasurehunt.viewmodel.ArViewModel

@Composable
fun ArScreen(collegeName: String, viewModel: ArViewModel = viewModel()) {
    val context = LocalContext.current
    val locationLiveData = remember { LocationUpdatesLiveData(context) }
    val currentLocation by locationLiveData.observeAsState()
    val college = remember(collegeName) { CollegeLocations.colleges.find { it.name == collegeName } }
    
    var showVideo by remember { mutableStateOf(false) }
    val currentBlockIndex = viewModel.currentBlockIndex
    val gameState = viewModel.gameState
    
    val currentBlock = college?.blocks?.getOrNull(currentBlockIndex)
    
    // Calculate distance to current target
    val distanceToTarget = remember(currentLocation, currentBlock) {
        val location = currentLocation
        if (location != null && currentBlock != null) {
            val targetLocation = Location("target").apply {
                latitude = currentBlock.latitude
                longitude = currentBlock.longitude
            }
            location.distanceTo(targetLocation)
        } else {
            Float.MAX_VALUE
        }
    }
    
    // Check if user reached the location (within 20 meters)
    LaunchedEffect(distanceToTarget) {
        if (distanceToTarget < 20f && gameState == "clue") {
            viewModel.toReached()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Camera background to create an AR-like overlay
        CameraPreview(modifier = Modifier.fillMaxSize())

        // Semi-transparent overlay to improve contrast if needed
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.06f))
        ) {}

        // Always-visible status chip for quick debugging/visibility
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 2.dp
            ) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${collegeName} • ${gameState.uppercase()} • #${currentBlockIndex + 1}",
                        color = Color(0xFF7B1FA2),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    when (gameState) {
            "clue" -> {
                // AR Clue Display
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // AR-like clue display
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color(0xFF7B1FA2), RoundedCornerShape(16.dp)), // home pastel purple accent
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE1BEE7) // home pastel purple card
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔍 AR CLUE ${currentBlockIndex + 1}",
                                color = Color(0xFF7B1FA2), // home purple title
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = currentBlock?.clue ?: "No clue available",
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                            
                            // Distance to target intentionally hidden from users
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Current location info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF9C4) // pastel yellow card
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📍 Current Location",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            currentLocation?.let { location ->
                                Text(
                                    text = "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}",
                                    color = Color.Black.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            } ?: Text(
                                text = "Getting location...",
                                color = Color.Black.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Test helper button to force "reached" and play video
                    Button(
                        onClick = {
                            showVideo = false
                            viewModel.toReached()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB8E6B8) // home pastel green
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "REACHED (TEST)",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            "reached" -> {
                // Location Reached Display
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
            Card(
                        modifier = Modifier
                            .fillMaxWidth()
                .border(2.dp, Color(0xFF0D47A1), RoundedCornerShape(16.dp)), // dark blue accent
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFBBDEFB) // pastel sky blue card
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎯 LOCATION REACHED!",
                                color = Color(0xFF0D47A1),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "You've found the ${currentBlock?.name}!",
                                color = Color(0xFF1976D2),
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                Button(
                                onClick = { 
                                    showVideo = true
                                    viewModel.toVideo()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "🎬 PLAY VIDEO",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B1FA2)
                                )
                            }
                        }
                    }
                }
            }
            
            "video" -> {
                // Video Display
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (showVideo && currentBlock != null) {
                        VideoPlayer(
                            videoName = currentBlock.video,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            showVideo = false
                            college?.let { viewModel.nextClue(it.blocks.size) }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White // white background as requested
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (currentBlockIndex >= (college?.blocks?.size ?: 1) - 1) "🏆 FINISH HUNT" else "➡️ NEXT CLUE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
            
            "completed" -> {
                // Game Completed
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color(0xFF7B1FA2), RoundedCornerShape(16.dp)), // stronger purple border
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE1BEE7) // pastel purple card
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏆 YOU WON!",
                                color = Color(0xFF7B1FA2),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Congratulations! You've completed the ${collegeName} campus treasure hunt!",
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                Button(
                                onClick = {
                                    college?.let { college ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(college.website))
                                        context.startActivity(intent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "🌐 KNOW MORE ABOUT ${collegeName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        }
        
    // Distance debug overlay removed
    }
}

