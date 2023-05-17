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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class UserSignUp(
    val username: String,
    val gender: String,
    var age: Int,
    var height: Int,
    val weight: String,
    val email: String
)

class InputNameActivity:AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inputname)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        //set initial progress value
        var currentProgress = 0
        progressBar.progress = currentProgress

//        // Get the progress value from gender selection activity
//        var progressValue = intent.getIntExtra("progressValue", 0)
//
//        // Set the progress bar to the progress value
//        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
//        progressBar.progress = progressValue

        //back button
        val backButton =  findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().reference

        // Get reference to Firebase database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("SignupUsers")
        //val usersCountRef = database.getReference("UsersCount")

        //next button
        val nextButton = findViewById<Button>(R.id.email_pwd_nextButton)
        nextButton.setOnClickListener {
            Log.i("Clicked", "Button is Clicked")

            // Get input text
            val usernameEditText = findViewById<EditText>(R.id.SignUp_Username_input)
            val username = usernameEditText.text.toString()

            // Check if username is empty
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //set the progress value to the progress bar
            progressBar.progress = currentProgress





            currentProgress += 20
            val intent = Intent(this, GenderSelectionActivity::class.java)
            intent.putExtra("progressValue", currentProgress)
            intent.putExtra("username", username)
            startActivity(intent)

        }
    }
}