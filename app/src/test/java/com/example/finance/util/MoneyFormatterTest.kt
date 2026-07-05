package com.example.finance.util

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyFormatterTest {

    @Test
    fun formatsZero() {
        assertEquals("$0", MoneyFormatter.format(0))
    }

    @Test
    fun formatsThousandsWithSeparator() {
        assertEquals("$1.500.000", MoneyFormatter.format(1_500_000))
    }

    @Test
    fun formatsNegativeAmounts() {
        assertEquals("-$25.000", MoneyFormatter.format(-25_000))
    }
}
