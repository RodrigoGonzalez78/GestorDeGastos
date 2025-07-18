package com.example.gestordegastos.data.mappers

import com.example.gestordegastos.data.local.entity.CategoryEntity
import com.example.gestordegastos.data.local.entity.OperationEntity
import com.example.gestordegastos.data.local.entity.TypeOperationEntity
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.model.TypeOperation

fun OperationEntity.toOperation(): Operation {
    return Operation(
        id = this.id,
        amount = this.amount,
        date = this.date,
        categoryId = this.category,
        typeOperationId = this.typeOperation,
    )
}

fun Operation.toOperationEntity(): OperationEntity {
    return OperationEntity(
        id = this.id,
        amount = this.amount,
        date = this.date,
        category = this.categoryId,
        typeOperation = this.typeOperationId,
    )
}

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = this.id,
        description = this.description,
        color = this.color,
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        description = this.description,
        color = this.color,
    )
}

fun TypeOperationEntity.toTypeOperation(): TypeOperation {
    return TypeOperation(
        id = this.id,
        description = this.description,
    )
}

fun TypeOperation.toTypeOperationEntity(): TypeOperationEntity {
    return TypeOperationEntity(
        id = this.id,
        description = this.description,

    )
}