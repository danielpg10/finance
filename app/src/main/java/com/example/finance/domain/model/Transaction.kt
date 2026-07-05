package com.example.finance.domain.model

import java.time.LocalDate

data class Transaction(
    val id: Long,
    val amount: Long,
    val type: TransactionType,
    val categoryId: Long?,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryColor: Long?,
    val fundId: Long?,
    val fundName: String?,
    val note: String?,
    val date: LocalDate
)
