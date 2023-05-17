package com.example.assignment_test

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ActivityChangePassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<ImageButton>(R.id.backbutton).setOnClickListener{
            onBackPressed()
        }

        val currentPasswordEditText = findViewById<TextInputEditText>(R.id.current_password_editText)
        val newPasswordEditText = findViewById<TextInputEditText>(R.id.new_password_editText)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.con_new_password_editText)

        val passwordRegex = "(?=.*[A-Z])(?=.*\\d).{8,}".toRegex()

        newPasswordEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val isValidPassword = passwordRegex.matches(s.toString())
                val newPasswordInputLayout =
                    findViewById<TextInputLayout>(R.id.new_password_layout)

                if (s.isNullOrEmpty()) {
                    newPasswordInputLayout.isErrorEnabled = true
                    newPasswordInputLayout.error = "Please enter a password."
                } else if (s.length < 8) {
                    newPasswordInputLayout.isErrorEnabled = true
                    newPasswordInputLayout.error = "Password must be at least 8 characters."
                } else if (!s.matches(passwordRegex)) {
                    newPasswordInputLayout.isErrorEnabled = true
                    newPasswordInputLayout.error =
                        "Password must contain at least 1 uppercase and 1 number."
                } else {
                    newPasswordInputLayout.isErrorEnabled = false
                    newPasswordInputLayout.error = null
                }

                val confirmTextChangedListener =
                    confirmPasswordEditText.getTag(R.id.con_new_password_editText) as? TextWatcher
                confirmTextChangedListener?.afterTextChanged(confirmPasswordEditText.text)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val confirmPasswordInputLayout =
            findViewById<TextInputLayout>(R.id.con_new_password_layout)
        val confirmPasswordTextChangedListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newPassword = newPasswordEditText.text.toString()
                if (s.toString() != newPassword) {
                    confirmPasswordInputLayout.isErrorEnabled = true
                    confirmPasswordInputLayout.error = "Confirm password does not match."
                } else {
                    confirmPasswordInputLayout.isErrorEnabled = false
                    confirmPasswordInputLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        confirmPasswordEditText.addTextChangedListener(confirmPasswordTextChangedListener)
        confirmPasswordEditText.setTag(
            R.id.con_new_password_editText,
            confirmPasswordTextChangedListener
        )

        val saveButton = findViewById<Button>(R.id.change_password_button)
        saveButton.setOnClickListener {
            val currentPasswordInput = currentPasswordEditText.text.toString()
            val newPasswordInput = newPasswordEditText.text.toString()
            val confirmPasswordInput = confirmPasswordEditText.text.toString()

            // check if any field is empty
            if (currentPasswordInput.isEmpty() || newPasswordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
                // show error message for empty fields
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()

                // set error message for empty fields
                if (currentPasswordInput.isEmpty()) {
                    val currentPasswordInputLayout =
                        findViewById<TextInputLayout>(R.id.current_password_layout)
                    currentPasswordInputLayout.isErrorEnabled = true
                    currentPasswordInputLayout.error = "Please enter your current password."
                }
                if (newPasswordInput.isEmpty()) {
                    val newPasswordInputLayout =
                        findViewById<TextInputLayout>(R.id.new_password_layout)
                    newPasswordInputLayout.isErrorEnabled = true
                    newPasswordInputLayout.error = "Please enter your new password."
                }
                if (confirmPasswordInput.isEmpty()) {
                    confirmPasswordInputLayout.isErrorEnabled = true
                    confirmPasswordInputLayout.error = "Please confirm your new password."
                }
                return@setOnClickListener
            }        // clear error messages if user enters values
            currentPasswordEditText.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        findViewById<TextInputLayout>(R.id.current_password_layout).error =
                            null
                    }
                }
            newPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    findViewById<TextInputLayout>(R.id.new_password_layout).error = null
                }
            }
            confirmPasswordEditText.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        findViewById<TextInputLayout>(R.id.con_new_password_layout).error =
                            null
                    }
                }

            // check if current password is valid (dummy condition)
            val currentPasswordIsValid = true

            // check if new password and confirm password match
            if (newPasswordInput != confirmPasswordInput) {
                // show error message for password mismatch
                Toast.makeText(
                    this,
                    "New password and confirm password do not match.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // check if new password is valid (based on TextWatcher checks)
            val isValidPassword = passwordRegex.matches(newPasswordInput)
            if (!isValidPassword) {
                // show error message for invalid password
                Toast.makeText(
                    this,
                    "New password must be at least 8 characters, with 1 uppercase and 1 number.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // check if current password is correct
            if (!currentPasswordIsValid) {
                // show dialog for incorrect current password
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("Incorrect current password input.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = dialogBuilder.create()
                dialog.show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Password change successful.", Toast.LENGTH_SHORT).show()
        }
    }



}