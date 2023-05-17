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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Fragment_bottom_sheet_email : BottomSheetDialogFragment() {

    // Declare the listener variable
    private var listener: OnNameEnteredListener? = null
    private lateinit var databaseRef: DatabaseReference

    // Declare the view variables
    private lateinit var editTextEmail: EditText
    private lateinit var buttonSave: Button

    // Define the listener interface
    interface OnNameEnteredListener {
        fun onEmailEntered(input: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.fragment_bottom_sheet_email, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)
        // Find the view references
        editTextEmail = view.findViewById(R.id.email_changed)
        buttonSave = view.findViewById(R.id.save_button)



        // Set the click listener for the save button
        buttonSave.setOnClickListener {
            // Get the user input
            val email = editTextEmail.text.toString().trim()

            // Call the listener method to pass the user input to the parent fragment
            listener?.onEmailEntered(email)
            user?.updateEmail(email)

            databaseRef.child("email").setValue(email)
                .addOnSuccessListener {
                    user?.updateEmail(email)
                }
                .addOnFailureListener { e ->
                                   }
            // Dismiss the bottom sheet dialog
            dismiss()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the parent fragment implements the listener interface
        if (parentFragment is Fragment_bottom_sheet_email.OnNameEnteredListener) {
            listener = parentFragment as Fragment_bottom_sheet_email.OnNameEnteredListener
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
