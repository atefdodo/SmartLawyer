package com.smartlawyer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "clients",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["phoneNumber"])
    ]
)
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val powerOfAttorneyNumber: String,
    val powerOfAttorneyImageUri: String? = null,
    val documents: String = "", // JSON string containing document paths
    val images: String = "" // JSON string containing image paths
)