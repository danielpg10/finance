package com.example.finance.ui.components

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyVisualTransformationTest {

    private fun transform(input: String): String =
        CurrencyVisualTransformation.filter(AnnotatedString(input)).text.text

    @Test
    fun formatsMillionsWithSeparators() {
        assertEquals("$ 4.000.000", transform("4000000"))
    }

    @Test
    fun formatsSmallAmounts() {
        assertEquals("$ 900", transform("900"))
    }

    @Test
    fun keepsEmptyInputEmpty() {
        assertEquals("", transform(""))
    }

    @Test
    fun offsetMappingIsConsistent() {
        val digits = "1234567"
        val result = CurrencyVisualTransformation.filter(AnnotatedString(digits))
        for (offset in 0..digits.length) {
            val transformed = result.offsetMapping.originalToTransformed(offset)
            assertEquals(offset, result.offsetMapping.transformedToOriginal(transformed))
        }
    }
}
