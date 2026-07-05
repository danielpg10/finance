package com.example.finance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.dao.FundDao
import com.example.finance.data.local.dao.GoalDao
import com.example.finance.data.local.dao.TransactionDao
import com.example.finance.data.local.entity.CategoryEntity
import com.example.finance.data.local.entity.FundEntity
import com.example.finance.data.local.entity.GoalEntity
import com.example.finance.data.local.entity.TransactionEntity

@Database(
    entities = [
        FundEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun fundDao(): FundDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao

    companion object {

        private val defaultCategories = listOf(
            Triple("Comida", "restaurant", 0xFFF97316),
            Triple("Transporte", "car", 0xFF3B82F6),
            Triple("Salidas", "party", 0xFFEC4899),
            Triple("Salud", "health", 0xFF14B8A6),
            Triple("Hogar", "home", 0xFF8B5CF6),
            Triple("Compras", "shopping", 0xFFF59E0B),
            Triple("Suscripciones", "subscriptions", 0xFF6366F1),
            Triple("Otros", "other", 0xFF64748B)
        )

        fun build(context: Context): FinanceDatabase =
            Room.databaseBuilder(context, FinanceDatabase::class.java, "finance.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        defaultCategories.forEach { (name, icon, color) ->
                            db.execSQL(
                                "INSERT INTO categories (name, icon, color, monthlyBudget) VALUES (?, ?, ?, NULL)",
                                arrayOf<Any>(name, icon, color)
                            )
                        }
                    }
                })
                .build()
    }
}
