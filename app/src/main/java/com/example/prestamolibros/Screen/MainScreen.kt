package com.example.prestamolibros.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    // Fondo degradado
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0b76d6), Color(0xFF0eb3aa))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient) // Aplicar el degradado como fondo
            .padding(16.dp), // Añadir un poco de padding al contenedor
        verticalArrangement = Arrangement.Center, // Centrado verticalmente
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sistema de Préstamo de Libros",
            fontSize = 24.sp, // Tamaño de fuente más grande
            fontWeight = FontWeight.Bold, // Texto en negrita
            color = Color.White, // Color del texto blanco
            modifier = Modifier.padding(bottom = 32.dp) // Espaciado debajo del título
        )

        // Contenedor para los botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp)) // Contenedor con fondo blanco y bordes redondeados
                .padding(16.dp), // Espaciado interno del contenedor
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val buttonModifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Espaciado vertical entre botones

            // Botones con color de fondo sólido
            Button(
                onClick = { navController.navigate("author_screen") },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Color de fondo de los botones
            ) {
                Text(text = "Registrar autor", color = Color.White) // Cambiar el color del texto a blanco
            }

            Button(
                onClick = { navController.navigate("book_screen") },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6))
            ) {
                Text(text = "Registrar Libro", color = Color.White)
            }

            Button(
                onClick = { navController.navigate("member_screen") },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6))
            ) {
                Text(text = "Registrar Miembro", color = Color.White)
            }

            Button(
                onClick = { navController.navigate("loan_screen") },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6))
            ) {
                Text(text = "Realizar Préstamo", color = Color.White)
            }
        }
    }
}