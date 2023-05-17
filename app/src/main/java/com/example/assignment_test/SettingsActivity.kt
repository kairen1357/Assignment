package com.example.assignment_test

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener{
            onBackPressed()
        }

        findViewById<Button>(R.id.delete_acc_button).setOnClickListener{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to delete this account permanently?")
                builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    showPasswordDialog()

                }
                builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()

        }


    }

    private fun showPasswordDialog(){
        val dialogView = layoutInflater.inflate(R.layout.fragment_bottom_sheet_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.password_input)
        val passwordInputLayout = dialogView.findViewById<TextInputLayout>(R.id.password_layout)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Password")
            .setPositiveButton("Submit", null) // Set positive button without click listener
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        builder.show()

        // Get the positive button from the dialog after it is shown
        val positiveButton = builder.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val password = passwordEditText.text.toString()

            if (password.isNotEmpty()) {
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                val credential =
                    EmailAuthProvider.getCredential(user?.email.toString(), password)
                user?.reauthenticate(credential)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = Firebase.auth.currentUser!!
                        user.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Delete Account Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, SignUpActivity::class.java))
                            }
                        }
                    } else {
                        passwordInputLayout.error = "Password Wrong"

                    }
                }
            } else {

                passwordInputLayout.error = "Password cannot be empty"
            }
        }

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear the error when the text changes
                passwordInputLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })

    }

        class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val fragmentBottomsheetcontact = FragmentBottomSheetContact()
            val contactUs = findPreference<Preference>("contactUs")
            contactUs?.setOnPreferenceClickListener {
                fragmentBottomsheetcontact.show(requireFragmentManager(), "BottomSheetDialog")
                true // return true to indicate that the click event was consumed
            }

            val darktheme = findPreference<SwitchPreferenceCompat>("theme")
            darktheme?.setOnPreferenceClickListener {
                val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val newTheme = if (currentTheme == Configuration.UI_MODE_NIGHT_NO) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(newTheme)


                true
            }

        }
    }
}