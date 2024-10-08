package com.example.calclt


import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.calclt.Helpers.*
import com.example.calclt.Models.CalculatorModel

class MainActivity : AppCompatActivity(), ICalculator {

    private lateinit var formulaTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var btnTheme: ImageView
    private lateinit var presenter: CalculatorPresenter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    private val formatter = NumberFormatHelper(DECIMAL_SEPARATOR, GROUPING_SEPARATOR)
    private var isNightMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeButtons()

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        editor = sharedPreferences.edit()
        isNightMode = sharedPreferences.getBoolean("nightMode", false)

        if(isNightMode){
            btnTheme.setImageResource(R.drawable.ic_dark)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        presenter = CalculatorPresenter(this, CalculatorModel())
        // Initialize UI elements
        formulaTextView = findViewById(R.id.formula)
        resultTextView = findViewById(R.id.result)

        // change text color status bar
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.setSystemBarsAppearance(
//                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
//                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
//            )
//        } else {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }
    }

    private fun initializeButtons() {
        // Number buttons
        val numberButtonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9
        )

        for (id in numberButtonIds) {
            findViewById<TextView>(id).setOnClickListener { numberAction(it) }
        }

        // Operator buttons
        val operatorButtonIds = listOf(
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply,
            R.id.btnDivide, R.id.btnModulo, R.id.btnPercent
        )

        for (id in operatorButtonIds) {
            findViewById<TextView>(id).setOnClickListener { operatorAction(it) }
        }

        // Dot button
        val btnDot = findViewById<TextView>(R.id.btnDot)
        btnDot.setOnClickListener { dotAction() }

        // Result button
        val btnResult = findViewById<TextView>(R.id.btnResult)
        btnResult.setOnClickListener { calculateResult() }

        // Clear button
        val btnAC = findViewById<TextView>(R.id.btnC)
        btnAC.setOnClickListener { clearAction() }

        // Clear theme
        btnTheme = findViewById<ImageView>(R.id.btnTheme)
        btnTheme.setOnClickListener { myThemes() }

        // Clear history
        val btnHistory = findViewById<ImageView>(R.id.btnHistory)
        btnHistory.setOnClickListener { }

        // Clear menu
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        btnMenu.setOnClickListener { openFuntionActivity() }

        val btnDelete = findViewById<ImageView>(R.id.btnDelete)
        btnDelete.setOnClickListener { dropLastAction() }
    }

    private fun numberAction(view: View) {
        if (view is TextView) {
            val text = view.text.toString()
            var resultText = resultTextView.text.toString()
            var formulaText = ""

            resultText = if (resultText == "0" || resultText.contains('=')) {
                text
            } else if (resultText.trim().endsWith('%')) {
                "$resultText × $text"
            } else {
                "$resultText$text"
            }

            resultTextView.text = formatter.addThousandsDelimiter(resultText)
            formulaTextView.text = formulaText
        }
    }

    private fun operatorAction(view: View) {
        var text = resultTextView.text.toString()
        if (text.isNotEmpty() && !text.last().isWhitespace()) {
            val operator = when (view.id) {
                R.id.btnAdd -> "+"
                R.id.btnSubtract -> "-"
                R.id.btnMultiply -> "×"
                R.id.btnDivide -> "÷"
                R.id.btnPercent -> "%"
                R.id.btnModulo -> "mod"
                else -> ""
            }

            text = resetFormula(text)

            text = if (operator != "%") "$text $operator " else "$text$operator "
            resultTextView.text = text
        }
    }

    private fun dotAction() {
        var text = resultTextView.text.toString()

        text = resetFormula(text)
        if (!text.takeLastWhile { it != ' ' }.contains(".")) {
            text = "$text."
        }
        resultTextView.text = text
    }

    private fun clearAction() {
        formulaTextView.text = ""
        resultTextView.text = "0"
    }

    private fun dropLastAction() {
        var text = resultTextView.text.toString()

        val newText = resetFormula(text, true)
        resultTextView.text = if (text != newText) newText else text.dropLast(1)
    }

    private fun resetFormula(str: String, isDropLastAction: Boolean = false): String {
        val formulaText = formulaTextView.text.toString()
        var newStr = str
        // resultText = "=123" -> remove "=" and set formulaText = ""
        if (formulaText != "") {
            newStr = if (isDropLastAction) "0" else newStr.substring(1)
            formulaTextView.text = ""
        }
        return newStr
    }

    private fun calculateResult() {
        val resultText = resultTextView.text.toString()
        if (resultText == "0" || resultText.contains('=')) {
            return
        }
        presenter.onCalculateButtonClicked(resultText)
    }

    private fun myThemes() {
        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putBoolean("nightMode", false)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putBoolean("nightMode", true)
        }
        editor.apply()
    }

    private fun openFuntionActivity(){
        val intent: Intent = Intent(this, FunctionActivity::class.java)
        startActivity(intent)
    }

    override fun showResult(result: String, formula: String) {
        formulaTextView.text = formula
        resultTextView.text = result
    }

    override fun showError(message: String, formula: String) {
        formulaTextView.text = formula
        resultTextView.text = message
    }
}