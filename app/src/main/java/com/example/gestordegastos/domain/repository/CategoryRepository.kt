package com.example.gestordegastos.domain.repository

import com.example.gestordegastos.domain.model.Category
import kotlinx.coroutines.flow.Flow


interface CategoryRepository {
    suspend fun getById(id: Int): Category?
    suspend fun insertCategory(category: Category): Long
    suspend fun getAllCategories(): Flow<List<Category>>
}