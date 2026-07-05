package com.example.finance.domain.model

data class MonthSummary(
    val income: Long,
    val expense: Long,
    val saving: Long
) {
    val balance: Long
        get() = income - expense - saving
}
