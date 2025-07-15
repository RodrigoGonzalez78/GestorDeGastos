package com.example.gestordegastos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Operation",
    foreignKeys = [
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["category"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TypeOperationEntity::class, parentColumns = ["id"], childColumns = ["type_operation"], onDelete = ForeignKey.CASCADE)
    ]
)
data class OperationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val date: String,
    val category: Int,
    @ColumnInfo(name = "type_operation") val typeOperation: Int
)