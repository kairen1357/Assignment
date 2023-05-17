package com.example.assignment_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.Query

class leaderboardAdapter(private val userList : ArrayList<User>) : RecyclerView.Adapter<leaderboardAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_user_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = userList[position]

        holder.ranking.text = currentItem.ranking.toString()
        holder.name.text = currentItem.name
        holder.powerPlantScore.text = currentItem.powerPlantScore.toString()

    }

    override fun getItemCount(): Int {

        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val ranking : TextView = itemView.findViewById(R.id.user_leaderboard_ranking)
        val name : TextView = itemView.findViewById(R.id.user_leaderboard_name)
        val powerPlantScore : TextView = itemView.findViewById(R.id.user_contributed_work_out_point)

    }

}