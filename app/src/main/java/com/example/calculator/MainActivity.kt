package com.example.calculator

import  android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false

    private lateinit var expression: Expression

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        binding.percent.setOnClickListener{
//            onPercentClick(it)
//        }
    }

    fun onACClick(view: View){
        binding.dataEntered.text = ""
        binding.result.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        binding.result.visibility = View.GONE
    }

    fun onDelClick(view: View){
        binding.dataEntered.text = binding.dataEntered.text.toString().dropLast(1)
        try{
            val lastChar = binding.dataEntered.text.toString().last()
            if(lastChar.isDigit()){
                checkForEqual()
            }
        }catch(e: Exception){
            binding.result.text = ""
            binding.result.visibility = View.GONE
            Log.e("Last Char Error",e.toString())
        }
    }

    fun onOperatorClick(view: View){
        if(!stateError && lastNumeric){
//            if(binding.dataEntered.text.toString().contains("X")){
//                binding.dataEntered.text.toString().replace("X","*")
//            }
            binding.dataEntered.append((view as Button).text)
            lastDot = false
            lastNumeric = false
            checkForEqual()
        }
    }

    fun onDigitClick(view: View){
        if(stateError){
            binding.dataEntered.text = (view as Button).text
            stateError = false
        }else{
            binding.dataEntered.append((view as Button).text)
        }
        lastNumeric = true
        checkForEqual()
    }

    fun onEqualsClick(view: View){
        checkForEqual()
        binding.dataEntered.text = binding.result.text.toString()
    }

    @SuppressLint("SetTextI18n")
    fun onPlusMinusClick(view: View){
        if(!stateError && lastNumeric){
            val first = binding.dataEntered.text.toString().first()
            if(first.isDigit()){
                binding.dataEntered.text = "(-" + binding.dataEntered.text.toString() + ")"
            }else{
                binding.dataEntered.text = first + "(-" + binding.dataEntered.text.toString() + ")"
            }
        }
        checkForEqual()
    }

    fun onPercentClick(view: View) {
        val dataEnteredText = binding.dataEntered.text.toString()
        if (dataEnteredText.isNotEmpty()) {
            val dataEnteredValue = BigDecimal(dataEnteredText).setScale(3,
                RoundingMode.HALF_UP).toDouble()
            val percentageValue = dataEnteredValue / 100
            binding.result.text = percentageValue.toString()
            checkForEqual()
        } else {
            binding.dataEntered.text = ""
            binding.result.text = ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkForEqual(){
        if(lastNumeric && !stateError){
            val txt = binding.dataEntered.text.toString()
            expression = ExpressionBuilder(txt).build()

            try{
                val result = expression.evaluate()
                binding.result.visibility = View.VISIBLE
                binding.result.text = result.toString()
            }catch(e: ArithmeticException){
                Log.e("Evaluation Error", e.toString())
                binding.result.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

}
