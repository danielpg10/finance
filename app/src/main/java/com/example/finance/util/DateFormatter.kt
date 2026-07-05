package com.example.finance.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    private val locale = Locale.forLanguageTag("es")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    private val dayFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", locale)

    fun formatMonth(month: YearMonth): String =
        monthFormatter.format(month).replaceFirstChar { it.uppercase(locale) }

    fun formatDay(date: LocalDate): String = dayFormatter.format(date)
}
