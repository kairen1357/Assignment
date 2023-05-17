package com.example.assignment_test

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import android.view.animation.AccelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView


class SignUpSub1Activity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_sub1)

        //text animation
        val textView: TextView = findViewById(R.id.sub1_text2)
        //val textToDisplay = textView.text
        //textView.text = textToDisplay
        textView.startAnimation(createSlideInAnimationLeft())

        //picture animation
        val robotPic: ImageView = findViewById(R.id.robot)
        robotPic.startAnimation(createSlideInAnimationRight())

        //button animation
        val goButton : Button = findViewById(R.id.ready_button)
        goButton.startAnimation(createSlideInAnimationBottom())


        //set button onClickListener
        goButton.setOnClickListener {
           /* val signUpSub1Fragment = SignUpSub1_fragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.signUpSub1_Frame, signUpSub1Fragment)
                commit()
            }*/
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.signUpSub1_Frame, SignUpSub1_fragment())
            fragmentTransaction.commit()
           // val intent = Intent(this, SignUpSub1_fragment::class.java)
           // startActivity(intent)
            Log.i("ButtonClick", "Button is successful clicked")
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