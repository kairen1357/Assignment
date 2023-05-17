package com.example.assignment_test

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.assignment_test.R.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class FragmentBottomSheetEmail : BottomSheetDialogFragment() {

    // Declare the listener variable
    private var listener: OnEmailEnteredListener? = null
    private lateinit var databaseRef: DatabaseReference

    // Declare the view variables
    private lateinit var editTextEmail: EditText
    private lateinit var buttonSave: Button
    private lateinit var passwordInput: TextInputEditText

    // Define the listener interface
    interface OnEmailEnteredListener {
        fun onEmailEntered(email: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.fragment_bottom_sheet_email, container, false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)

        // Find the view references
        editTextEmail = view.findViewById(R.id.email_changed)
        buttonSave = view.findViewById(R.id.save_button)
        passwordInput = view.findViewById(R.id.password_input)


        // Set the click listener for the save button
        buttonSave.setOnClickListener {
            // Get the user input
            val email = editTextEmail.text.toString().trim()
            val password = passwordInput.text.toString()
            if (password.isNotEmpty())
            {
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                val credential = EmailAuthProvider.getCredential(user?.email.toString(), password)
                user?.reauthenticate(credential)?.addOnCompleteListener{
                    if (it.isSuccessful) {
                        user?.updateEmail(email)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                listener?.onEmailEntered(email)
                                dismiss()
                            }
                        }
                    }
                }
            }
            else {
                view.findViewById<TextInputLayout>(R.id.password_layout).error = "Empty Password"
            }
            // Call the listener method to pass the user input to the parent fragment


        }

        return view
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the parent fragment implements the listener interface
        if (parentFragment is OnEmailEnteredListener) {
            listener = parentFragment as OnEmailEnteredListener
        } else {
            throw RuntimeException("$context must implement OnEmailEnteredListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        // Reset the listener variable
        listener = null
    }
}
