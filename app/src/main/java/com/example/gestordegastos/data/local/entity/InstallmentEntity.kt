package com.example.gestordegastos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Installment",
    foreignKeys = [
        ForeignKey(
            entity = OperationEntity::class,
            parentColumns = ["id"],
            childColumns = ["operation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InstallmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "operation_id") val operationId: Int,
    val amount: Double,
    @ColumnInfo(name = "due_date") val dueDate: String,
    @ColumnInfo(name = "is_paid", defaultValue = "0") val isPaid: Boolean = false,
    @ColumnInfo(name = "installment_number") val installmentNumber: Int
)
