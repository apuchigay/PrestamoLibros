package com.example.prestamolibros.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sistema de Préstamo de Libros")
        Button(onClick = { navController.navigate("author_screen") }) {
            Text(text = "Registrar Autor")
        }
        Button(onClick = { navController.navigate("book_screen") }) {
            Text(text = "Registrar Libro")
        }
        Button(onClick = { navController.navigate("member_screen") }) {
            Text(text = "Registrar Miembro")
        }
        Button(onClick = { navController.navigate("loan_screen") }) {
            Text(text = "Realizar Préstamo")
        }
    }
}