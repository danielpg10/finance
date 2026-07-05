package com.example.finance.domain.planner

import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class SavingsPlannerTest {

    @Test
    fun dividesEvenly() {
        assertEquals(1_000_000L, SavingsPlanner.monthlyAmount(12_000_000L, 12))
    }

    @Test
    fun roundsUpSoTheGoalIsAlwaysReached() {
        assertEquals(3_333_334L, SavingsPlanner.monthlyAmount(10_000_000L, 3))
    }

    @Test
    fun returnsZeroForInvalidInput() {
        assertEquals(0L, SavingsPlanner.monthlyAmount(0L, 12))
        assertEquals(0L, SavingsPlanner.monthlyAmount(1_000_000L, 0))
    }

    @Test
    fun computesCompletionMonth() {
        val from = YearMonth.of(2026, 7)
        assertEquals(YearMonth.of(2027, 1), SavingsPlanner.completionMonth(6, from))
    }
}
