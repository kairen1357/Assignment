package com.example.assignment_test

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LunchActivity:AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val breakfastRef = database.getReference("lunch")
    private val foodItems = mutableListOf<FoodItem>()


    private lateinit var linearLayout: LinearLayout
    private lateinit var foodList: MutableList<FoodItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lunch)

        linearLayout = findViewById(R.id.foodLayout_lunch)
        foodList = mutableListOf()

        val foodRef = FirebaseDatabase.getInstance().getReference("lunch")
//            val newFoodRef = breakfastRef.push()
//
//            val food = FoodItem("Milk", 100)
//            newFoodRef.setValue(food)

//        val foodItem = FoodItem(name = "Grilled chicken breast sandwich", calories = 320)
//        val key = foodItem.name.replace("\\s+".toRegex(), "-")
//
//        foodRef.child(key).setValue(foodItem).addOnSuccessListener {
//            Toast.makeText(this, "Food added successfully", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(this, "Failed to add food", Toast.LENGTH_SHORT).show()
//        }

        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnap in snapshot.children) {
                    val food = foodSnap.getValue(FoodItem::class.java)
                    foodList.add(food!!)
                    addFoodToLayout(food)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LunchActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addFoodToLayout(food: FoodItem) {
        val foodView = layoutInflater.inflate(R.layout.food_item, null)
        val nameTextView = foodView.findViewById<TextView>(R.id.food_name)
        val caloriesTextView = foodView.findViewById<TextView>(R.id.favorite_calories)
        val favoriteIcon = foodView.findViewById<ImageView>(R.id.favoriteicon)
        val deleteIcon = foodView.findViewById<ImageView>(R.id.Remove_icon)

        nameTextView.text = food.name
        caloriesTextView.text = food.calories.toString()

        var isFavorite = false
        favoriteIcon.setOnClickListener {
            Log.i("love", "is clicked")
            //favoriteIcon.setImageResource(R.drawable.redlove)
            // Toggle the favorite state
            isFavorite = !isFavorite

            // Change the image to the red heart icon if the item is a favorite,
            // or to the normal heart icon if the item is not a favorite
            val heartIconResource = if (isFavorite) R.drawable.redlove else R.drawable.whitelove
            favoriteIcon.setImageResource(heartIconResource)

            // Update the favorite state of the food item in the Firebase Realtime Database
            val databaseReference = FirebaseDatabase.getInstance().getReference("food-items")
            val key = food.name.replace("\\s+".toRegex(), "-")
            val updatedFoodItem = FoodItem(food.name, food.calories)
            databaseReference.child(key).setValue(updatedFoodItem)
            databaseReference.child(key).child("isFavorite").setValue(isFavorite)

            // Show a toast message when the update is successful
            val message = if (isFavorite) "${food.name} added to favorites" else "${food.name} removed from favorites"
            Toast.makeText(foodView.context, message, Toast.LENGTH_SHORT).show()

        }
        deleteIcon.setOnClickListener {
            showConfirmDialog(food, foodView) { confirmed ->
                if (confirmed as Boolean) {
                    // Delete the food item from the database
                    val database = FirebaseDatabase.getInstance()
                    val foodsRef = database.getReference("lunch")
                    val query = foodsRef.orderByChild("name").equalTo(food.name)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (itemSnapshot in snapshot.children) {
                                itemSnapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Error deleting food item: ${error.message}")
                        }
                    })

                    // Remove the food item view from the layout
                    val parentView = foodView.parent as ViewGroup
                    parentView.removeView(foodView)

                    // Show a success message and dismiss the dialog
                    Toast.makeText(foodView.context, "${food.name} deleted successfully.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        linearLayout.addView(foodView)
    }
    private fun showConfirmDialog(food: FoodItem, foodView: View, foodLayout: (Any) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.food_popup, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.message_text_view)
        val yesButton = dialogView.findViewById<Button>(R.id.yes_button)
        val noButton = dialogView.findViewById<Button>(R.id.no_button)

        // Set the message text
        messageTextView.text = "Are you sure you want to delete ${food.name}?"

        // Create the dialog and show it
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        // Handle button clicks
        yesButton.setOnClickListener {
            // Delete the food item from the database
            val database = FirebaseDatabase.getInstance()
            val foodsRef = database.getReference("lunch")

            val query = foodsRef.orderByChild("name").equalTo(food.name)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        itemSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error deleting food item: ${error.message}")
                }
            })

            // Remove the food item view from the layout
            val parentView = foodView.parent as ViewGroup
            parentView.removeView(foodView)

            // Show a success message and dismiss the dialog
            Toast.makeText(this, "${food.name} deleted successfully.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        noButton.setOnClickListener {
            // Dismiss the dialog
            dialog.dismiss()
        }
    }

    companion object {
        private const val TAG = "LunchActivity"
    }
}