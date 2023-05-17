package com.example.assignment_test

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment_test.databinding.PlantingTreeBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlantingTreeActivity : AppCompatActivity(){

    lateinit var binding : PlantingTreeBinding
    var workOutPoint=1000
    var powerPlantScore=100
    var powerPlantPointUsedPerClick=100
    var currentUserPowerPlantScoreClickable = true
    var isAnimationPlaying = false

    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<User>

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=PlantingTreeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.currentUserTotalContributedText.isClickable=currentUserPowerPlantScoreClickable

        //trigger planting action
        binding.workOutPointTextField.text="$workOutPoint / 100"

        binding.plantingVideo.setOnClickListener{
            if (workOutPoint >= 100 ) {

                if (!isAnimationPlaying) {
                    triggerPlantingVideo(powerPlantScore)

                    workOutPoint-=powerPlantPointUsedPerClick
                    powerPlantScore+=powerPlantPointUsedPerClick
                    binding.workOutPointTextField.text="$workOutPoint / 100"
                    binding.currentUserTotalContributedText.text="Power Plant Score : $powerPlantScore"


                    if(currentUserPowerPlantScoreClickable){
                        triggerCurrentUserPowerPlantScore(powerPlantPointUsedPerClick)
                    }
                }

            }
            else{
                Toast.makeText( this,"no enough point", Toast.LENGTH_LONG).show()
            }
        }


        //Power Plant Score part
        binding.currentUserTotalContributedText.text="Power Plant Score : $powerPlantScore"

        binding.currentUserTotalContributedText.setOnClickListener {
            if(currentUserPowerPlantScoreClickable)
            triggerCurrentUserPowerPlantScore(0)
            }


        //leaderboard part
        binding.userLeaderboardName1.text="number1"
        binding.userLeaderboardName2.text="number2"
        binding.userLeaderboardName3.text="number3"

        binding.userContributedWorkOutPoint1.text="1000"
        binding.userContributedWorkOutPoint2.text="900"
        binding.userContributedWorkOutPoint3.text="800"



        //recyclerView part
        userRecyclerview = binding.leaderboardRecyclerView
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        userArrayList = arrayListOf<User>()
        getUserData()


    }

    private fun triggerCurrentUserPowerPlantScore(powerPlantScoreChanges : Int){

        val slideLeftAnimation = TranslateAnimation(0f, -100f, 0f, 0f)
        slideLeftAnimation.duration = 500 // Animation duration in milliseconds
        slideLeftAnimation.fillAfter = true // Keeps the final position of the animation

        val slideRightAnimation = TranslateAnimation(-100f, 0f, 0f, 0f)
        slideRightAnimation.duration = 500
        slideRightAnimation.fillAfter = true

        val handler = Handler()

        binding.currentUserTotalContributedText.startAnimation(slideLeftAnimation)

        currentUserPowerPlantScoreClickable=false

        handler.postDelayed({
            binding.currentUserTotalContributedText.startAnimation(slideRightAnimation)
            currentUserPowerPlantScoreClickable=true
        }, 5000)


    }

    private fun triggerPlantingVideo(powerPlantScore : Int) {

        val plantingVideo: LottieAnimationView = binding.plantingVideo
        when{
            powerPlantScore<=100 -> plantingVideo.setAnimation(R.raw.planting_video1)
            powerPlantScore<=300 -> plantingVideo.setAnimation(R.raw.planting_video2)
            else -> plantingVideo.setAnimation(R.raw.planting_video3)

        }


            isAnimationPlaying = true

            // Set up an AnimatorListener to listen for animation events
            val animationListener = object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // Animation started, perform actions here
                    // For example, you can interrupt the changes during the start
                }

                override fun onAnimationEnd(animation: Animator) {
                    // Animation ended, perform actions here
                    // For example, you can interrupt the changes during the end
                    isAnimationPlaying = false
                }

                override fun onAnimationCancel(animation: Animator) {
                    // Animation cancelled, perform actions here if needed
                    isAnimationPlaying = false
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // Animation repeated, perform actions here if needed
                }
            }

            // Set the animation listener to the LottieAnimationView
            binding.plantingVideo.addAnimatorListener(animationListener)

            // Start the animation
            binding.plantingVideo.playAnimation()
    }

    private fun getUserData() {

        dbref = FirebaseDatabase.getInstance().getReference("Users")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){

                        val user = userSnapshot.getValue(User::class.java)
                        userArrayList.add(user!!)

                    }

                    userRecyclerview.adapter = leaderboardAdapter(userArrayList)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

}

