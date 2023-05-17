package com.example.assignment_test

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouriteActivity:AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noFoodToShow: TextView
    private var totalCalories: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_food)

        recyclerView = findViewById(R.id.favorite_list)
        noFoodToShow = findViewById(R.id.noFoodToShow)

        // Retrieve the favorite food items from the Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("food-items")
        val query = databaseReference.orderByChild("isFavorite").equalTo(true)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val favoriteItems = mutableListOf<FoodItem>()
                totalCalories = 0
                for (foodItemSnapshot in dataSnapshot.children) {
                    val foodItem = foodItemSnapshot.getValue(FoodItem::class.java)
                    if (foodItem != null) {
                        favoriteItems.add(foodItem)
                        totalCalories += foodItem.calories
                    }
                }
                updateTotalCalories()

                // Pass the list of favorite food items to the adapter
                val adapter = FavoriteAdapter(favoriteItems)
                recyclerView.adapter = adapter

                // Show "No food to show" message if the list is empty
                if (favoriteItems.isEmpty()) {
                    noFoodToShow.visibility = View.VISIBLE
                } else {
                    noFoodToShow.visibility = View.GONE
                }

                // Use a GridLayoutManager to display multiple items at once
               // val layoutManager = GridLayoutManager(applicationContext, 2)
               // recyclerView.layoutManager = layoutManager
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FavoriteActivity", "Error retrieving favorite food items: $databaseError")
            }
        })

        // Set the layout manager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)


    }
    private fun updateTotalCalories() {
        val totalCaloriesView = findViewById<TextView>(R.id.total_calories)
        totalCaloriesView.text = "Total calories: $totalCalories"
    }


    class FavoriteAdapter(private val favoriteItems: MutableList<FoodItem>) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
            return FavoriteViewHolder(view)
        }

        override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
            holder.bind(favoriteItems[position])
        }

        override fun getItemCount(): Int {
            return favoriteItems.size
        }

        inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(foodItem: FoodItem) {
                val foodName = itemView.findViewById<TextView>(R.id.favorite_foodName)
                val calories = itemView.findViewById<TextView>(R.id.favorite_calories)
                val icon = itemView.findViewById<ImageView>(R.id.favoriteIcon)

                foodName.text = foodItem.name
                calories.text = foodItem.calories.toString()
                icon.setImageResource(R.drawable.redlove)

//                if (foodItem.isFavorite) {
//                    icon.setImageResource(R.drawable.redlove)
//                } else {
//                    icon.setImageResource(R.drawable.whitelove)
//                }


                icon.setOnClickListener {
                    val databaseReference = FirebaseDatabase.getInstance().getReference("food-items")
                    val key = foodItem.name.replace("\\s+".toRegex(), "-")
                    Toast.makeText(itemView.context, "${foodItem.name} removed from favorites", Toast.LENGTH_SHORT).show()

                    databaseReference.child(key).child("isFavorite").setValue(false).addOnSuccessListener {
                        // Remove the item from the list and notify the adapter
                        val index = favoriteItems.indexOf(foodItem)
                        favoriteItems.remove(foodItem)
                        notifyItemRemoved(index)
                    }
                }
            }
        }
    }
}