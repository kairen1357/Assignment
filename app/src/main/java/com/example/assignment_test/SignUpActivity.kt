package com.example.assignment_test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.assignment_test.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_signup)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_signup)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //background video display
        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.video)
        videoView.setVideoURI(videoUri)
        // loop mute and start video
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0f,0f)
            mp.start()
        }

        //set up button on click (start Button)
        val startButton : Button = findViewById(R.id.start_button)
        startButton.setOnClickListener {
            val startIntent = Intent(this, SignUpSub1Activity::class.java)
            startActivity(startIntent)
        }

        //set text intent to login activity (login button)
        //val goLoginText : TextView = findViewById(R.id.GoToLogin)
        binding.GoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}