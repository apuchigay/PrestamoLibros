package com.example.prestamolibros.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.prestamolibros.Repository.BookRepository
import com.example.prestamolibros.model.Author
import com.example.prestamolibros.model.Book
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestamolibros.R
import com.example.prestamolibros.database.LoanSystemDatabase

class BookViewModel(private val repository: BookRepository) : ViewModel() {
    var titulo by mutableStateOf("")
    var genero by mutableStateOf("")
    var selectedAuthor by mutableStateOf<Author?>(null) // Autor seleccionado

    var errorTitulo by mutableStateOf("")
    var errorGenero by mutableStateOf("")
    var errorAuthor by mutableStateOf("")

    var isSuccess by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    var bookList by mutableStateOf(listOf<Book>())

    // Variables para el dropdown de autores
    var authorsList by mutableStateOf(listOf<Author>())
    var expanded by mutableStateOf(false)

    // Variables para la actualización
    var isUpdating by mutableStateOf(false) // Controla si estamos en modo actualización
    var bookToUpdate: Book? = null // Guarda el libro que se está actualizando

    fun insertBook() {
        if (validateFields()) {
            val book = Book(
                titulo = titulo,
                genero = genero,
                autorId = selectedAuthor?.autorId
                    ?: 0 // Relacionar con el ID del autor seleccionado
            )

            viewModelScope.launch {
                try {
                    repository.insertBook(book)
                    isSuccess = true
                    successMessage = "El libro '$titulo' ha sido registrado con éxito."
                    clearFields()
                    getBooks()
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al registrar el libro: ${e.message}"
                }
            }
        }
    }

    fun getBooks() {
        viewModelScope.launch {
            bookList = repository.getAllBooks()
        }
    }

    fun getAuthors() {
        viewModelScope.launch {
            authorsList = repository.getAllAuthors() // Obtener autores para el Dropdown
        }
    }

    // Función para iniciar la actualización de un libro (cargar los datos en el formulario)
    fun updateBookDetails(book: Book) {
        titulo = book.titulo
        genero = book.genero
        selectedAuthor =
            authorsList.find { it.autorId == book.autorId } // Seleccionar el autor correspondiente

        isUpdating = true // Cambia al modo actualización
        bookToUpdate = book // Guarda el libro que se está actualizando
    }

    // Función para realizar la actualización del libro
    fun updateBook() {
        if (validateFields() && bookToUpdate != null) {
            viewModelScope.launch {
                try {
                    // Crear un nuevo libro con los datos actualizados
                    val updatedBook = bookToUpdate!!.copy(
                        titulo = titulo,
                        genero = genero,
                        autorId = selectedAuthor?.autorId ?: 0
                    )

                    repository.updateBook(updatedBook)
                    isSuccess = true
                    successMessage =
                        "El libro '${updatedBook.titulo}' ha sido actualizado con éxito."
                    clearFields()
                    getBooks() // Volver a cargar la lista de libros
                    resetForm() // Restablecer el estado del formulario
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al actualizar el libro: ${e.message}"
                }
            }
        }
    }

    // Función para eliminar un libro
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                repository.deleteBook(book)
                isSuccess = true
                successMessage = "El libro '${book.titulo}' ha sido eliminado con éxito."
                getBooks() // Volver a cargar la lista de libros
            } catch (e: Exception) {
                isSuccess = false
                successMessage = "Error al eliminar el libro: ${e.message}"
            }
        }
    }

    private fun validateFields(): Boolean {
        errorTitulo = if (titulo.isBlank()) "El título es obligatorio" else ""
        errorGenero = if (genero.isBlank()) "El género es obligatorio" else ""
        errorAuthor = if (selectedAuthor == null) "Debes seleccionar un autor" else ""

        return errorTitulo.isEmpty() && errorGenero.isEmpty() && errorAuthor.isEmpty()
    }

    private fun clearFields() {
        titulo = ""
        genero = ""
        selectedAuthor = null
        errorTitulo = ""
        errorGenero = ""
        errorAuthor = ""
    }

    // Función para restablecer el formulario después de la actualización
    private fun resetForm() {
        isUpdating = false
        bookToUpdate = null
    }
}


class BookViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookForm(viewModel: BookViewModel, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getAuthors() // Cargar autores cuando la vista se inicialice
    }

    // Fondo degradado
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0b76d6), Color(0xFF0eb3aa))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient) // Aplicar el degradado como fondo
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registrar Libro",
            fontSize = 24.sp, // Tamaño de fuente más grande
            fontWeight = FontWeight.Bold, // Negrita
            color = Color.White, // Color del texto
            modifier = Modifier.padding(bottom = 16.dp) // Espaciado
        )

        // Contenedor para el formulario con fondo blanco y bordes redondeados
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campos de texto sin fondo
            TextField(
                value = viewModel.titulo,
                onValueChange = { viewModel.titulo = it },
                label = { Text("Título del Libro") },
                isError = viewModel.errorTitulo.isNotEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent) // Sin fondo
            )
            if (viewModel.errorTitulo.isNotEmpty()) {
                Text(text = viewModel.errorTitulo, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.genero,
                onValueChange = { viewModel.genero = it },
                label = { Text("Género") },
                isError = viewModel.errorGenero.isNotEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent) // Sin fondo
            )
            if (viewModel.errorGenero.isNotEmpty()) {
                Text(text = viewModel.errorGenero, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menú desplegable para seleccionar el autor
            MyDropMenu(
                authorList = viewModel.authorsList,
                selectedAuthor = viewModel.selectedAuthor,
                onAuthorSelected = { selectedAuthor -> viewModel.selectedAuthor = selectedAuthor }
            )
            if (viewModel.errorAuthor.isNotEmpty()) {
                Text(text = viewModel.errorAuthor, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
            val buttonModifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (viewModel.isUpdating) {
                            viewModel.updateBook() // Actualizar libro
                        } else {
                            viewModel.insertBook() // Registrar libro
                        }
                    }
                },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Botón azul
            ) {
                Text(text = if (viewModel.isUpdating) "Actualizar Libro" else "Registrar Libro", color = Color.White)
            }

            Button(
                onClick = { viewModel.getBooks() },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6))
            ) {
                Text(text = "Listar Libros", color = Color.White)
            }

            // Mostrar éxito
            Spacer(modifier = Modifier.height(16.dp))
            if (viewModel.isSuccess) {
                Text(text = viewModel.successMessage, color = Color.Green)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("main_screen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6))
            ) {
                Text(text = "Volver al Menú Principal", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(viewModel.bookList) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Título: ${book.titulo}")
                            Text(text = "Género: ${book.genero}")
                            Text(
                                text = "Autor: ${
                                    viewModel.authorsList.firstOrNull { it.autorId == book.autorId }
                                        ?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido"
                                }"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                // Botón para actualizar libro
                                Button(
                                    onClick = {
                                        viewModel.updateBookDetails(book) // Cargar datos del libro para actualizar
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Botón azul
                                ) {
                                    Text("Actualizar", color = Color.White)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Botón para eliminar libro
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            viewModel.deleteBook(book) // Eliminar el libro
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0b76d6)) // Botón azul
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
fun BookScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LoanSystemDatabase.getDatabase(context)
    val bookDao = db.bookDao()

    val repository = BookRepository(bookDao)
    val viewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(repository)
    )

    // Fondo degradado
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0b76d6), Color(0xFF0eb3aa))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient) // Fondo degradado
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookForm(viewModel = viewModel, navController = navController)
    }
}

@Composable
fun MyDropMenu(
    authorList: List<Author>,
    selectedAuthor: Author?,
    onAuthorSelected: (Author) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Botón que despliega el menú
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedAuthor?.let { "${it.nombre} ${it.apellido}" } ?: "Seleccionar Autor")
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Menú desplegable
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                authorList.forEach { author ->
                    TextButton(
                        onClick = {
                            onAuthorSelected(author)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "${author.nombre} ${author.apellido}")
                    }
                }
            }
        }
    }
}
