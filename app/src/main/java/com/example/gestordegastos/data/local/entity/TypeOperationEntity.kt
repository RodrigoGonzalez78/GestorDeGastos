package com.example.gestordegastos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TypeOperation")
data class TypeOperationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String
)

