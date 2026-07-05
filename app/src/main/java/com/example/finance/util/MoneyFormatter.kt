package com.example.finance.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyFormatter {

    private val symbols = DecimalFormatSymbols(Locale.forLanguageTag("es-CO"))
    private val format = DecimalFormat("#,###", symbols)

    fun format(amount: Long): String {
        val formatted = format.format(kotlin.math.abs(amount))
        val sign = if (amount < 0) "-" else ""
        return "$sign$$formatted"
    }
}

fun Long.toMoney(): String = MoneyFormatter.format(this)
