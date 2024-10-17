package com.example.prestamolibros.Screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prestamolibros.Repository.AuthorRepository
import com.example.prestamolibros.database.LoanSystemDatabase


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") { MainScreen(navController) }

        composable("author_screen") {
            // Instanciar el AuthorDao aquí
            val context = LocalContext.current
            val db = LoanSystemDatabase.getDatabase(context) // Asegúrate de tener acceso a tu base de datos
            val authorDao = db.authorDao() // Obtener el DAO de autores

            // Instanciar el AuthorRepository y AuthorViewModel
            val repository = AuthorRepository(authorDao)
            val authorViewModel: AuthorViewModel = viewModel(factory = AuthorViewModelFactory(repository))

            AuthorScreen(navController)
        }

        composable("book_screen") { BookScreen(navController) }
        composable("loan_screen") { LoanScreen(navController) }
        composable("member_screen") { MemberScreen(navController) }
    }
}
