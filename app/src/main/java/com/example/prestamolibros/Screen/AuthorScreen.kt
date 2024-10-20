package com.example.prestamolibros.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.prestamolibros.Repository.AuthorRepository
import com.example.prestamolibros.model.Author
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestamolibros.database.LoanSystemDatabase

class AuthorViewModel(private val repository: AuthorRepository) : ViewModel() {
    var nombre by mutableStateOf("")
    var apellido by mutableStateOf("")
    var nacionalidad by mutableStateOf("")

    var errorNombre by mutableStateOf("")
    var errorApellido by mutableStateOf("")
    var errorNacionalidad by mutableStateOf("")

    var isSuccess by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    // Lista para almacenar autores
    var authorsList by mutableStateOf(listOf<Author>())

    // Variables para la actualización
    var isUpdating by mutableStateOf(false) // Controla si estamos en modo actualización
    var authorToUpdate: Author? = null // Guarda el autor que se está actualizando

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
                    // Se eliminó la llamada a getAuthors() aquí
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al registrar el autor: ${e.message}"
                }
            }
        }
    }

    // Función para obtener la lista de autores
    fun getAuthors() {
        viewModelScope.launch {
            authorsList = repository.getAllAuthors() // Actualiza la lista de autores
        }
    }

    // Función para iniciar la actualización de un autor (cargar los datos en el formulario)
    fun updateAuthorDetails(author: Author) {
        nombre = author.nombre
        apellido = author.apellido
        nacionalidad = author.nacionalidad

        isUpdating = true // Cambia al modo actualización
        authorToUpdate = author // Guarda el autor que se está actualizando
    }

    // Función para realizar la actualización del autor
    fun updateAuthor() {
        if (validateFields() && authorToUpdate != null) {
            viewModelScope.launch {
                try {
                    // Crear un nuevo autor con los datos actualizados
                    val updatedAuthor = authorToUpdate!!.copy(
                        nombre = nombre,
                        apellido = apellido,
                        nacionalidad = nacionalidad
                    )

                    repository.updateAuthor(updatedAuthor)
                    isSuccess = true
                    successMessage = "El autor ${updatedAuthor.nombre} ${updatedAuthor.apellido} ha sido actualizado con éxito."
                    clearFields()
                    getAuthors() // Volver a cargar la lista de autores
                    resetForm() // Restablecer el estado del formulario
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al actualizar el autor: ${e.message}"
                }
            }
        }
    }

    fun deleteAuthor(author: Author) {
        viewModelScope.launch {
            try {
                repository.deleteAuthor(author)
                isSuccess = true
                successMessage = "El autor ${author.nombre} ${author.apellido} ha sido eliminado con éxito."
                getAuthors() // Volver a cargar la lista de autores
            } catch (e: Exception) {
                isSuccess = false
                successMessage = "Error al eliminar el autor: ${e.message}"
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

    // Función para restablecer el formulario después de la actualización
    private fun resetForm() {
        isUpdating = false
        authorToUpdate = null
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
fun AuthorForm(viewModel: AuthorViewModel, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    // Contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Espaciado alrededor del contenedor
        contentAlignment = Alignment.Center // Centrado vertical y horizontal
    ) {
        // Contenedor para el formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp)) // Fondo blanco y bordes redondeados
                .padding(16.dp), // Espaciado interno del contenedor
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.nombre = it },
                label = { Text("Nombre del Autor") },
                isError = viewModel.errorNombre.isNotEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White // Color de fondo blanco
                )
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
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White // Color de fondo blanco
                )
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
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White // Color de fondo blanco
                )
            )
            if (viewModel.errorNacionalidad.isNotEmpty()) {
                Text(text = viewModel.errorNacionalidad, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para registrar o actualizar el autor
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (viewModel.isUpdating) {
                            viewModel.updateAuthor() // Actualizar autor
                        } else {
                            viewModel.insertAuthor() // Registrar autor
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Color del botón
            ) {
                Text(if (viewModel.isUpdating) "Actualizar Autor" else "Registrar Autor", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para listar autores
            Button(
                onClick = {
                    viewModel.getAuthors() // Llama a la función para obtener autores solo al hacer clic en este botón
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Color del botón
            ) {
                Text("Listar Autores", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver al menú principal
            Button(
                onClick = { navController.navigate("main_screen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Color del botón
            ) {
                Text(text = "Volver al Menú Principal", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar la lista de autores usando LazyColumn
            LazyColumn {
                items(viewModel.authorsList) { author ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Nombre: ${author.nombre} ${author.apellido}")
                            Text(text = "Nacionalidad: ${author.nacionalidad}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                // Botón para actualizar autor
                                Button(
                                    onClick = {
                                        viewModel.updateAuthorDetails(author) // Cargar datos del autor para actualizar
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Color del botón
                                ) {
                                    Text("Actualizar", color = Color.White)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Botón para eliminar autor
                                Button(
                                    onClick = {
                                        viewModel.deleteAuthor(author) // Eliminar el autor
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Color del botón
                                ) {
                                    Text("Eliminar", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AuthorScreen(navController: NavController) {
    // Instanciar el AuthorDao aquí
    val context = LocalContext.current
    val db = LoanSystemDatabase.getDatabase(context) // Asegúrate de tener acceso a tu base de datos
    val authorDao = db.authorDao() // Obtener el DAO de autores

    // Instanciar el AuthorViewModel usando la fábrica
    val repository = AuthorRepository(authorDao) // Pasar el authorDao al repositorio
    val viewModel: AuthorViewModel = viewModel(
        factory = AuthorViewModelFactory(repository)
    )

    // Fondo degradado
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0b76d6), Color(0xFF0eb3aa))
    )

    // Contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient), // Aplicar el fondo degradado
        contentAlignment = Alignment.Center // Centrar el contenido
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Registrar Autor",
                fontSize = 32.sp, // Tamaño de fuente más grande
                fontWeight = FontWeight.Bold, // Texto en negrita
                color = Color.White, // Color del texto blanco
                modifier = Modifier.padding(bottom = 16.dp) // Espaciado debajo del título
            )

            // Pasar el navController a AuthorForm
            AuthorForm(viewModel = viewModel, navController = navController)

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isSuccess) {
                Text(text = viewModel.successMessage, color = Color.Green)
            }

            // Botón para volver al menú principal
            Button(onClick = { navController.navigate("main_screen") }) {
                Text(text = "Volver al Menú Principal")
            }
        }
    }
}
