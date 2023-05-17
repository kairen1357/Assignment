package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HeightSelectionActivity:AppCompatActivity() {

    private lateinit var username: String
    private lateinit var gender: String
    private var age: Int = 0
    private var height: Int = 0
    private lateinit var weight: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.heightselection)

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

        val heightDisplay = findViewById<TextView>(R.id.weightDisplay)

        // Get the progress value from gender inputName activity
        var progressValue = intent.getIntExtra("progressValue", 0)

        // Set the progress bar to the progress value
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = progressValue

        val heightValue = findViewById<NumberPicker>(R.id.weight_number_picker)
        heightValue.wrapSelectorWheel = false
        heightValue.minValue = 140
        heightValue.maxValue = 220
        heightValue.value = 160
        //display value
        heightDisplay.text = "${heightValue.value}"
        //display changed value
        heightValue.setOnValueChangedListener { picker, oldVal, newVal ->
            heightDisplay.text = "$newVal"
        }

        // next button
        val nextButton = findViewById<Button>(R.id.weight_nextButton)
        nextButton.setOnClickListener {
            Log.i("button Clicked", "is Clicked")
            progressValue += 10
            //set the progress value to the progress bar
            progressBar.progress = progressValue

            height = heightDisplay.text.toString().toInt()



            val intent = Intent(this, WeightSelectionActivity::class.java)
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
            progressValue -= 20
            //set the progress value to the progress bar
            progressBar.progress = progressValue

            val userRef = usersRef.child(username)
            userRef.child("age").setValue(0)

            val intent = Intent(this,InputAgeActivity::class.java)
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
        private const val TAG = "HeightSelectionActivity"
    }
}