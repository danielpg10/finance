package com.example.finance.domain.repository

import com.example.finance.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun addCategory(name: String, icon: String, color: Long, monthlyBudget: Long?)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: Long)
}
