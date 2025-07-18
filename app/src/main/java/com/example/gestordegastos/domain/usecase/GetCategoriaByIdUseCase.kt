package com.example.gestordegastos.domain.usecase

import com.example.gestordegastos.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriaByIdUseCase @Inject constructor(private val repo: CategoryRepository) {
    suspend operator fun invoke(id: Int) = repo.getById(id)
}