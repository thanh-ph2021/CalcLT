package com.example.calclt.Helpers

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class NumberFormatHelper(
    var decimalSeparator: String = ".",
    var groupingSeparator: String = ",",
) {
    private val numbersRegex = "[^0-9,.]".toRegex()

    fun doubleToString(d: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        symbols.decimalSeparator = decimalSeparator.single()
        symbols.groupingSeparator = groupingSeparator.single()

        val formatter = DecimalFormat()
        formatter.maximumFractionDigits = 12
        formatter.decimalFormatSymbols = symbols
        formatter.isGroupingUsed = true
        return formatter.format(d)
    }

    fun addGroupingSeparators(str: String): String {
        return doubleToString(removeGroupingSeparator(str).toDouble())
    }

    fun removeGroupingSeparator(str: String): String {
        return str.replace(groupingSeparator, "").replace(decimalSeparator, ".")
    }

    fun formatNumber(number: Double): String {
        return if (number >= 1_000_000_000_000) {
            val formatter = DecimalFormat("0.########E0")
            formatter.format(number).replace("E", "e")
        } else {
            val formatter = DecimalFormat("#,###.########")
            formatter.format(number)
        }
    }

    fun removeThousandsDelimiter(str: String): String {
        val valuesToCheck = numbersRegex.split(str).filter { it.trim().isNotEmpty() }
        var updateText = str
        valuesToCheck.forEach {
            var newString = removeGroupingSeparator(it)

            updateText = updateText.replace(it, newString)
        }
        return updateText
    }

    fun addThousandsDelimiter(str: String): String {
        val valuesToCheck = numbersRegex.split(str).filter { it.trim().isNotEmpty() }
        var updateText = str
        valuesToCheck.forEach {
            var newString = addGroupingSeparators(it)

            // allow writing numbers like 0.003
            if (it.contains(DECIMAL_SEPARATOR)) {
                val firstPart = newString.substringBefore(DECIMAL_SEPARATOR)
                val lastPart = it.substringAfter(DECIMAL_SEPARATOR)
                newString = "$firstPart$DECIMAL_SEPARATOR$lastPart"
            }
            updateText = updateText.replace(it, newString)
        }
        return updateText
    }
}