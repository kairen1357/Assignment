package com.example.assignment_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class outdoorWorkOutLvl1_list_frag : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_outdoor_work_out_lvl1_list_frag)

        val indoorBtnStartLvl3: Button =  findViewById(R.id.indoorStartBtnLvl3)
        val backButton: ImageButton = findViewById(R.id.backWorkout)
        backButton.setOnClickListener {
            finish()
        }

        indoorBtnStartLvl3.setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.outdoorLvl1Frag, outdoorWorkOutLvl1_frag())
                addToBackStack("outdoorWorkOutLvl1_list_frag")
                commit()
            }
            System.out.println("FRAGMENT IS CLICKED")
        }
    }
}