package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(private val repository: CategoryRepository) {
    suspend operator fun invoke(): Flow<List<Category>> {
        return repository.getAllCategories()
    }
}