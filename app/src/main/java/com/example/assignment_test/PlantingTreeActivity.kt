package com.example.assignment_test

import FirstTimeDialog
import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment_test.databinding.PlantingTreeBinding
import com.example.assignment_test.leaderboardAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log


class PlantingTreeActivity : AppCompatActivity(){

    lateinit var binding : PlantingTreeBinding
    var username ="abc123"
    var workOutPoint=0
    var powerPlantScore=0
    var powerPlantPointUsedPerClick=100
    var currentUserPowerPlantScoreClickable = true
    var isAnimationPlaying = false

    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<User>

    private lateinit var dialog: FirstTimeDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=PlantingTreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get username

//        getUsername()

        //initialize

        binding.currentUserTotalContributedText.isClickable=currentUserPowerPlantScoreClickable
        getWorkOutPoint()
        FirebaseApp.initializeApp(this)



        //trigger planting action
        binding.workOutPointTextField.text="$workOutPoint / 100"

        //first time dialog
        if(powerPlantScore==0){
            dialog = FirstTimeDialog(this)
            dialog.show()
        }

        binding.plantingVideo.setOnClickListener{
            if (workOutPoint >= 100 ) {

                if (!isAnimationPlaying) {
                    triggerPlantingVideo()

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

        val slideLeftAnimation = TranslateAnimation(0f, -150f, 0f, 0f)
        slideLeftAnimation.duration = 500 // Animation duration in milliseconds
        slideLeftAnimation.fillAfter = true // Keeps the final position of the animation

        val slideRightAnimation = TranslateAnimation(-150f, 0f, 0f, 0f)
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

    private fun triggerPlantingVideo() {

        val plantingVideo: LottieAnimationView = binding.plantingVideo
        when{
            powerPlantScore<=100 -> plantingVideo.setAnimation(R.raw.planting_video1)
            powerPlantScore<=300 -> plantingVideo.setAnimation(R.raw.planting_video2)
            else -> plantingVideo.setAnimation(R.raw.planting_video3)

        }

        // Your existing code for animation

        // Update the currentPowerPlantScore and workOutPoint values in the database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("Users")

        val query = usersRef.orderByChild("name").equalTo(username)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userRef = userSnapshot.ref
                        val newPowerPlantScore = powerPlantScore + powerPlantPointUsedPerClick
                        val newWorkOutPoint = workOutPoint - powerPlantPointUsedPerClick

                        // Update the values in the database
                        userRef.child("powerPlantScore").setValue(newPowerPlantScore)
                        userRef.child("workOutPoint").setValue(newWorkOutPoint)

                        // Update the local variables and UI
                        powerPlantScore = newPowerPlantScore
                        workOutPoint = newWorkOutPoint
                        binding.currentUserTotalContributedText.text = "Power Plant Score : $powerPlantScore"
                        binding.workOutPointTextField.text = "$workOutPoint / 100"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

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
                if (snapshot.exists()) {
                    val userListFromDatabase = ArrayList<User>()

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let {
                            userListFromDatabase.add(it)
                        }
                    }

                    // Sort the user list in reverse order based on powerPlantScore
                    userListFromDatabase.sortByDescending { it.powerPlantScore }

                    // Store records 1 to 3 in variables
                    val name1 = userListFromDatabase.getOrNull(0)?.name ?: ""
                    val powerPlantScore1 = userListFromDatabase.getOrNull(0)?.powerPlantScore ?: 0

                    val name2 = userListFromDatabase.getOrNull(1)?.name ?: ""
                    val powerPlantScore2 = userListFromDatabase.getOrNull(1)?.powerPlantScore ?: 0

                    val name3 = userListFromDatabase.getOrNull(2)?.name ?: ""
                    val powerPlantScore3 = userListFromDatabase.getOrNull(2)?.powerPlantScore ?: 0

                    binding.userLeaderboardName1.text=name1
                    binding.userLeaderboardName2.text=name2
                    binding.userLeaderboardName3.text=name3
                    binding.userContributedWorkOutPoint1.text=powerPlantScore1.toString()
                    binding.userContributedWorkOutPoint2.text=powerPlantScore2.toString()
                    binding.userContributedWorkOutPoint3.text=powerPlantScore3.toString()

                    // Prepare the list for RecyclerView by removing records 1 to 3
                    val visibleUserList = userListFromDatabase.subList(3, minOf(userListFromDatabase.size, 10))

                    // Initialize the adapter with the visibleUserList
                    val adapter = leaderboardAdapter(ArrayList(visibleUserList))

                    // Set the adapter to the RecyclerView
                    userRecyclerview.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun getWorkOutPoint(){
        FirebaseApp.initializeApp(applicationContext)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("Users")

        val query = usersRef.orderByChild("name").equalTo(username)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val tempWorkOutPoint = userSnapshot.child("workOutPoint").getValue(Int::class.java) ?: 0
                        workOutPoint=tempWorkOutPoint
                        // Use the workOutPoint value as needed

                        binding.workOutPointTextField.text="$workOutPoint / 100"

                        val tempPowerPlantScore = userSnapshot.child("powerPlantScore").getValue(Int::class.java) ?: 0
                        powerPlantScore=tempPowerPlantScore
                        binding.currentUserTotalContributedText.text="Power Plant Score : $powerPlantScore"
                    }
                } else {
                    // Username not found
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun getUsername(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var databaseRef = FirebaseDatabase.getInstance().reference.child("SignupUsers").child(uid)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.value as? HashMap<String, Any>
                if (userData != null) {
                    username = userData["username"].toString()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })

    }


}


