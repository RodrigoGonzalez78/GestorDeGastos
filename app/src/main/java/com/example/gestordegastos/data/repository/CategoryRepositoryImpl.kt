package com.example.gestordegastos.data.repository

import com.example.gestordegastos.data.local.dao.CategoryDao
import com.example.gestordegastos.data.mappers.toCategory
import com.example.gestordegastos.data.mappers.toCategoryEntity
import com.example.gestordegastos.domain.model.Category
import com.example.gestordegastos.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun getById(id: Int): Category? {
        return categoryDao.getById(id)?.toCategory()
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insert(category.toCategoryEntity())
    }

    override suspend fun getAllCategories():  Flow<List<Category>> {
        return categoryDao.getAll().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    suspend fun delete(category: Category): Int {
        return categoryDao.delete(category.toCategoryEntity())
    }
}