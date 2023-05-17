package com.example.assignment_test

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GenderSelectionActivity : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var gender: String
    private var age: Int = 0
    private var height: Int = 0
    private lateinit var weight: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.genderselection)

        // Get reference to Firebase database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("SignupUsers")

        // Get user sign up data from intent
        username = intent.getStringExtra("username") ?: ""
        age = intent.getIntExtra("age", 0)
        height = intent.getIntExtra("height", 0)
        weight = intent.getStringExtra("weight") ?: ""
        email = intent.getStringExtra("email") ?: ""



        //set back button onclick
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val userRef = usersRef.child(username)
            userRef.child("username").setValue("")
            val intent = Intent(this, InputNameActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            startActivity(intent)
        }

        // Get the progress value from gender selection activity
        var progressValue = intent.getIntExtra("progressValue", 0)

        // Set the progress bar to the progress value
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = progressValue


        //create val for imageView
        val male = findViewById<ImageView>(R.id.maleImage)
        val female = findViewById<ImageView>(R.id.femaleImage)

        //set male image onclick
        male.setOnClickListener {
            progressValue += 20
            //set the progress value to the progress bar
            progressBar.progress =  progressValue
            val intent = Intent(this,InputAgeActivity::class.java)
            intent.putExtra("progressValue",  progressValue)
            intent.putExtra("username",username)
            intent.putExtra("gender", "male") // Add selected gender to intent
            // Create UserSignUp object with updated gender information
            val userSignUp = UserSignUp(username, "male", age, height, weight, email )
            // Update user sign up data in Firebase database
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
            startActivity(intent)


        }
        //set female image onclick
        female.setOnClickListener {
            progressValue += 20
            //set the progress value to the progress bar
            progressBar.progress = progressValue
            val intent = Intent(this,InputAgeActivity::class.java)
            intent.putExtra("progressValue", progressValue)
            intent.putExtra("username",username)
            intent.putExtra("gender", "female") // Add selected gender to intent
            // Create UserSignUp object with updated gender information
            val userSignUp = UserSignUp(username, "female", age, height, weight, email)
            // Update user sign up data in Firebase database
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
            startActivity(intent)
        }
    }
    companion object {
        private const val TAG = "GenderSelectionActivity"
    }
}