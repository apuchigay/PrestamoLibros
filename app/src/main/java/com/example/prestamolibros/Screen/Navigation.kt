package com.example.prestamolibros.Screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") { MainScreen(navController) }
        composable("author_screen") {
            val authorViewModel: AuthorViewModel = viewModel()
            AuthorScreen(navController, authorViewModel)
        }
        composable("book_screen") { BookScreen(navController) }
        composable("loan_screen") { LoanScreen(navController) }
        composable("member_screen") { MemberScreen(navController) }
    }

}