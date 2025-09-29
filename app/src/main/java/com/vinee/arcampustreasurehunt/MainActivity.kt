package com.vinee.arcampustreasurehunt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import com.google.firebase.auth.FirebaseAuth
import com.vinee.arcampustreasurehunt.navigation.Screen
import com.vinee.arcampustreasurehunt.ui.about.AboutScreen
import com.vinee.arcampustreasurehunt.ui.ar.ArScreen
import com.vinee.arcampustreasurehunt.ui.auth.AuthScreen
import com.vinee.arcampustreasurehunt.ui.home.HomeScreen
import com.vinee.arcampustreasurehunt.ui.theme.ARCampusTreasureHuntTheme

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Creating MainActivity")
        try {
            setContent {
                ARCampusTreasureHuntTheme {
                    AppNavigator(auth)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }
    }
}

@Composable
fun AppNavigator(auth: FirebaseAuth) {
    val navController2 = rememberNavController()
    val startDestination = if (auth.currentUser != null) Screen.HomeScreen.route else Screen.AuthScreen.route
    
    Log.d("AppNavigator", "Sta rting with destination: $startDestination")

    NavHost(navController = navController2, startDestination = startDestination) {
        composable(Screen.AuthScreen.route) {
            Log.d("Navigation", "Showing AuthScreen")
            AuthScreen(navController2, auth)
        }
        composable(Screen.HomeScreen.route) {
            Log.d("Navigation", "Showing HomeScreen")
            HomeScreen(navController2)
        }
        composable(Screen.AboutScreen.route) {
            Log.d("Navigation", "Showing AboutScreen")
            AboutScreen(navController2)
        }
        composable(Screen.ArScreen.route) { backStackEntry ->
            Log.d("Navigation", "Showing ArScreen")
            val collegeName = backStackEntry.arguments?.getString("collegeName")
            if (collegeName != null) {
                ArScreen(collegeName)
            }
        }
    }
}
