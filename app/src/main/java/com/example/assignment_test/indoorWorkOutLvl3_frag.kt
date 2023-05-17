package com.example.assignment_test

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pl.droidsonroids.gif.GifImageView
import java.lang.Math.ceil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class indoorWorkOutLvl3_frag : Fragment() {
    private var currentExercisePosition = 0
    private lateinit var currentExercise: Exercise
    private lateinit var timer: CountDownTimer
    private lateinit var progressBar: ProgressBar
    private var timerStarted = false
    private lateinit var nameTextView: TextView
    private lateinit var gifImageView: GifImageView
    private var workoutTypeGL: String? = null
    private var caloriesBurnedGL: Int? = null
    private var durationGL: Int? = null
    private lateinit var uid:String
    private lateinit var timeStart:String
    private lateinit var date:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_indoor_work_out_lvl1_frag, container, false)

        val currentUser = FirebaseAuth.getInstance().currentUser
        uid = currentUser?.uid.toString()

        nameTextView = view.findViewById<TextView>(R.id.nameWorkout)
        gifImageView = view.findViewById<GifImageView>(R.id.gifImageView)
        val timerTextView = view.findViewById<TextView>(R.id.timerDuration)
        val nextButton = view.findViewById<AppCompatImageView>(R.id.nextWorkout)
        val previousButton = view.findViewById<AppCompatImageView>(R.id.previousWorkout)
        val pauseButton = view.findViewById<AppCompatImageView>(R.id.pauseWorkout)
        progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.progress = 0

        val backButton: ImageButton = view.findViewById(R.id.backBtn)

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        currentExercise = exercises[currentExercisePosition]

        nameTextView.text = currentExercise.name
        gifImageView.setImageResource(currentExercise.gifResourceId)

        startTimer(timerTextView)

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        date = dateFormat.format(currentDate)
        timeStart = timeFormat.format(currentDate)

        nextButton.setOnClickListener {


            timer.cancel()
            currentExercisePosition++
            if (currentExercisePosition >= exercises.size) {
                // Last exercise completed
                timerStarted = false
                progressBar.progress = 100 // Set progress to 100% to indicate completion
                nameTextView.text = "Completed" // Set text to "Complete"
                timerTextView.text = "Nice !"
                getWorkout()
                // Set image to check mark icon
                // gifImageView.setImageResource(R.drawable.check_mark)
            } else {
                currentExercise = exercises[currentExercisePosition]
                nameTextView.text = currentExercise.name
                gifImageView.setImageResource(currentExercise.gifResourceId)
                timerTextView.text = "${currentExercise.duration} seconds"
                timerStarted = false
                startTimer(timerTextView)
            }

        }


        previousButton.setOnClickListener {

            if (currentExercisePosition == 0) {
                timer.cancel()
                timerStarted = false
                progressBar.progress = 0
                currentExercise = exercises[currentExercisePosition]
                nameTextView.text = currentExercise.name
                gifImageView.setImageResource(currentExercise.gifResourceId)
                timerTextView.text = "${currentExercise.duration} seconds"
                startTimer(timerTextView)
                Toast.makeText(requireContext(), "You're already at the first exercise!", Toast.LENGTH_SHORT).show()
            } else {
                timer.cancel()
                currentExercisePosition--
                currentExercise = exercises[currentExercisePosition]
                nameTextView.text = currentExercise.name
                gifImageView.setImageResource(currentExercise.gifResourceId)
                timerTextView.text = "${currentExercise.duration} seconds"
                timerStarted = false
                startTimer(timerTextView)
            }
        }


        pauseButton.setOnClickListener {
            if (timerStarted) {
                // pause the timer
                timer.cancel()
                timerStarted = false

                nextButton.isEnabled = false
                previousButton.isEnabled = false

                // change the button icon to play button
                pauseButton.setImageResource(R.drawable.logo_play)
            } else {
                // start the timer
                startTimer(timerTextView)
                timerStarted = true

                nextButton.isEnabled = true
                previousButton.isEnabled = true

                // change the button icon to pause button
                pauseButton.setImageResource(R.drawable.logo_stop_circle)
            }
        }
        return view
    }

    private fun startTimer(timerTextView: TextView) {
        timer = object : CountDownTimer((currentExercise.duration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = ceil(millisUntilFinished / 1000.0).toInt()
                timerTextView.text = "$remainingSeconds seconds"
                val progress = ((currentExercise.duration - remainingSeconds) * 100) / currentExercise.duration
                progressBar.progress = progress
            }

            override fun onFinish() {
                currentExercisePosition++
                if (currentExercisePosition >= exercises.size) {
                    // Last exercise completed
                    timerStarted = false
                    progressBar.progress = 100 // Set progress to 100% to indicate completion
                    nameTextView.text = "Completed" // Set text to "Complete"
                    timerTextView.text = "Nice !"
                    getWorkout()
//                    gifImageView.setImageResource(R.drawable.) // Set image to check mark icon
                    return // Stop looping
                }
                currentExercise = exercises[currentExercisePosition]
                progressBar.progress = 0
                nameTextView.text = currentExercise.name
                gifImageView.setImageResource(currentExercise.gifResourceId)
                timerStarted = false
                timerTextView.text = "${currentExercise.duration} seconds"
                startTimer(timerTextView)
            }
        }

        if (!timerStarted) {
            timerStarted = true
            timer.start()
        }
    }

    companion object {
        val exercises = arrayOf(
            Exercise("Bent Over Twists", R.drawable.gif_bent_over_twist, 45),
            Exercise("Jumping Jacks", R.drawable.gif_jumping_jacks, 50),
            Exercise("Burpees", R.drawable.gif_burpees, 120),
            Exercise("Squat", R.drawable.gif_squat, 60),
            Exercise("Back Extension", R.drawable.gif_back_extension, 30),
            Exercise("Run in Place", R.drawable.gif_run_in_place, 120),
            Exercise("Reverse Crunches", R.drawable.gif_reverse_crunches, 60),
            Exercise("Arm Circles", R.drawable.gif_arm_circles, 30),
            Exercise("Standing Criss-Cross", R.drawable.gif_standing_criss_cross, 50),
            Exercise("Plank", R.drawable.gif_plank, 120)
        )
    }

    data class Exercise(val name: String, val gifResourceId: Int, val duration: Int)

    private var workoutListener: ValueEventListener? = null
    private fun getWorkout() {
        val workoutRef = FirebaseDatabase.getInstance().getReference("Workout")
        val workoutId = "w3"
        println("I AM BEING CALLED")
        workoutListener = workoutRef.child(workoutId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle data change event
                val workoutData = dataSnapshot.getValue(indoorw1::class.java)
                // Access the workout data object here
                val workoutType = workoutData?.workout_type
                val caloriesBurned = workoutData?.calories_burned
                val duration = workoutData?.duration

                // Print the retrieved data to the console
                println("Workout Type: $workoutType")
                println("Calories Burned: $caloriesBurned")
                println("Duration: $duration")
                workoutTypeGL = workoutType
                caloriesBurnedGL = caloriesBurned
                durationGL = duration
                // Remove the listener
                workoutRef.child(workoutId).removeEventListener(workoutListener!!)
                workoutListener = null
                showPopup()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancelled event
            }
        })




    }

    private fun showPopup() {
        saveRecord()
        println("POPUP INITIATED")

        Handler().postDelayed({
            val myDialog = Dialog(requireContext())
            myDialog.setContentView(R.layout.popup_completed)
            val txtclose = myDialog.findViewById<TextView>(R.id.txtclose)
            val btnClose = myDialog.findViewById<Button>(R.id.btnClose)

            var workoutTypeDisplay: TextView = myDialog.findViewById(R.id.workoutType)
            val caloriesBurnedDisplay: TextView = myDialog.findViewById(R.id.caloriesBurned)
            val durationDisplay: TextView = myDialog.findViewById(R.id.duration)

            workoutTypeDisplay.text = workoutTypeGL
            caloriesBurnedDisplay.text = caloriesBurnedGL.toString()
            durationDisplay.text = durationGL.toString() + " Sec"
            println("NOT CLOSE BTN CLICKED")
            println("Workout Type: $workoutTypeGL")
            println("Calories Burned: $caloriesBurnedGL")
            println("Duration: $durationGL")

            myDialog.setCancelable(false)


            txtclose.setOnClickListener {
                println("CLOSE BTN CLICKED")
                println("Workout Type: $workoutTypeGL")
                println("Calories Burned: $caloriesBurnedGL")
                println("Duration: $durationGL")
                myDialog.dismiss()
                requireActivity().finish()

            }

            btnClose.setOnClickListener {
                println("CLOSE BTN CLICKED")
                myDialog.dismiss()
                requireActivity().finish()


            }
            myDialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.transparent)))
            myDialog.show()
        }, 500)
    }

    private fun saveRecord(){
        val database = FirebaseDatabase.getInstance().getReference()
        val parentNode = database.child("User_Workout")
        val newNodeKey = parentNode.push().key
        val newNodeData = HashMap<String, Any>()

        newNodeData["uid"] = uid
        newNodeData["workout_ID"] = "w3"
        newNodeData["date"] = date
        newNodeData["start_time"] = timeStart

        val reference = FirebaseDatabase.getInstance().getReference("SignupUsers")

        reference.child(uid).child("powerPlantScore").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentScore = dataSnapshot.getValue(Int::class.java) ?: 0
                val divisionResult = (685 / 60).toInt()
                val newScore = currentScore + divisionResult

                reference.child(uid).child("powerPlantScore").setValue(newScore)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })

        val childUpdates = HashMap<String, Any>()
        childUpdates["$newNodeKey"] = newNodeData

        parentNode.updateChildren(childUpdates)
            .addOnSuccessListener {
                // New node created successfully
            }
            .addOnFailureListener { exception ->
                // Failed to create new node
            }
    }


}


