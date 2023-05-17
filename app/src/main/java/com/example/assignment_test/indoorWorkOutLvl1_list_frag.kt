package com.example.assignment_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class indoorWorkOutLvl1_list_frag : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Fragment", "indoorWorkOutLvl1_frag created")
        setContentView(R.layout.activity_indoor_work_out_lvl1_list_frag)

        val indoorBtnStartLvl1: Button =  findViewById(R.id.indoorStartBtnLvl1)
        val backButton: ImageButton = findViewById(R.id.backWorkout)
        backButton.setOnClickListener {
            finish()
        }

        indoorBtnStartLvl1.setOnClickListener{

            System.out.println("FRAGMENT IS CLICKED")
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.indoorLvl1Frag, indoorWorkOutLvl1_frag())
                addToBackStack("indoorWorkOutLvl1_list_frag")
                commit()
            }



        }
    }
}