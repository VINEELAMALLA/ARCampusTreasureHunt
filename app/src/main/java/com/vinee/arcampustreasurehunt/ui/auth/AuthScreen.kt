package com.vinee.arcampustreasurehunt.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.vinee.arcampustreasurehunt.navigation.Screen

@Composable
fun AuthScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(id = com.vinee.arcampustreasurehunt.R.drawable.background3), contentScale = ContentScale.Crop)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(text = if (isLogin) "Login" else "Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
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
        
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all fields"
                    return@Button
                }
                
                isLoading = true
                errorMessage = ""
                
                // Simple mock authentication for testing
                if (email == "test@example.com" && password == "password123") {
                    // Mock successful authentication
                    isLoading = false
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.AuthScreen.route) { inclusive = true }
                    }
                    return@Button
                }
                
                // Try Firebase authentication for other credentials
                try {
                    if (isLogin) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    navController.navigate(Screen.HomeScreen.route) {
                                        popUpTo(Screen.AuthScreen.route) { inclusive = true }
                                    }
                                } else {
                                    errorMessage = task.exception?.message ?: "Login failed"
                                }
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                errorMessage = exception.message ?: "Authentication failed"
                            }
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    navController.navigate(Screen.HomeScreen.route) {
                                        popUpTo(Screen.AuthScreen.route) { inclusive = true }
                                    }
                                } else {
                                    errorMessage = task.exception?.message ?: "Sign up failed"
                                }
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                errorMessage = exception.message ?: "Authentication failed"
                            }
                    }
                } catch (e: Exception) {
                    isLoading = false
                    errorMessage = "Authentication service error: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLogin) "Login" else "Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { 
                isLogin = !isLogin
                errorMessage = ""
            }
        ) {
            Text(if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = {
                email = "test@example.com"
                password = "password123"
                errorMessage = ""
            }
        ) {
            Text("Use Test Credentials", style = MaterialTheme.typography.bodySmall)
        }
        }
    }
}
