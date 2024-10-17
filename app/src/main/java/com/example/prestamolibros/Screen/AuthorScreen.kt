package com.example.prestamolibros.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.prestamolibros.DAO.AuthorDao
import com.example.prestamolibros.Repository.AuthorRepository
import com.example.prestamolibros.model.Author
import com.example.prestamolibros.database.LoanSystemDatabase // Asegúrate de que la ruta es correcta
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

class AuthorViewModel(private val repository: AuthorRepository) : ViewModel() {
    var nombre by mutableStateOf("")
    var apellido by mutableStateOf("")
    var nacionalidad by mutableStateOf("")

    var errorNombre by mutableStateOf("")
    var errorApellido by mutableStateOf("")
    var errorNacionalidad by mutableStateOf("")

    var isSuccess by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    fun insertAuthor() {
        if (validateFields()) {
            val author = Author(
                nombre = nombre,
                apellido = apellido,
                nacionalidad = nacionalidad
            )

            // Lanzar la inserción dentro de una corrutina
            viewModelScope.launch {
                try {
                    repository.insertAuthor(author)
                    isSuccess = true
                    successMessage = "El autor $nombre $apellido ha sido registrado con éxito."
                    clearFields()
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al registrar el autor: ${e.message}"
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else ""
        errorApellido = if (apellido.isBlank()) "El apellido es obligatorio" else ""
        errorNacionalidad = if (nacionalidad.isBlank()) "La nacionalidad es obligatoria" else ""

        return errorNombre.isEmpty() && errorApellido.isEmpty() && errorNacionalidad.isEmpty()
    }

    private fun clearFields() {
        nombre = ""
        apellido = ""
        nacionalidad = ""
        errorNombre = ""
        errorApellido = ""
        errorNacionalidad = ""
    }
}

class AuthorViewModelFactory(private val repository: AuthorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorForm(viewModel: AuthorViewModel) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            label = { Text("Nombre del Autor") },
            isError = viewModel.errorNombre.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        if (viewModel.errorNombre.isNotEmpty()) {
            Text(text = viewModel.errorNombre, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.apellido,
            onValueChange = { viewModel.apellido = it },
            label = { Text("Apellido del Autor") },
            isError = viewModel.errorApellido.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        if (viewModel.errorApellido.isNotEmpty()) {
            Text(text = viewModel.errorApellido, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.nacionalidad,
            onValueChange = { viewModel.nacionalidad = it },
            label = { Text("Nacionalidad") },
            isError = viewModel.errorNacionalidad.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )
        if (viewModel.errorNacionalidad.isNotEmpty()) {
            Text(text = viewModel.errorNacionalidad, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para registrar el autor
        Button(onClick = {
            coroutineScope.launch {
                viewModel.insertAuthor() // Llamada directa a la función
            }
        }) {
            Text("Registrar Autor")
        }

        // Mostrar mensaje de éxito si la inserción es exitosa
        if (viewModel.isSuccess) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = viewModel.successMessage, color = MaterialTheme.colorScheme.primary)
        }
    }
}

// Composable principal para mostrar el formulario
@Composable
fun AuthorScreen(navController: NavController, viewModel: AuthorViewModel) {
    // Instanciar el AuthorDao aquí
    val context = LocalContext.current
    val db = LoanSystemDatabase.getDatabase(context) // Asegúrate de tener acceso a tu base de datos
    val authorDao = db.authorDao() // Obtener el DAO de autores

    // Instanciar el AuthorViewModel usando la fábrica
    val repository = AuthorRepository(authorDao) // Pasar el authorDao al repositorio
    val viewModel: AuthorViewModel = viewModel(
        factory = AuthorViewModelFactory(repository)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registrar Autor")

        // Pasa el viewModel a AuthorForm
        AuthorForm(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isSuccess) {
            Text(text = viewModel.successMessage, color = Color.Green)
        }

        Button(onClick = { navController.navigate("main_screen") }) {
            Text(text = "Volver al Menú Principal")
        }
    }
}

