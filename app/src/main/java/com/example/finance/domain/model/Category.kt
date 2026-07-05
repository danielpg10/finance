package com.example.finance.domain.model

data class Category(
    val id: Long,
    val name: String,
    val icon: String,
    val color: Long,
    val monthlyBudget: Long?
)
