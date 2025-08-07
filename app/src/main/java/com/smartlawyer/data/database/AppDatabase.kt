package com.smartlawyer.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.smartlawyer.data.dao.CaseDao
import com.smartlawyer.data.dao.ClientDao
import com.smartlawyer.data.entities.Case
import com.smartlawyer.data.entities.Client

@Database(
    entities = [Client::class, Case::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun caseDao(): CaseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                println("AppDatabase: Creating new database instance")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartlawyer_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        println("AppDatabase: Database created successfully")
                    }
                    
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        println("AppDatabase: Database opened successfully")
                    }
                })
                .build()
                INSTANCE = instance
                println("AppDatabase: Database instance created and cached")
                instance
            }
        }
    }
} 