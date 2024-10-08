package com.example.calclt

interface ICalculator {
    fun showResult(result: String, formula: String)
    fun showError(message: String, formula: String)
}