package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.repository.CategoryRepository
import javax.inject.Inject

class InsertCategoryUseCase @Inject constructor(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category): Long {
        return repository.insertCategory(category)
    }
}
