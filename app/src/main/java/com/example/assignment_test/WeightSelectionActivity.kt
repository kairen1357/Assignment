package com.example.assignment_test

import android.content.Intent
import android.icu.math.BigDecimal
import android.icu.math.MathContext
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.math.RoundingMode


class WeightSelectionActivity:AppCompatActivity() {

    private lateinit var username: String
    private lateinit var gender: String
    private var age: Int = 0
    private var height: Int = 0
    private lateinit var weight: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weightselection)

        // Get reference to Firebase database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("SignupUsers")

        // Get user sign up data from intent
        username = intent.getStringExtra("username") ?: ""
        gender = intent.getStringExtra("gender") ?: ""
        age = intent.getIntExtra("age", 0)
        height = intent.getIntExtra("height", 0)
        weight = intent.getStringExtra("weight") ?: ""
        email = intent.getStringExtra("email") ?: ""

        val weightDisplay = findViewById<TextView>(R.id.weightDisplay)

        // Get the progress value from gender inputName activity
        var progressValue = intent.getIntExtra("progressValue", 0)

        // Set the progress bar to the progress value
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = progressValue

        val weightValue = findViewById<NumberPicker>(R.id.weight_number_picker)

        weightValue.wrapSelectorWheel = false
        weightValue.minValue = 20
        weightValue.maxValue = 250
        weightValue.value = 50
        //weightValue.displayedValues = (20..250).map { "%.1f".format(it / 1f, it / 1f + 0.1 , it / 1f + 0.2).toString() }.toTypedArray()

        //significant number
        val sigNum = findViewById<NumberPicker>(R.id.weight_significantNumber)

        sigNum.wrapSelectorWheel = false
        sigNum.minValue = 0
        sigNum.maxValue = 9
        sigNum.value = 5
        //sigNum.displayedValues = arrayOf("0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9")
       // sigNum.displayedValues = (0..9).map { "%.1f".format(it * 0.1f).toString() }.toTypedArray()



        //display value
        weightDisplay.text = "${weightValue.value}.${sigNum.value}"
        //display changed value
        // set listeners to update display value
        weightValue.setOnValueChangedListener { picker, oldVal, newVal ->
            weightDisplay.text = "${newVal}.${sigNum.value}"
        }
        sigNum.setOnValueChangedListener { picker, oldVal, newVal ->
            weightDisplay.text = "${weightValue.value}.${sigNum.value}"
        }
//        weightValue.setOnValueChangedListener { picker, oldVal, newVal ->
//            weightDisplay.text = "$newVal"
//        }

        // next button
        val nextButton = findViewById<Button>(R.id.weight_nextButton)
        nextButton.setOnClickListener {
            Log.i("button Clicked", "is Clicked")
            progressValue += 10

            //set the progress value to the progress bar
            progressBar.progress = progressValue

            weight = weightDisplay.text.toString()



            val intent = Intent(this,InputEmail_PwdActivity::class.java)
            intent.putExtra("progressValue", progressValue)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        //back button
        val backButton =  findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            progressValue -= 10
            //set the progress value to the progress bar
            progressBar.progress = progressValue

            val userRef = usersRef.child(username)
            userRef.child("height").setValue(0)

            val intent = Intent(this,HeightSelectionActivity::class.java)
            intent.putExtra("progressValue", progressValue)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }
    companion object {
        private const val TAG = "WeightSelectionActivity"
    }
}