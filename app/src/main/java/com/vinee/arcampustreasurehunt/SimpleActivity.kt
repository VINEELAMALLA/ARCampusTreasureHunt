package com.vinee.arcampustreasurehunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vinee.arcampustreasurehunt.ui.theme.ARCampusTreasureHuntTheme

class SimpleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARCampusTreasureHuntTheme {
                SimpleScreen()
            }
        }
    }
}

@Composable
fun SimpleScreen() {
    var currentScreen by remember { mutableStateOf("login") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentScreen) {
            "login" -> {
                Text("AR Campus Treasure Hunt", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            currentScreen = "home"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(
                    onClick = {
                        email = "test@example.com"
                        password = "password123"
                    }
                ) {
                    Text("Use Test Credentials")
                }
            }
            
            "home" -> {
                Text("Welcome to AR Campus Treasure Hunt!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { currentScreen = "ar_gvpce" },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("GVPCE")
                }
                
                Button(
                    onClick = { currentScreen = "ar_mvgr" },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("MVGR")
                }
                
                Button(
                    onClick = { currentScreen = "about" },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("ABOUT THE PROJECT")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = { currentScreen = "login" }
                ) {
                    Text("Logout")
                }
            }
            
            "ar_gvpce" -> {
                Text("GVPCE Treasure Hunt", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Location tracking would start here...")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { currentScreen = "home" }) {
                    Text("Back to Home")
                }
            }
            
            "ar_mvgr" -> {
                Text("MVGR Treasure Hunt", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Location tracking would start here...")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { currentScreen = "home" }) {
                    Text("Back to Home")
                }
            }
            
            "about" -> {
                Text("About the Project", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "AR Campus Treasure Hunt is an innovative application that combines augmented reality with location-based gaming to create an engaging campus exploration experience.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { currentScreen = "home" }) {
                    Text("Back to Home")
                }
            }
        }
    }
}
