package com.example.finance.data.repository

import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.entity.CategoryEntity
import com.example.finance.domain.model.Category
import com.example.finance.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomCategoryRepository(private val categoryDao: CategoryDao) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addCategory(name: String, icon: String, color: Long, monthlyBudget: Long?) {
        categoryDao.insert(
            CategoryEntity(name = name, icon = icon, color = color, monthlyBudget = monthlyBudget)
        )
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.update(
            CategoryEntity(
                id = category.id,
                name = category.name,
                icon = category.icon,
                color = category.color,
                monthlyBudget = category.monthlyBudget
            )
        )
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.delete(categoryId)
    }
}

internal fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    icon = icon,
    color = color,
    monthlyBudget = monthlyBudget
)
