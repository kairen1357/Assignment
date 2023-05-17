package com.example.assignment_test

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class CreateFoodActivity:AppCompatActivity() {

    private lateinit var foodNameEditText: EditText
    private lateinit var calorieCountEditText: EditText
    private lateinit var categoryRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createfood)

        // Get references to the EditText views
        foodNameEditText = findViewById(R.id.foodName_Create)
        calorieCountEditText = findViewById(R.id.calories_Create)
        categoryRadioGroup = findViewById(R.id.category_rg)
        val breakfastRadioButton = findViewById<RadioButton>(R.id.breakfast_rb)
        val lunchRadioButton = findViewById<RadioButton>(R.id.lunch_rb)
        val dinnerRadioButton = findViewById<RadioButton>(R.id.dinner_rb)

        // Get reference to Firebase database
        val database = FirebaseDatabase.getInstance()

        // Add a click listener to the "Create" button
        val createButton = findViewById<Button>(R.id.create_Button)
        createButton.setOnClickListener {
            // Get the food name and calorie count values from the EditText views
            val foodName = foodNameEditText.text.toString().trim()
            val calorieCount = calorieCountEditText.text.toString().toIntOrNull()

            // Get the selected category from the RadioGroup
            val selectedCategory = when (categoryRadioGroup.checkedRadioButtonId) {
                R.id.breakfast_rb -> "breakfast"
                R.id.lunch_rb -> "lunch"
                R.id.dinner_rb -> "dinner"
                else -> ""
            }

            // Validate that all fields are filled
            if (foodName.isEmpty() || calorieCount == null || selectedCategory.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new food item
            val foodId = database.getReference(selectedCategory).push().key ?: ""
            val food = FoodItem(foodName, calorieCount)

            // Add the food item to the selected category in the database
            database.getReference(selectedCategory).child(foodId).setValue(food)

            // Show a success message and finish the activity
            Toast.makeText(this, "Food item created successfully.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}