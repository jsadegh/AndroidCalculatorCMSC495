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
            .background(Color(0xFF424242)) //frame color
            .padding(16.dp)
    ) {

        // History Panel
        if (showHistory) {
            Card(
                modifier = Modifier.fillMaxWidth().weight(0.6f).padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
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
                            text = "History", color = Color.White, fontSize = 20.sp
                        )

                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(history) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.expression} = ${item.result}",
                                        color = Color.White,
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5DC)) //display color
        ) {
            Column(
                //display text alignment
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    //display text properties
                    text = displayText, color = Color.Black, fontSize = 54.sp,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
            }
        }

        //buttons
        Column(modifier = Modifier.fillMaxWidth()) {
            //Row 1
            Row(modifier = Modifier.fillMaxWidth()) {

                CalcButton("AC", Modifier.weight(1f), Color(0xFFFF5252)) { clear() }
                CalcButton("⌫", Modifier.weight(1f), Color(0xFFFF9800)) { backspace() }
                //using stopwatch unicode emote. Can switch out for a better history icon
                CalcButton("⏱", Modifier.weight(1f), Color(0xFF0000FF)) { showHistory = !showHistory }
                CalcButton("÷", Modifier.weight(1f), Color(0xFF0000FF)) { executeOperation("÷") }
            }

            //Row 2
            Row(modifier = Modifier.fillMaxWidth()) {

                CalcButton("7", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("7") }
                CalcButton("8", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("8") }
                CalcButton("9", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("9") }
                CalcButton("×", Modifier.weight(1f), Color(0xFF0000FF)) { executeOperation("×") }
            }

            //Row 3
            Row(modifier = Modifier.fillMaxWidth()) {

                CalcButton("4", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("4") }
                CalcButton("5", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("5") }
                CalcButton("6", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("6") }
                CalcButton("-", Modifier.weight(1f), Color(0xFF0000FF)) { executeOperation("-") }
            }

            //Row 4
            Row(modifier = Modifier.fillMaxWidth()) {

                CalcButton("1", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("1") }
                CalcButton("2", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("2") }
                CalcButton("3", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber("3") }
                CalcButton("+", Modifier.weight(1f), Color(0xFF0000FF)) { executeOperation("+") }
            }

            //Row 5
            Row(modifier = Modifier.fillMaxWidth()) {

                CalcButton("0", Modifier.weight(2f), Color(0xFF2C2C2C)) { appendNumber("0") }
                CalcButton(".", Modifier.weight(1f), Color(0xFF2C2C2C)) { appendNumber(".") }
                CalcButton("=", Modifier.weight(1f), Color(0xFF006400)) { calculate() }
            }
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    //colors are defined for each individual button. We can "group" based on button type later maybe
    backgroundColor: Color,
    onClick: () -> Unit
) {
    //button spacing, size, and shape
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp).padding(5.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
    ) {
        //button text properties
        Text(
            text = text,
            fontSize = 24.sp,
            color = Color.LightGray,
        )
    }
}