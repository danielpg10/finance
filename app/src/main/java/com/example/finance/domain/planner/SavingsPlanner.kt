package com.example.finance.domain.planner

import java.time.YearMonth

object SavingsPlanner {

    fun monthlyAmount(target: Long, months: Int): Long =
        if (target <= 0 || months <= 0) 0 else (target + months - 1) / months

    fun completionMonth(months: Int, from: YearMonth = YearMonth.now()): YearMonth =
        from.plusMonths(months.toLong())
}
