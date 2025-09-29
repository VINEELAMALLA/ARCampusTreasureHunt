package com.vinee.arcampustreasurehunt.navigation

sealed class Screen(val route: String) {
    object AuthScreen : Screen("auth_screen")
    object HomeScreen : Screen("home_screen")
    object AboutScreen : Screen("about_screen")
    object ArScreen : Screen("ar_screen/{collegeName}") {
        fun createRoute(collegeName: String) = "ar_screen/$collegeName"
    }
}
