package com.example.finance.domain.model

data class Fund(
    val id: Long,
    val name: String,
    val type: FundType,
    val initialBalance: Long,
    val includeInGoal: Boolean,
    val balance: Long
)
