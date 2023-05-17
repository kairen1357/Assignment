package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        database = FirebaseDatabase.getInstance().getReference("SignupUsers")

        val loginButton: Button =  findViewById(R.id.login_button)
        val emailInputLayout = findViewById<TextInputLayout>(R.id.Login_email_input_layout)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.Login_password_input_layout)
        val emailEditText = findViewById<TextInputEditText>(R.id.Login_email_input)
        val passwordEditText = findViewById<TextInputEditText>(R.id.Login_password_input)

        emailEditText.addTextChangedListener {
            emailInputLayout.error = null
        }

        passwordEditText.addTextChangedListener {
            passwordInputLayout.error = null
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener{
            onBackPressed()
        }



        loginButton.setOnClickListener {
            signIn()
        }
    }


    private fun signIn() {
        val email = findViewById<TextInputEditText>(R.id.Login_email_input).text.toString()
        val password = findViewById<TextInputEditText>(R.id.Login_password_input).text.toString()

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            if (email.isEmpty()) {
                val emailInputLayout = findViewById<TextInputLayout>(R.id.Login_email_input_layout)
                emailInputLayout.error = "Please enter email address."
                emailInputLayout.isErrorEnabled = true

            }
            if (password.isEmpty()) {
                val PasswordInputLayout = findViewById<TextInputLayout>(R.id.Login_password_input_layout)
                PasswordInputLayout.error = "Please enter password."
                PasswordInputLayout.isErrorEnabled = true

            }
            return

        }


        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userID = user?.uid
                    Log.d("AUTH", "signInWithEmail:success. userID: $userID")
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra("userID", userID) // pass the userID to MainActivity
                    startActivity(intent)

                    finish()
                } else {
                    Log.w("AUTH", "signInWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    findViewById<TextInputEditText>(R.id.Login_password_input).text=null
                }
            }
    }



}