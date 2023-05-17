package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import android.os.Handler

import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout

class SplashScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val motionLayout = findViewById<MotionLayout>(R.id.motionLayout)
            motionLayout.transitionToEnd()
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)




    }
}