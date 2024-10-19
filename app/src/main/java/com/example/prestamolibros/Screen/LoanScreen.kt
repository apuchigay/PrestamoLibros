package com.example.prestamolibros.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.prestamolibros.Repository.LoanRepository
import com.example.prestamolibros.model.Loan
import com.example.prestamolibros.model.Book
import com.example.prestamolibros.model.Member
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prestamolibros.R
import com.example.prestamolibros.database.LoanSystemDatabase
import com.example.prestamolibros.model.Author

class LoanViewModel(private val repository: LoanRepository) : ViewModel() {
    var fechaPrestamo by mutableStateOf("")
    var fechaDevolucion by mutableStateOf("")
    var selectedBook by mutableStateOf<Book?>(null) // Libro seleccionado
    var selectedMember by mutableStateOf<Member?>(null) // Miembro seleccionado

    var errorFechaPrestamo by mutableStateOf("")
    var errorFechaDevolucion by mutableStateOf("")
    var errorBook by mutableStateOf("")
    var errorMember by mutableStateOf("")

    var isSuccess by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    var loanList by mutableStateOf(listOf<Loan>())

    // Variables para los dropdowns
    var booksList by mutableStateOf(listOf<Book>())
    var membersList by mutableStateOf(listOf<Member>())
    var expandedBooks by mutableStateOf(false)
    var expandedMembers by mutableStateOf(false)

    // Variables para la actualización
    var isUpdating by mutableStateOf(false) // Controla si estamos en modo actualización
    var loanToUpdate: Loan? = null // Guarda el préstamo que se está actualizando

    fun insertLoan() {
        if (validateFields()) {
            val loan = Loan(
                fechaPrestamo = fechaPrestamo,
                fechaDevolucion = fechaDevolucion,
                libroId = selectedBook?.libroId ?: 0, // Relacionar con el ID del libro seleccionado
                miembroId = selectedMember?.miembroId ?: 0 // Relacionar con el ID del miembro seleccionado
            )

            viewModelScope.launch {
                try {
                    repository.insertLoan(loan)
                    isSuccess = true
                    successMessage = "El préstamo ha sido registrado con éxito."
                    clearFields()
                    getLoans()
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al registrar el préstamo: ${e.message}"
                }
            }
        }
    }

    fun getLoans() {
        viewModelScope.launch {
            loanList = repository.getAllLoans()
        }
    }

    fun getBooksAndMembers() {
        viewModelScope.launch {
            booksList = repository.getAllBooks()
            membersList = repository.getAllMembers()
        }
    }

    // Función para iniciar la actualización de un préstamo
    fun updateLoanDetails(loan: Loan) {
        fechaPrestamo = loan.fechaPrestamo
        fechaDevolucion = loan.fechaDevolucion.toString()
        selectedBook = booksList.find { it.libroId == loan.libroId }
        selectedMember = membersList.find { it.miembroId == loan.miembroId }

        isUpdating = true // Cambia al modo actualización
        loanToUpdate = loan // Guarda el préstamo que se está actualizando
    }

    // Función para realizar la actualización del préstamo
    fun updateLoan() {
        if (validateFields() && loanToUpdate != null) {
            viewModelScope.launch {
                try {
                    val updatedLoan = loanToUpdate!!.copy(
                        fechaPrestamo = fechaPrestamo,
                        fechaDevolucion = fechaDevolucion,
                        libroId = selectedBook?.libroId ?: 0,
                        miembroId = selectedMember?.miembroId ?: 0
                    )

                    repository.updateLoan(updatedLoan)
                    isSuccess = true
                    successMessage = "El préstamo ha sido actualizado con éxito."
                    clearFields()
                    getLoans()
                    resetForm()
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al actualizar el préstamo: ${e.message}"
                }
            }
        }
    }

    // Función para eliminar un préstamo
    fun deleteLoan(loan: Loan) {
        viewModelScope.launch {
            try {
                repository.deleteLoan(loan)
                isSuccess = true
                successMessage = "El préstamo ha sido eliminado con éxito."
                getLoans()
            } catch (e: Exception) {
                isSuccess = false
                successMessage = "Error al eliminar el préstamo: ${e.message}"
            }
        }
    }

    private fun validateFields(): Boolean {
        errorFechaPrestamo = if (fechaPrestamo.isBlank()) "La fecha de préstamo es obligatoria" else ""
        errorFechaDevolucion = if (fechaDevolucion.isBlank()) "La fecha de devolución es obligatoria" else ""
        errorBook = if (selectedBook == null) "Debes seleccionar un libro" else ""
        errorMember = if (selectedMember == null) "Debes seleccionar un miembro" else ""

        return errorFechaPrestamo.isEmpty() && errorFechaDevolucion.isEmpty() && errorBook.isEmpty() && errorMember.isEmpty()
    }

    private fun clearFields() {
        fechaPrestamo = ""
        fechaDevolucion = ""
        selectedBook = null
        selectedMember = null
        errorFechaPrestamo = ""
        errorFechaDevolucion = ""
        errorBook = ""
        errorMember = ""
    }

    // Función para restablecer el formulario después de la actualización
    private fun resetForm() {
        isUpdating = false
        loanToUpdate = null
    }
}

class LoanViewModelFactory(private val repository: LoanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanForm(viewModel: LoanViewModel, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    //LaunchedEffect(Unit) {
    //    viewModel.getBooksAndMembers() // Cargar libros y miembros al iniciar
    //    viewModel.getLoans() // Cargar préstamos al iniciar
    //}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = viewModel.fechaPrestamo,
            onValueChange = { viewModel.fechaPrestamo = it },
            label = { Text("Fecha de Préstamo") },
            isError = viewModel.errorFechaPrestamo.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        if (viewModel.errorFechaPrestamo.isNotEmpty()) {
            Text(text = viewModel.errorFechaPrestamo, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.fechaDevolucion,
            onValueChange = { viewModel.fechaDevolucion = it },
            label = { Text("Fecha de Devolución") },
            isError = viewModel.errorFechaDevolucion.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        if (viewModel.errorFechaDevolucion.isNotEmpty()) {
            Text(text = viewModel.errorFechaDevolucion, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown para seleccionar libro
        MyDropMenuBooks(
            bookList = viewModel.booksList,
            selectedBook = viewModel.selectedBook,
            onBookSelected = { selectedBook ->
                viewModel.selectedBook = selectedBook
            }
        )

        if (viewModel.errorBook.isNotEmpty()) {
            Text(text = viewModel.errorBook, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown para seleccionar miembro
        MyDropMenuMembers(
            memberList = viewModel.membersList,
            selectedMember = viewModel.selectedMember,
            onMemberSelected = { selectedMember ->
                viewModel.selectedMember = selectedMember
            }
        )

        if (viewModel.errorMember.isNotEmpty()) {
            Text(text = viewModel.errorMember, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de acción para registrar o actualizar préstamo
        Button(onClick = {
            coroutineScope.launch {
                if (viewModel.isUpdating) {
                    viewModel.updateLoan() // Actualizar préstamo
                } else {
                    viewModel.insertLoan() // Registrar préstamo
                }
            }
        }) {
            Text(if (viewModel.isUpdating) "Actualizar Préstamo" else "Registrar Préstamo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para listar los préstamos
        Button(onClick = {
            viewModel.getLoans() // Cargar préstamos
        }) {
            Text("Listar Préstamos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver al menú principal
        Button(onClick = { navController.navigate("main_screen") }) {
            Text(text = "Volver al Menú Principal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de préstamos con LazyColumn
        LazyColumn {
            items(viewModel.loanList) { loan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Libro: ${viewModel.booksList.firstOrNull { it.libroId == loan.libroId }?.let { it.titulo } ?: "Desconocido"}")
                        Text(text = "Miembro: ${viewModel.membersList.firstOrNull { it.miembroId == loan.miembroId }?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido"}")
                        Text(text = "Fecha de Préstamo: ${loan.fechaPrestamo}")
                        Text(text = "Fecha de Devolución: ${loan.fechaDevolucion}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            // Botón para actualizar préstamo
                            Button(onClick = {
                                viewModel.updateLoanDetails(loan) // Cargar datos del préstamo para actualizar
                            }) {
                                Text("Actualizar")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Botón para eliminar préstamo
                            Button(onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteLoan(loan) // Eliminar el préstamo
                                }
                            }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Mostrar mensajes de éxito o error
    if (viewModel.isSuccess) {
        Toast.makeText(LocalContext.current, viewModel.successMessage, Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun LoanScreen(navController: NavController) {
    val context = LocalContext.current
    val db = LoanSystemDatabase.getDatabase(context) // Obtén acceso a la base de datos

    // Obtén los DAOs de préstamos, libros y miembros
    val loanDao = db.loanDao()
    val bookDao = db.bookDao()
    val memberDao = db.memberDao()

    // Instanciar el LoanRepository con todos los DAOs
    val repository = LoanRepository(loanDao, bookDao, memberDao)
    val viewModel: LoanViewModel = viewModel(
        factory = LoanViewModelFactory(repository)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registrar Préstamo")

        // Formulario de préstamo
        LoanForm(viewModel = viewModel, navController = navController)

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra un mensaje de éxito si el préstamo fue registrado correctamente
        if (viewModel.isSuccess) {
            Text(text = viewModel.successMessage, color = Color.Green)
        }

        // Botón para volver a la pantalla principal
        Button(onClick = { navController.navigate("main_screen") }) {
            Text(text = "Volver al Menú Principal")
        }
    }
}

@Composable
fun MyDropMenuBooks(
    bookList: List<Book>, // Lista de libros
    selectedBook: Book?, // Libro seleccionado
    onBookSelected: (Book) -> Unit // Acción cuando se selecciona un libro
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón que despliega el menú
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedBook?.let { it.titulo } ?: "Seleccionar Libro")
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                contentDescription = "DropDown Icon"
            )
        }

        // Menú desplegable
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                bookList.forEach { book ->
                    TextButton(
                        onClick = {
                            onBookSelected(book)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = book.titulo) // Mostrar el título del libro
                    }
                }
            }
        }
    }
}

@Composable
fun MyDropMenuMembers(
    memberList: List<Member>, // Lista de miembros
    selectedMember: Member?, // Miembro seleccionado
    onMemberSelected: (Member) -> Unit // Acción cuando se selecciona un miembro
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón que despliega el menú
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedMember?.let { "${it.nombre} ${it.apellido}" } ?: "Seleccionar Miembro")
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                contentDescription = "DropDown Icon"
            )
        }

        // Menú desplegable
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                memberList.forEach { member ->
                    TextButton(
                        onClick = {
                            onMemberSelected(member)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "${member.nombre} ${member.apellido}")
                    }
                }
            }
        }
    }
}
