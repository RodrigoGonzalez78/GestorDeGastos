package com.example.gestordegastos.data.local.entity

import androidx.room.*

@Entity(tableName = "Category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val color: String?
)

