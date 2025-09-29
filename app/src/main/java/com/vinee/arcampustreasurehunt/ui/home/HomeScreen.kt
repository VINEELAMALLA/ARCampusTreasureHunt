package com.vinee.arcampustreasurehunt.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vinee.arcampustreasurehunt.R
import com.vinee.arcampustreasurehunt.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
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
        // App Logo
        Image(
            painter = painterResource(id = com.vinee.arcampustreasurehunt.R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // App Name
        Text(
            text = "AR Campus Treasure Hunt",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // College Buttons
        Button(
            onClick = { navController.navigate(Screen.ArScreen.createRoute("GVPCE")) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("GVPCE")
        }

        Button(
            onClick = { navController.navigate(Screen.ArScreen.createRoute("MVGR")) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("MVGR")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Button
        Button(
            onClick = { navController.navigate(Screen.AboutScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ABOUT THE PROJECT")
        }
        }
    }
}
