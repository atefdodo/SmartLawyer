package com.smartlawyer.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cases",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["clientId"])]
)
data class Case(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val caseNumber: String,
    val caseYear: String,
    val registrationDate: String,
    val clientId: Long,
    val clientRole: String,
    val opponentName: String,
    val opponentRole: String,
    val caseSubject: String,
    val courtName: String,
    val caseType: String,
    val firstSessionDate: String,
    val documents: String = "", // JSON string containing document paths
    val images: String = "" // JSON string containing image paths
)