package com.example.prestamolibros.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "miembros")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val miembroId: Int = 0,
    val nombre: String,
    val apellido: String,
    val fechaInscripcion: String // Usaremos String para manejar la fecha como texto
)
