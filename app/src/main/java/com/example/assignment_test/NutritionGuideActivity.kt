package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NutritionGuideActivity : AppCompatActivity(){

    private lateinit var searchView: SearchView
    private lateinit var database: DatabaseReference
    private lateinit var search:AutoCompleteTextView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nutrition_guide)

        //self create food button
        val selfCreate = findViewById<Button>(R.id.selfCreate_button)
        selfCreate.setOnClickListener {
            val intent = Intent(this,CreateFoodActivity::class.java)
            startActivity(intent)
        }

        //search = findViewById(R.id.nutrition_searchbar)
        search = findViewById(R.id.test_searchbar)

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("breakfast")
        val ref2 = database.getReference("lunch")
        val ref3 = database.getReference("dinner")

//        fun showPopup(food: FoodItem) {
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle(food.name)
//            builder.setMessage("Calories: ${food.calories}")
//            builder.setPositiveButton("OK", null)
//            builder.show()
//        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 1) {
                    showDropDownList(s.toString())
                }
            }
        })

        search.setOnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position) as String
            performSearch(selected)
        }



        // Set up the adapter for the drop-down list
        /*val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        search.setAdapter(adapter)

        // Listen for text changes and update the drop-down list accordingly
        search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                if (query.length >= 3) {
                    showDropDownList(query)
                } else {
                    adapter.clear()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // Handle item selection from the drop-down list
        search.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = adapter.getItem(position)
            // Perform the search for the selected item
            if (selectedItem != null) {
                performSearch(selectedItem)
            }
        }*/



        /*fun searchDatabase(query: String) {
            val queryRef = ref.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
            queryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.children.mapNotNull { it.getValue(FoodItem::class.java) }
                    val exactMatch = result.find { it.name.equals(query, ignoreCase = true) }
                    if (exactMatch != null) {
                        showPopup(exactMatch)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Failed to read value.", error.toException())
                }
            })
            val queryRef2 = ref2.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
            queryRef2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.children.mapNotNull { it.getValue(FoodItem::class.java) }
                    val exactMatch = result.find { it.name.equals(query, ignoreCase = true) }
                    if (exactMatch != null) {
                        showPopup(exactMatch)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Failed to read value.", error.toException())
                }
            })
            val queryRef3 = ref3.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
            queryRef3.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.children.mapNotNull { it.getValue(FoodItem::class.java) }
                    val exactMatch = result.find { it.name.equals(query, ignoreCase = true) }
                    if (exactMatch != null) {
                        showPopup(exactMatch)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Failed to read value.", error.toException())
                }
            })
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchDatabase(newText)
                }
                return true
            }
        })*/

        val breakfast = findViewById<ImageView>(R.id.breakfast_button)
        breakfast.setOnClickListener {
            val intent = Intent(this,BreakfastActivity::class.java)
            startActivity(intent)
        }
        val lunch = findViewById<ImageView>(R.id.lunch_button)
        lunch.setOnClickListener {
            Log.i("lunch", "is Clicked")
            val intent = Intent(this,LunchActivity::class.java)
            startActivity(intent)
        }
        val dinner = findViewById<ImageView>(R.id.dinner_button)
        dinner.setOnClickListener {
            Log.i("dinner", "is Clicked")
            val intent = Intent(this,DinnerActivity::class.java)
            startActivity(intent)
        }
        val favourite = findViewById<ImageButton>(R.id.favourites_button)
        favourite.setOnClickListener {
            val intent = Intent(this,FavouriteActivity::class.java)
            startActivity(intent)
        }

        Toast.makeText(this,"Firebase connection Success", Toast.LENGTH_LONG).show()
    }

    private fun showDropDownList(query: String) {
        val dbRef = FirebaseDatabase.getInstance().reference
        val suggestions = mutableListOf<String>()

        // Search for matching food items in the "breakfast" category
        dbRef.child("breakfast").orderByChild("name").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val foodItem = childSnapshot.getValue(FoodItem::class.java)
                        if (foodItem != null) {
                            suggestions.add(foodItem.name)
                        }
                    }
                    // Search for matching food items in the "lunch" category
                    dbRef.child("lunch").orderByChild("name").startAt(query).endAt(query + "\uf8ff")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (childSnapshot in snapshot.children) {
                                    val foodItem = childSnapshot.getValue(FoodItem::class.java)
                                    if (foodItem != null) {
                                        suggestions.add(foodItem.name)
                                    }
                                }
                                // Search for matching food items in the "dinner" category
                                dbRef.child("dinner").orderByChild("name").startAt(query).endAt(query + "\uf8ff")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (childSnapshot in snapshot.children) {
                                                val foodItem = childSnapshot.getValue(FoodItem::class.java)
                                                if (foodItem != null) {
                                                    suggestions.add(foodItem.name)
                                                }
                                            }
                                            // Create an adapter for the dropdown list
                                            val adapter = ArrayAdapter<String>(this@NutritionGuideActivity, android.R.layout.simple_dropdown_item_1line, suggestions.distinct())
                                            // Set the adapter for the search bar
                                            search.setAdapter(adapter)
                                            // Show the dropdown list
                                            search.showDropDown()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("TAG", "Failed to read value.", error.toException())
                                        }
                                    })
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("TAG", "Failed to read value.", error.toException())
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Failed to read value.", error.toException())
                }
            })
    }

    private fun performSearch(query: String) {
        val ref = FirebaseDatabase.getInstance().getReference()
        val searchResults = mutableListOf<FoodItem>()
        val mealTypes = arrayOf("breakfast", "lunch", "dinner")
        var numQueries = 0

        for (mealType in mealTypes) {
            val queryRef = ref.child(mealType).orderByChild("name").startAt(query).endAt(query + "\uf8ff")
            queryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val foodItem = childSnapshot.getValue(FoodItem::class.java)
                        if (foodItem != null) {
                            searchResults.add(foodItem)
                        }
                    }
                    numQueries++
                    if (numQueries == mealTypes.size) {
                        if (searchResults.isNotEmpty()) {
                            showPopup(searchResults)
                        } else {
                            Toast.makeText(this@NutritionGuideActivity, "No results found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun showPopup(foodItems: List<FoodItem>) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.nutrition_search_popup, null)

        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        popupView.findViewById<TextView>(R.id.title).text = "Search Results"
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FoodItemAdapter(foodItems)

        popupView.findViewById<Button>(R.id.close_btn).setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.isFocusable = true
        popupWindow.showAtLocation(search, Gravity.CENTER, 0, 0)
    }

    class FoodItemAdapter(private val foodItems: List<FoodItem>) : RecyclerView.Adapter<FoodItemAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.food_name)
            val caloriesTextView: TextView = itemView.findViewById(R.id.favorite_calories)
            val icon : ImageView =  itemView.findViewById(R.id.favoriteicon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val foodItem = foodItems[position]
            holder.nameTextView.text = foodItem.name
            holder.caloriesTextView.text = "${foodItem.calories} calories"
            if (foodItem.isFavorite) {
                holder.icon.setImageResource(R.drawable.redlove)
            } else {
                holder.icon.setImageResource(R.drawable.whitelove)
            }
            holder.icon.setOnClickListener {
                foodItem.isFavorite = !foodItem.isFavorite
                updateFavoriteStatus(foodItem, holder)
                notifyDataSetChanged()
            }

        }

        override fun getItemCount(): Int {
            return foodItems.size
        }

        private fun updateFavoriteStatus(foodItem: FoodItem, holder: ViewHolder) {
//            val dbRef = FirebaseDatabase.getInstance().getReference("food-items").child(foodItem.name)
//            if (foodItem.isFavorite) {
//                dbRef.setValue(foodItem)
//            } else {
//                dbRef.removeValue()
//            }
            // Update the favorite state of the food item in the Firebase Realtime Database
            val databaseReference = FirebaseDatabase.getInstance().getReference("food-items")
            val key = foodItem.name.replace("\\s+".toRegex(), "-")
            val updatedFoodItem = FoodItem(foodItem.name, foodItem.calories)
            databaseReference.child(key).setValue(updatedFoodItem)
            databaseReference.child(key).child("isFavorite").setValue(foodItem.isFavorite)

            // Show a toast message when the update is successful
            val message = if (foodItem.isFavorite) "${foodItem.name} added to favorites" else "${foodItem.name} removed from favorites"
            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

//    private fun showPopup(searchResults: List<FoodItem>) {
//        val builder = AlertDialog.Builder(this@NutritionGuideActivity)
//        builder.setTitle("Search Results")
//        val items = searchResults.map { it.name + " (Calories: ${it.calories})" }.toTypedArray()
//
//        builder.setItems(items) { _, which ->
//            val selectedFood = searchResults[which]
//            val foodBuilder = AlertDialog.Builder(this@NutritionGuideActivity)
//            foodBuilder.setTitle(selectedFood.name)
//            foodBuilder.setMessage("Calories: ${selectedFood.calories}")
//            foodBuilder.setPositiveButton("OK", null)
//            foodBuilder.create().show()
//        }
//        builder.create().show()
//    }


