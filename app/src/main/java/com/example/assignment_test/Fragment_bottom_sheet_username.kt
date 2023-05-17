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

class Fragment_bottom_sheet_username : BottomSheetDialogFragment() {

    // Declare the listener variable
    private var listener: OnUserIdEnteredListener? = null
    private lateinit var databaseRef: DatabaseReference

    // Declare the view variables
    private lateinit var editTextUsername: EditText
    private lateinit var buttonSave: Button

    // Define the listener interface
    interface OnUserIdEnteredListener {
        fun onInputReceived(input: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(layout.fragment_bottom_sheet_username, container, false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)

        // Find the view references
        editTextUsername = view.findViewById(R.id.username_changed)
        buttonSave = view.findViewById(R.id.save_button)



        // Set the click listener for the save button
        buttonSave.setOnClickListener {
            // Get the user input
            val username = editTextUsername.text.toString().trim()

            // Call the listener method to pass the user input to the parent fragment
            listener?.onInputReceived(username)
            databaseRef.child("username").setValue(username)
                .addOnSuccessListener {
                    }
                .addOnFailureListener { e ->
                    // Error occurred while updating data
                }
            // Dismiss the bottom sheet dialog
            dismiss()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the parent fragment implements the listener interface
        if (parentFragment is OnUserIdEnteredListener) {
            listener = parentFragment as OnUserIdEnteredListener
        } else {
            throw RuntimeException("$context must implement OnUserIdEnteredListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        // Reset the listener variable
        listener = null
    }
}
