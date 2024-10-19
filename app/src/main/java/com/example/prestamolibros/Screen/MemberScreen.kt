package com.example.prestamolibros.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.prestamolibros.Repository.MemberRepository
import com.example.prestamolibros.database.LoanSystemDatabase
import com.example.prestamolibros.model.Member
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

class MemberViewModel(private val repository: MemberRepository) : ViewModel() {
    var nombre by mutableStateOf("")
    var apellido by mutableStateOf("")
    var fechaInscripcion by mutableStateOf("")

    var errorNombre by mutableStateOf("")
    var errorApellido by mutableStateOf("")
    var errorFechaInscripcion by mutableStateOf("")

    var isSuccess by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    // Lista para almacenar miembros
    var membersList by mutableStateOf(listOf<Member>())

    // Variables para la actualización
    var isUpdating by mutableStateOf(false) // Controla si estamos en modo actualización
    var memberToUpdate: Member? = null // Guarda el miembro que se está actualizando

    fun insertMember() {
        if (validateFields()) {
            val member = Member(
                nombre = nombre,
                apellido = apellido,
                fechaInscripcion = fechaInscripcion
            )

            // Lanzar la inserción dentro de una corrutina
            viewModelScope.launch {
                try {
                    repository.insertMember(member)
                    isSuccess = true
                    successMessage = "El miembro $nombre $apellido ha sido registrado con éxito."
                    clearFields()
                    getMembers() // Actualizar la lista después de la inserción
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al registrar el miembro: ${e.message}"
                }
            }
        }
    }

    // Función para obtener la lista de miembros
    fun getMembers() {
        viewModelScope.launch {
            membersList = repository.getAllMembers() // Actualiza la lista de miembros
        }
    }

    // Función para iniciar la actualización de un miembro (cargar los datos en el formulario)
    fun updateMemberDetails(member: Member) {
        nombre = member.nombre
        apellido = member.apellido
        fechaInscripcion = member.fechaInscripcion

        isUpdating = true // Cambia al modo actualización
        memberToUpdate = member // Guarda el miembro que se está actualizando
    }

    // Función para realizar la actualización del miembro
    fun updateMember() {
        if (validateFields() && memberToUpdate != null) {
            viewModelScope.launch {
                try {
                    // Crear un nuevo miembro con los datos actualizados
                    val updatedMember = memberToUpdate!!.copy(
                        nombre = nombre,
                        apellido = apellido,
                        fechaInscripcion = fechaInscripcion
                    )

                    repository.updateMember(updatedMember)
                    isSuccess = true
                    successMessage = "El miembro ${updatedMember.nombre} ${updatedMember.apellido} ha sido actualizado con éxito."
                    clearFields()
                    getMembers() // Volver a cargar la lista de miembros
                    resetForm() // Restablecer el estado del formulario
                } catch (e: Exception) {
                    isSuccess = false
                    successMessage = "Error al actualizar el miembro: ${e.message}"
                }
            }
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            try {
                repository.deleteMember(member)
                isSuccess = true
                successMessage = "El miembro ${member.nombre} ${member.apellido} ha sido eliminado con éxito."
                getMembers() // Volver a cargar la lista de miembros
            } catch (e: Exception) {
                isSuccess = false
                successMessage = "Error al eliminar el miembro: ${e.message}"
            }
        }
    }

    private fun validateFields(): Boolean {
        errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else ""
        errorApellido = if (apellido.isBlank()) "El apellido es obligatorio" else ""
        errorFechaInscripcion = if (fechaInscripcion.isBlank()) "La fecha de inscripción es obligatoria" else ""

        return errorNombre.isEmpty() && errorApellido.isEmpty() && errorFechaInscripcion.isEmpty()
    }

    private fun clearFields() {
        nombre = ""
        apellido = ""
        fechaInscripcion = ""
        errorNombre = ""
        errorApellido = ""
        errorFechaInscripcion = ""
    }

    // Función para restablecer el formulario después de la actualización
    private fun resetForm() {
        isUpdating = false
        memberToUpdate = null
    }
}

class MemberViewModelFactory(private val repository: MemberRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberForm(viewModel: MemberViewModel, navController: NavController) {
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
            label = { Text("Nombre del Miembro") },
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
            label = { Text("Apellido del Miembro") },
            isError = viewModel.errorApellido.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        if (viewModel.errorApellido.isNotEmpty()) {
            Text(text = viewModel.errorApellido, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.fechaInscripcion,
            onValueChange = { viewModel.fechaInscripcion = it },
            label = { Text("Fecha de Inscripción") },
            isError = viewModel.errorFechaInscripcion.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )
        if (viewModel.errorFechaInscripcion.isNotEmpty()) {
            Text(text = viewModel.errorFechaInscripcion, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para registrar o actualizar el miembro
        Button(onClick = {
            coroutineScope.launch {
                if (viewModel.isUpdating) {
                    viewModel.updateMember() // Actualizar miembro
                } else {
                    viewModel.insertMember() // Registrar miembro
                }
            }
        }) {
            Text(if (viewModel.isUpdating) "Actualizar Miembro" else "Registrar Miembro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para listar miembros
        Button(onClick = {
            viewModel.getMembers() // Llama a la función para obtener miembros
        }) {
            Text("Listar Miembros")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver al menú principal
        Button(onClick = { navController.navigate("main_screen") }) {
            Text(text = "Volver al Menú Principal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la lista de miembros usando LazyColumn
        LazyColumn {
            items(viewModel.membersList) { member ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Nombre: ${member.nombre} ${member.apellido}")
                        Text(text = "Fecha de Inscripción: ${member.fechaInscripcion}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            // Botón para actualizar miembro
                            Button(onClick = {
                                viewModel.updateMemberDetails(member) // Cargar datos del miembro para actualizar
                            }) {
                                Text("Actualizar")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Botón para eliminar miembro
                            Button(onClick = {
                                viewModel.deleteMember(member) // Eliminar el miembro
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
fun MemberScreen(navController: NavController) {
    val database = LoanSystemDatabase.getDatabase(LocalContext.current)
    val repository = MemberRepository(database.memberDao())
    val memberViewModel: MemberViewModel = viewModel(factory = MemberViewModelFactory(repository))

    MemberForm(viewModel = memberViewModel, navController = navController)
}
