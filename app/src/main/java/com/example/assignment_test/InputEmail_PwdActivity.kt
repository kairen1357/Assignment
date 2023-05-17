package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class InputEmail_PwdActivity: AppCompatActivity() {

    private lateinit var username: String
    private lateinit var gender: String
    private var age: Int = 0
    private var height: Int = 0
    private lateinit var weight: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_password)

        // Get references to the email and password EditText views
        val emailEditText = findViewById<EditText>(R.id.SignUp_Email_input)
        val passwordEditText = findViewById<EditText>(R.id.SignUp_pwd_input)
        val retypePasswordEditText = findViewById<TextInputEditText>(R.id.SignUp_rePwd_input)

        // Get reference to Firebase Authentication
        val auth = FirebaseAuth.getInstance()

        // Get the progress value from gender selection activity
        var progressValue = intent.getIntExtra("progressValue", 0)

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

        // Set the progress bar to the progress value
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = progressValue

        //back button
        val backButton =  findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            progressValue -= 10
            //set the progress value to the progress bar
            progressBar.progress = progressValue

            val intent = Intent(this,WeightSelectionActivity::class.java)
            intent.putExtra("progressValue", progressValue)

            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            startActivity(intent)
        }

        //next button
        val nextButton = findViewById<Button>(R.id.email_pwd_nextButton)
        nextButton.setOnClickListener {
            // Get the email and password values from the EditText views
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val retypePassword = retypePasswordEditText.text.toString().trim()

            // Validate that all fields are filled
            if (email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate that the email is in the correct format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate that the email does not start with a number
            if (email[0].isDigit()) {
                Toast.makeText(this, "Email address cannot start with a number.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate that the password is at least 6 characters long
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@_])(?=\\S+\$).{6,}\$".toRegex()

            if (!passwordRegex.matches(password)) {
                Toast.makeText(this, "Password must contain at least 1 uppercase letter, 1 number, 1 '@' or '_', and must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate that the password and retype password fields match
            if (password != retypePassword) {
                Toast.makeText(this, "Password and Retype Password do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a user with the given email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val userUid = task.result?.user?.uid.toString()

                        val userSignUp = UserSignUp(username = username, gender = gender, age = age, height = height, weight = weight, email = email)
                        // Save user sign up data to Firebase database
                        usersRef.child(userUid).setValue(userSignUp)

                        // Update the user's email
                        progressValue += 20
                        //set the progress value to the progress bar
                        progressBar.progress = progressValue
                        val intent = Intent(this, Done_signUp_Activity::class.java)
                        intent.putExtra("progressValue", progressValue)
                        startActivity(intent)
                        Toast.makeText(this, "Register Successfully.", Toast.LENGTH_SHORT).show()

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed." +
                                " Email already exist",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    companion object {
        private const val TAG = "InputEmail_PwdActivity"
    }
}