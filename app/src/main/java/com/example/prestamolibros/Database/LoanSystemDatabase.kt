package com.example.prestamolibros.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.prestamolibros.DAO.AuthorDao
import com.example.prestamolibros.DAO.BookDao
import com.example.prestamolibros.DAO.LoanDao
import com.example.prestamolibros.DAO.MemberDao
import com.example.prestamolibros.model.*

@Database(
    entities = [Author::class, Book::class, Member::class, Loan::class],
    version = 1,
    exportSchema = false
)
abstract class LoanSystemDatabase : RoomDatabase() {

    abstract fun authorDao(): AuthorDao
    abstract fun bookDao(): BookDao
    abstract fun memberDao(): MemberDao
    abstract fun loanDao(): LoanDao

    companion object {
        @Volatile
        private var INSTANCE: LoanSystemDatabase? = null

        fun getDatabase(context: Context): LoanSystemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LoanSystemDatabase::class.java,
                    "loan_system_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
