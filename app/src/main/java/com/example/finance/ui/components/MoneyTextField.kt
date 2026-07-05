package com.example.finance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

object CurrencyVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        if (digits.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }
        val grouped = digits.reversed().chunked(3).joinToString(".").reversed()
        val output = "$ $grouped"

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val separators = (1 until offset).count { (digits.length - it) % 3 == 0 }
                return 2 + offset + separators
            }

            override fun transformedToOriginal(offset: Int): Int {
                var original = digits.length
                while (original > 0 && originalToTransformed(original) > offset) {
                    original--
                }
                return original
            }
        }
        return TransformedText(AnnotatedString(output), mapping)
    }
}

@Composable
fun MoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter(Char::isDigit).take(12)) },
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = {
            Text("$ 0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = CurrencyVisualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        shape = RoundedCornerShape(16.dp)
    )
}
