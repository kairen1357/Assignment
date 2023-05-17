package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InputAgeActivity:AppCompatActivity() {

    private lateinit var username: String
    private lateinit var gender: String
    private var age: Int = 0
    private var height: Int = 0
    private lateinit var weight: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inputage)

        // Get the progress value from inputName activity
        var progressValue = intent.getIntExtra("progressValue", 0)

        // Set the progress bar to the progress value
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = progressValue

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


        //back button
        val backButton =  findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            progressValue -= 20
            //set the progress value to the progress bar
            progressBar.progress = progressValue


            val intent = Intent(this,GenderSelectionActivity::class.java)
            intent.putExtra("progressValue", progressValue)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            intent.putExtra("email", email)
            startActivity(intent)
        }



       //next button
        val nextButton = findViewById<Button>(R.id.inputAge_nextButton)
        nextButton.setOnClickListener {
            Log.i("Clicked", "Button is Clicked")

            // Update the user sign up data in Firebase database with the new age value
            val ageEditText = findViewById<EditText>(R.id.SignUp_Age_input)
            //age = ageEditText.text.toString().toInt()
            try {
                age = ageEditText.text.toString().toInt()
            } catch (e: NumberFormatException) {
                // Display an error message to the user
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Return without executing the subsequent code
            }
            val userSignUp = UserSignUp(username, gender, age, height, weight, email)
            usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            it.ref.setValue(userSignUp)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Failed to update user sign up data", error.toException())
                    }
                })

            progressValue += 20
            //set the progress value to the progress bar
            progressBar.progress = progressValue

            // Launch the next activity
            val intent = Intent(this, HeightSelectionActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            intent.putExtra("email", email)
            intent.putExtra("progressValue", progressValue)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "InputAgeActivity"
    }
}






