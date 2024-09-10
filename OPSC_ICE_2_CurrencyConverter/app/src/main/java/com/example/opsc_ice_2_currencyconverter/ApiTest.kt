package com.example.opsc_ice_2_currencyconverter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.widget.doOnTextChanged

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URL
class ApiTest : AppCompatActivity() {

    private lateinit var et_firstConversion: EditText
    private lateinit var et_secondConversion: EditText
    private lateinit var spinner_firstConversion: Spinner
    private lateinit var spinner_secondConversion: Spinner
    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.api_main)

        et_firstConversion = findViewById(R.id.et_firstConversion)
        et_secondConversion = findViewById(R.id.et_secondConversion)
        spinner_firstConversion = findViewById(R.id.spinner_firstConversion)
        spinner_secondConversion = findViewById(R.id.spinner_secondConversion)

        spinnerSetup()
        textChangedStuff()

    }
    private fun textChangedStuff() {
        et_firstConversion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Type a value", Toast.LENGTH_SHORT).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "OnTextChanged")
            }

        })

    }
    private fun getApiResult() {
        if (et_firstConversion != null && et_firstConversion.text.isNotEmpty() && et_firstConversion.text.isNotBlank()) {

            val API_KEY = "I cannot upload api key to github"
            val amount = et_firstConversion.text.toString()
            //test thingy
            val url = "https://currency.getgeoapi.com/api/v2/currency/convert?api_key=$API_KEY&from=$baseCurrency&to=$convertedToCurrency&amount=$amount&format=json"

            if (baseCurrency == convertedToCurrency) {
                Toast.makeText(
                    applicationContext,
                    "Please pick different currencies to convert",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                GlobalScope.launch(Dispatchers.IO) {

                    try {
                        val apiResult = URL(url).readText()
                        val jsonObject = JSONObject(apiResult)


                        conversionRate = jsonObject.getJSONObject("rates").getJSONObject(convertedToCurrency).getString("rate").toFloat()

                        Log.d("Main", "Conversion Rate: $conversionRate")
                        Log.d("Main", apiResult)

                        withContext(Dispatchers.Main) {
                            val convertedAmount =
                                ((et_firstConversion.text.toString().toFloat()) * conversionRate).toString()
                            et_secondConversion?.setText(convertedAmount)
                        }

                    } catch (e: Exception) {
                        Log.e("Main", "Error: $e")
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun spinnerSetup() {
        val spinner: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter

        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner2.adapter = adapter

        }

        spinner.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

        })

        spinner2.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

        })
    }
}