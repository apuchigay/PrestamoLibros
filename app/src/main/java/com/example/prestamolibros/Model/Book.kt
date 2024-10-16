package com.example.prestamolibros.model

import Autor
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "libros",
    foreignKeys = [
        ForeignKey(
            entity = Autor::class,
            parentColumns = ["autorId"],
            childColumns = ["autorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    val libroId: Int = 0,
    val titulo: String,
    val genero: String,
    val autorId: Int
)
