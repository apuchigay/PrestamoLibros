package com.example.prestamolibros.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "prestamos",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["libroId"],
            childColumns = ["libroId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["miembroId"],
            childColumns = ["miembroId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Loan(
    @PrimaryKey(autoGenerate = true)
    val prestamoId: Int = 0,
    val libroId: Int,
    val miembroId: Int,
    val fechaPrestamo: String,
    val fechaDevolucion: String?
)
