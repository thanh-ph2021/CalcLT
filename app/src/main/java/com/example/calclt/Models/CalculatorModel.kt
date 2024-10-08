package com.example.calclt.Models

import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorModel {

    fun calculate(expression: String): Double? {
        val exp = ExpressionBuilder(expression).build()
        return exp.evaluate()
    }
}