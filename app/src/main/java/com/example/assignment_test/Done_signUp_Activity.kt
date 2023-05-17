package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Done_signUp_Activity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.done_signup)

        // Get the progress value from gender selection activity
        var progressValue = intent.getIntExtra("progressValue", 0)

        // Set the progress bar to the progress value
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.progress = progressValue

        //text animation
        val text2: TextView = findViewById(R.id.done_text2)
        text2.startAnimation(createSlideInAnimationLeft())
        val text3: TextView = findViewById(R.id.done_text3)
        text3.startAnimation(createSlideInAnimationRight())
        val doneButton: Button = findViewById(R.id.signup_done_button)
        doneButton.startAnimation(createSlideInAnimationBottom())


        //back button
        val backButton =  findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            progressValue -= 20
            //set the progress value to the progress bar
            progressBar.progress = progressValue
            val intent = Intent(this,SignUpActivity::class.java)
            intent.putExtra("progressValue", progressValue)
            startActivity(intent)
        }

        //done button
        //val doneButton = findViewById<Button>(R.id.signup_done_button)
        doneButton.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }
    private fun createSlideInAnimationLeft(): TranslateAnimation {
        val animation = TranslateAnimation(
            TranslateAnimation.RELATIVE_TO_PARENT, -1.0f,
            TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
            TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
            TranslateAnimation.RELATIVE_TO_SELF, 0.0f
        ).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
        }

        return animation
    }
    private fun createSlideInAnimationRight(): TranslateAnimation {
        val animation = TranslateAnimation(
            TranslateAnimation.RELATIVE_TO_PARENT, 1.0f,
            TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
            TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
            TranslateAnimation.RELATIVE_TO_SELF, 0.0f
        ).apply {
            duration = 800
            interpolator = AccelerateInterpolator()
        }

        return animation
    }

    private fun createSlideInAnimationBottom(): TranslateAnimation {
        val animation = TranslateAnimation(
            TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
            TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
            TranslateAnimation.RELATIVE_TO_SELF, 5.0f,
            TranslateAnimation.RELATIVE_TO_SELF, 0.0f
        ).apply {
            duration = 1300
            interpolator = AccelerateInterpolator()
        }

        return animation
    }
}