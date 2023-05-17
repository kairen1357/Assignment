package com.example.assignment_test

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.assignment_test.databinding.FragmentDailyKcalChartBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FragmentSubFragmentDailyKcal : Fragment() {
    data class WorkoutRecord(val caloriesBurnt: Long, val timeDecimal: Long)

    private lateinit var binding : FragmentDailyKcalChartBinding
    private lateinit var line_chart: LineChart

    interface DataRetrievalCallback {
        fun onDataRetrieved(workoutRecords: List<WorkoutRecord>)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyKcalChartBinding.inflate(layoutInflater)

        line_chart = binding.lineChart
        binding.rightButton.setOnClickListener{

        }

        val currentDate = LocalDate.now()
        val dateTextView = binding.dateTextview

        binding.rightButton.setColorFilter(Color.parseColor("#808080"))


        // Set initial date to today's date
        updateDate(currentDate)

        binding.rightButton.setOnClickListener {
            val textViewDate = LocalDate.parse(dateTextView.text, DateTimeFormatter.ofPattern("d MMMM yyyy"))
            val nextDate = textViewDate.plusDays(1)

            setUpLineChart()
            if(nextDate <= currentDate){
                updateDate(nextDate)
                if(nextDate == currentDate)
                {
                    binding.rightButton.setColorFilter(Color.parseColor("#808080"))
                }
            }

        }

        binding.leftButton.setOnClickListener {
            val textViewDate = LocalDate.parse(dateTextView.text, DateTimeFormatter.ofPattern("d MMMM yyyy"))
            val prevDate= textViewDate.minusDays(1)
            binding.rightButton.clearColorFilter()
            updateDate(prevDate)
        }


        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDate(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        binding.dateTextview.text = date.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        // assuming you have a LineChart object named "chart" in your layout
        setUpLineChart()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpLineChart() {
        retrieveData(object : DataRetrievalCallback {
            override fun onDataRetrieved(workoutRecords: List<WorkoutRecord>) {
                // Call the function to set up the line chart using the retrieved data
                updateLineChart(workoutRecords)
            }
        })

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateLineChart(workoutRecords: List<WorkoutRecord>){
        val chart = binding.lineChart
        val entries = ArrayList<Entry>()
        // create a list of days to use as x-axis labels
        val totalKcal = workoutRecords.sumBy { it.caloriesBurnt.toInt() }.toFloat()
        val hoursKcal=(totalKcal/3600)


            view?.findViewById<TextView>(R.id.total_kcal)!!.text= totalKcal.toString() + " Kcal"


        for (rec in workoutRecords) {
            entries.add(Entry(rec.timeDecimal.toFloat(), rec.caloriesBurnt.toFloat()))

        }
        // create a LineDataSet object to hold the data and customize the appearance of the line
        val dataSet = LineDataSet(entries, "Weekly Report")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(false)

        // create a LineData object to hold the LineDataSet and customize the appearance of the chart
        val data = LineData(dataSet)
        chart.data = data

        // customize the appearance of the chart's x-axis
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        val min = workoutRecords.minByOrNull { it.timeDecimal }
        xAxis.setAxisMinimum(min?.timeDecimal!!.toFloat())

        xAxis.granularity = 1f
        xAxis.setDrawLabels(false) // Hide X-axis labels
        xAxis.labelCount = 0

        // customize the appearance of the chart's y-axis
        val yAxis = chart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.axisMinimum = 0f
        chart.axisLeft.isEnabled = false

        // customize the appearance of the chart's right y-axis
        val rightAxis = chart.axisRight
        rightAxis.setDrawGridLines(false) // hide grid lines
        rightAxis.setDrawAxisLine(false)
        rightAxis.labelCount = 6 // set the number of labels to show

        // customize the appearance of the chart itself
        chart.setTouchEnabled(false)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateY(500)
        chart.invalidate()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun retrieveData(callback: DataRetrievalCallback) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val ref: DatabaseReference = database.reference
        val workoutRecords = mutableListOf<WorkoutRecord>()
        val currentDate = LocalDate.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = currentDate.format(dateFormat)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val query: Query = ref.child("User_Workout")
            .orderByChild("uid")
            .equalTo(uid)
        // Execute the query and retrieve the data
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val matchingRecords = mutableListOf<DataSnapshot>()

                for (recordSnapshot in dataSnapshot.children) {
                    val recordDate: String? = recordSnapshot.child("date").getValue(String::class.java)

                    // Filter the records based on the Date condition locally
                    if (recordDate == date) {
                        matchingRecords.add(recordSnapshot)
                    }
                }

                // Iterate over the matching records
                for (recordSnapshot in matchingRecords) {
                    val workoutId: String? = recordSnapshot.child("workout_ID").getValue(String::class.java)

                    // Get the corresponding Workout data based on WorkoutID
                    val workoutQuery: Query = ref.child("Workout")
                        .orderByKey()
                        .equalTo(workoutId)

                    workoutQuery.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(workoutSnapshot: DataSnapshot) {

                            for (workoutDataSnapshot in workoutSnapshot.children) {
                                val startTime: String? = recordSnapshot.child("start_time").getValue(String::class.java)
                                val caloriesBurnt: Long? = workoutDataSnapshot.child("calories_burned").getValue(Long::class.java)
                                val timeParts = startTime!!.split(":")

                                val timeDecimal = timeParts[0].toDouble() + timeParts[1].toDouble()/ 60

                                val record = WorkoutRecord(caloriesBurnt!!,
                                    timeDecimal.toLong()
                                )
                                workoutRecords.add(record)

                            }
                            callback.onDataRetrieved(workoutRecords)

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle any errors that occur
                        }
                    })
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
                println("Query cancelled or failed: ${databaseError.message}")
            }


        })

    }

}


