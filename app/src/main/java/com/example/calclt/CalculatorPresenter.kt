package com.example.calclt

import android.util.Log
import com.example.calclt.Helpers.*
import com.example.calclt.Models.CalculatorModel
import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorPresenter (private val view: ICalculator, private val model: CalculatorModel) {

    private val formatter = NumberFormatHelper( DECIMAL_SEPARATOR, GROUPING_SEPARATOR)

    fun onCalculateButtonClicked(str: String) {
        var expressionText = str
            .replace("×", "*")
            .replace("÷", "/")
            .replace("%", "/100")
            .replace("mod", "%")
            .trim()

        if (expressionText.endsWith('+') ||
            expressionText.endsWith('-') ||
            expressionText.endsWith('*') ||
            expressionText.endsWith('/') ||
            // mod
            expressionText.endsWith('%')) {

            expressionText = expressionText.dropLast(1)
        }

        try {
            expressionText = formatter.removeThousandsDelimiter(expressionText)
            val result = model.calculate(expressionText)

            view.showResult("=${formatter.formatNumber(result!!)}", str)
        } catch (e: Exception) {
            Log.d("ERROR-RESULT", e.toString())
            e.message?.let { view.showResult(it, str) }
        }
    }
}