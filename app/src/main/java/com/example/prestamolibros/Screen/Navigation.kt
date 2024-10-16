package com.example.prestamolibros.Screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(navController)
        }
        composable("author_screen") {
            AuthorScreen(navController)
        }
        composable("book_screen") {
            BookScreen(navController)
        }
        composable("member_screen") {
            MemberScreen(navController)
        }
        composable("loan_screen") {
            LoanScreen(navController)
        }
    }
}