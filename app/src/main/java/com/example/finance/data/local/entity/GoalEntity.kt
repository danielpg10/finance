package com.example.finance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal")
data class GoalEntity(
    @PrimaryKey val id: Long = 1,
    val name: String,
    val targetAmount: Long
)
