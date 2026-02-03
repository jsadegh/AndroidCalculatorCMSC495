package com.example.calculator_cmsc495

import kotlin.math.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorScreen()
        }
    }
}

class CalcHistory(
    val expression: String,
    val result: String
)

// --- LG EARTHY COLOR PALETTE ---
// Centralized palette so the whole UI can be rethemed by editing only this object.
private object CalcColors {
    // App surfaces
    val Frame = Color(0xFF1A1A14)          // deep forest-ink (background)
    val Display = Color(0xFFE7DDC8)        // parchment (main display)
    val DisplayText = Color(0xFF1B1A16)    // near-black ink

    // History surfaces
    val HistoryPanel = Color(0xFF232118)   // dark moss/charred olive
    val HistoryCard = Color(0xFF2E2B1F)    // warm shadow bark
    val HistoryTitleText = Color(0xFFEFE6D6)
    val HistoryItemText = Color(0xFFE7DDC8)

    // Buttons
    val NumberBtn = Color(0xFF2B2A20)      // bark/espresso
    val OperatorBtn = Color(0xFF6B7A3A)    // moss green
    val EqualsBtn = Color(0xFF8B6A2B)      // antique gold/bronze

    val ClearBtn = Color(0xFF7A2E2E)       // dried berry / oxblood
    val BackspaceBtn = Color(0xFFB07A2A)   // amber
    val HistoryBtn = Color(0xFF4F3B5F)     // muted amethyst (witchy accent)

    // Text on buttons
    val BtnText = Color(0xFFEFE6D6)        // warm parchment-white
}

@Composable
fun CalculatorScreen() {

    var currentValue by remember { mutableStateOf(0.0) }
    var previousValue by remember { mutableStateOf(0.0) }
    var displayText by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf("") }
    var isNewNumber by remember { mutableStateOf(true) }
    var history by remember { mutableStateOf(listOf<CalcHistory>()) }
    var showHistory by remember { mutableStateOf(false) }

    fun appendNumber(num: String) {

        if (isNewNumber) {
            if (num == ".") {
                displayText = "0."
            } else
                displayText = num

            isNewNumber = false

        } else {
            if (num == "." && displayText.contains(".")) return

            if (displayText == "0" && num != ".") {
                displayText = num
            } else
                displayText + num
        }

        currentValue = displayText.toDoubleOrNull() ?: 0.0
    }

    fun backspace() {

        if (!isNewNumber && displayText.length > 1) {
            displayText = displayText.dropLast(1)
            currentValue = displayText.toDoubleOrNull() ?: 0.0
        } else {
            displayText = "0"
            currentValue = 0.0
            isNewNumber = true
        }
    }

    fun calculate() {

        val result = when (operation) {

            "+" -> previousValue + currentValue
            "-" -> previousValue - currentValue
            "×" -> previousValue * currentValue
            "÷" -> if (currentValue != 0.0) {
                previousValue / currentValue
            } else Double.NaN

            else -> currentValue
        }

        val resultText = if (result.isNaN()) {
            "Error"
        }
        else {
            //keep if the result is a whole number
            if (result % 1.0 == 0.0) {
                result.toLong().toString()
            }
            //Shows 8 decimal places if the decimal places are nonzero
            else String.format("%.8f", result).trimEnd('0').trimEnd('.')
        }

        //add to history if a calculation happens
        if (operation.isNotEmpty()) {
            val expression = "$previousValue $operation $currentValue"
            history = history + CalcHistory(expression, resultText)
        }

        displayText = resultText
        currentValue = result
        previousValue = 0.0
        operation = ""
        isNewNumber = true
    }

    fun executeOperation(oper: String) {

        if (operation.isNotEmpty() && !isNewNumber) {
            calculate()
        }

        previousValue = currentValue
        operation = oper
        isNewNumber = true
    }

    fun clear() {

        displayText = "0"
        currentValue = 0.0
        previousValue = 0.0
        operation = ""
        isNewNumber = true
    }

    //frame
    Column(
        modifier = Modifier.fillMaxSize()
            .background(CalcColors.Frame)
            .padding(16.dp)
    ) {

        // History Panel
        if (showHistory) {
            Card(
                modifier = Modifier.fillMaxWidth().weight(0.6f).padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CalcColors.HistoryPanel)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "History",
                            color = CalcColors.HistoryTitleText,
                            fontSize = 20.sp
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(history) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                colors = CardDefaults.cardColors(containerColor = CalcColors.HistoryCard)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.expression} = ${item.result}",
                                        color = CalcColors.HistoryItemText,
                                        fontSize = 16.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        //display
        Card(
            //can adjust size of the history panel by adjusting weight
            modifier = Modifier.fillMaxWidth().weight(if (showHistory) 0.3f else 1f),
            colors = CardDefaults.cardColors(containerColor = CalcColors.Display)
        ) {
            Column(
                //display text alignment
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    //display text properties
                    text = displayText,
                    color = CalcColors.DisplayText,
                    fontSize = 54.sp,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
            }
        }

        //buttons
        Column(modifier = Modifier.fillMaxWidth()) {

            //Row 1
            Row(modifier = Modifier.fillMaxWidth()) {
                CalcButton("AC", Modifier.weight(1f), CalcColors.ClearBtn) { clear() }
                CalcButton("⌫", Modifier.weight(1f), CalcColors.BackspaceBtn) { backspace() }
                CalcButton("⏱", Modifier.weight(1f), CalcColors.HistoryBtn) { showHistory = !showHistory }
                CalcButton("÷", Modifier.weight(1f), CalcColors.OperatorBtn) { executeOperation("÷") }
            }

            //Row 2
            Row(modifier = Modifier.fillMaxWidth()) {
                CalcButton("7", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("7") }
                CalcButton("8", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("8") }
                CalcButton("9", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("9") }
                CalcButton("×", Modifier.weight(1f), CalcColors.OperatorBtn) { executeOperation("×") }
            }

            //Row 3
            Row(modifier = Modifier.fillMaxWidth()) {
                CalcButton("4", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("4") }
                CalcButton("5", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("5") }
                CalcButton("6", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("6") }
                CalcButton("-", Modifier.weight(1f), CalcColors.OperatorBtn) { executeOperation("-") }
            }

            //Row 4
            Row(modifier = Modifier.fillMaxWidth()) {
                CalcButton("1", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("1") }
                CalcButton("2", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("2") }
                CalcButton("3", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber("3") }
                CalcButton("+", Modifier.weight(1f), CalcColors.OperatorBtn) { executeOperation("+") }
            }

            //Row 5
            Row(modifier = Modifier.fillMaxWidth()) {
                CalcButton("0", Modifier.weight(2f), CalcColors.NumberBtn) { appendNumber("0") }
                CalcButton(".", Modifier.weight(1f), CalcColors.NumberBtn) { appendNumber(".") }
                CalcButton("=", Modifier.weight(1f), CalcColors.EqualsBtn) { calculate() }
            }
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp).padding(5.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            color = CalcColors.BtnText
        )
    }
}
