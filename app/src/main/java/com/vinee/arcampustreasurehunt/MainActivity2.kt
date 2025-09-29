package com.vinee.arcampustreasurehunt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
// import com.vinee.arcampustreasurehunt.ui.ar.ArCoreScreen
// import com.vinee.arcampustreasurehunt.ar.ArSupport
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.vinee.arcampustreasurehunt.ui.ar.ArScreen
import com.vinee.arcampustreasurehunt.ui.theme.ARCampusTreasureHuntTheme
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

class MainActivity2 : ComponentActivity() {
    private var hasLocationPermission by mutableStateOf(false)
    private var hasLocationPermissionDenied by mutableStateOf(false)
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        hasLocationPermission = fineLocationGranted || coarseLocationGranted
        hasLocationPermissionDenied = !hasLocationPermission

        if (!hasLocationPermission) {
            Toast.makeText(this, "Location permission is required for the treasure hunt!", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            android.util.Log.d("MainActivity", "Firebase initialized successfully")
            
            // Check location permissions
            checkLocationPermissions()
            
            setContent {
                ARCampusTreasureHuntTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (hasLocationPermissionDenied) {
                            PermissionDeniedScreen {
                                checkLocationPermissions()
                            }
                        } else {
                            AppNavigator()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate", e)
            setContent {
                ErrorScreen(error = e.message ?: "Unknown error occurred")
            }
        }
    }
    
    private fun checkLocationPermissions() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        hasLocationPermission = fineLocationPermission == PackageManager.PERMISSION_GRANTED || 
                               coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        composable("auth") {
            AuthScreenNew(
                onLoginSuccess = { 
                    android.util.Log.d("Navigation", "Navigating to home screen")
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        composable("home") {
            HomeScreenNew(
                onCollegeSelected = { collegeName ->
                    android.util.Log.d("Navigation", "Selected college: $collegeName")
                    navController.navigate("ar/$collegeName")
                },
                onAboutClicked = {
                    navController.navigate("about")
                }
            )
        }
        
        composable("about") {
            AboutScreenNew(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("ar/{collegeName}") { backStackEntry ->
            val collegeName = backStackEntry.arguments?.getString("collegeName") ?: "GVPCE"
            android.util.Log.d("Navigation", "Starting CameraX AR (simulated) for: $collegeName")
            ArScreen(collegeName = collegeName)
        }
    }
}

@Composable
fun PermissionDeniedScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.1f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️ Permission Required",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Location permission is required for the AR treasure hunt experience. Please grant location access to continue.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Grant Permission", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ErrorScreen(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.1f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🚨 Error",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Something went wrong: $error",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Please check your Firebase configuration and try again.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun AuthScreenNew(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val coroutineScope = rememberCoroutineScope()
    
    fun authenticateUser() {
        isLoading = true
        errorMessage = ""
        
        // Mock authentication - for testing purposes
        if ((email == "test@example.com" && password == "password123") || email.isNotEmpty()) {
            android.util.Log.d("AuthScreen", "Mock authentication successful")
            isLoading = false
            onLoginSuccess()
            return
        }
        
        isLoading = false
        errorMessage = "Please use test credentials or enter any email"
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(id = com.vinee.arcampustreasurehunt.R.drawable.background3), contentScale = ContentScale.Crop)
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title block matching Home screen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("   AR CAMPUS", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
                    Text("    TREASURE HUNT", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("      Welcome", fontSize = 16.sp, color = Color.Black, textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
        
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { authenticateUser() },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("SIGN IN", fontWeight = FontWeight.Bold)
                        }
                    }
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Test Credentials", color = Color.Black, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { email = "test@example.com"; password = "password123" }
                    ) {
                        Text("Use Test Login", fontSize = 12.sp)
                    }
                }
            }
        }

        // Footer attribution
        Text(
            text = "Made by Malla Sai Vineela",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun HomeScreenNew(onCollegeSelected: (String) -> Unit, onAboutClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(id = com.vinee.arcampustreasurehunt.R.drawable.background3), contentScale = ContentScale.Crop)
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("   AR CAMPUS", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center) // Black text for visibility
                    Text("    TREASURE HUNT", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center) // Black text
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("      Choose your college to start!", fontSize = 16.sp, color = Color.Black, textAlign = TextAlign.Center) // Black text
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)), // Pastel sky blue
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f))
            ) {
                Button(
                    onClick = { onCollegeSelected("GVPCE") },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏛️ GVPCE", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text("Gayatri Vidya Parishad College", fontSize = 12.sp, color = Color(0xFF1976D2))
                        Text("📍 Visakhapatnam", fontSize = 12.sp, color = Color(0xFF1E88E5))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)), // Pastel yellow
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f))
            ) {
                Button(
                    onClick = { onCollegeSelected("MVGR") },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏛️ MVGR", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Maharaj Vijayaram Gajapathi Raj", fontSize = 12.sp, color = Color.Black.copy(alpha = 0.85f))
            Text("📍 Vizianagaram", fontSize = 12.sp, color = Color.Black.copy(alpha = 0.75f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // About the App Button
            Card(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7)), // Light pastel purple
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f))
            ) {
                Button(
                    onClick = onAboutClicked,
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ℹ️ ABOUT THE APP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
                        Text("Learn more about this treasure hunt", fontSize = 12.sp, color = Color(0xFF9C27B0))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Attribution
            Text(
                text = "Made by Malla Sai Vineela",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7f), // Changed to dark color for white background
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AboutScreenNew(onBackClicked: () -> Unit) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE1BEE7), Color(0xFFFFF9C4)) // home pastel purple → pastel yellow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = onBackClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFF9C4) // pastel yellow button
                    )
                ) {
                    Text("← Back", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "🗺️ AR CAMPUS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "TREASURE HUNT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Welcome to the ultimate campus exploration experience! This innovative AR-based treasure hunt app transforms the way you discover and learn about college campuses.",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Features Section
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // back to white
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "🎯 Key Features:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "🔍 AR Clue System - Interactive augmented reality clues guide you to each building",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "📍 GPS Location Tracking - Real-time location detection to verify your discoveries",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "🎬 Educational Videos - Learn about each building through engaging video content",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "🏆 Progressive Challenges - Complete all buildings to win the treasure hunt",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "🌐 College Information - Access official college websites for more details",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Supported Colleges
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // back to white
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "🏫 Supported Colleges:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "• GVPCE - Gayatri Vidya Parishad College of Engineering, Visakhapatnam\n",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                                lineHeight = 18.sp

                            )
                            Text(
                                text = "• MVGR - Maharaj Vijayaram Gajapathi Raj College of Engineering, Vizianagaram\n",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                                lineHeight = 18.sp

                            )
                            Text(
                                text = "• Both colleges feature comprehensive building exploration with unique clues\n",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                                lineHeight = 18.sp

                            )
                            Text(
                                text = "• Each campus offers 5-7 buildings to discover through AR-guided treasure hunts\n",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                                lineHeight = 18.sp

                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // How to Play
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // back to white
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.Black.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "🎮 How to Play:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "1. Select your college from the home screen\n2. Follow AR clues to find blocks\n3. Use GPS tracking to reach target locations\n4. Watch videos when you arrive at the correct block using AR\n5. Complete all buildings to win!\n6. Explore college websites for more information",
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Made by Malla Sai Vineela",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}
