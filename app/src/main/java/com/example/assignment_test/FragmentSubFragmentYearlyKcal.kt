package com.example.assignment_test

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.assignment_test.databinding.FragmentYearlyKcalChartBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Arrays


class FragmentSubFragmentYearlyKcal : Fragment() {
    data class WorkoutRecord(val caloriesBurnt: Long, val date: String)

    private lateinit var binding : FragmentYearlyKcalChartBinding
    private lateinit var line_chart: LineChart
    interface DataRetrievalCallback {
        fun onDataRetrieved(workoutRecords: List<WorkoutRecord>)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYearlyKcalChartBinding.inflate(layoutInflater)
        line_chart = binding.linechart
        binding.rightButton.setOnClickListener{
            setUpLineChart()
        }

        binding.rightButton.setColorFilter(Color.parseColor("#808080"))

        val dateTextView = binding.yearTextview

        // Set initial date to today's date
        val currentDate = LocalDate.now()
        updateDate(currentDate)
        val currentTextView=binding.yearTextview.text.toString()

        binding.rightButton.setOnClickListener {
            val textViewDate = LocalDate.parse("01 "+ dateTextView.text, DateTimeFormatter.ofPattern("dd MMM yyyy"))
            val nextDate = textViewDate.plusMonths(1)
            if(nextDate <= currentDate){
                updateDate(nextDate)
                val changedTextView=binding.yearTextview.text.toString()
                if(currentTextView == changedTextView)
                {
                    binding.rightButton.setColorFilter(Color.parseColor("#808080"))
                }
            }
        }

        binding.leftButton.setOnClickListener {
            val textViewDate = LocalDate.parse("01 " + dateTextView.text, DateTimeFormatter.ofPattern("dd MMM yyyy"))
            val prevDate= textViewDate.minusMonths(1)
            binding.rightButton.clearColorFilter()
            updateDate(prevDate)
        }


        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDate(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
        binding.yearTextview.text = date.format(formatter)
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
        val chart = binding.linechart

        // create a list of days to use as x-axis labels
        val totalKcal = workoutRecords.sumBy { it.caloriesBurnt!!.toInt() }.toFloat()


        view?.findViewById<TextView>(R.id.total_kcal)!!.text= totalKcal.toString() + " Kcal"



        val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")


        val kcal = arrayOfNulls<Int>(12)

        Arrays.fill(kcal, 0)

        for (rec in workoutRecords) {
            for(i in 0..11){
                if(rec.date.substring(5,7).toInt()==i)
                {
                    kcal[i] = kcal[i]!! + rec.caloriesBurnt.toInt()
                }

            }
        }

        // create an ArrayList of Entry objects to represent the data points on the chart
        val entries = ArrayList<Entry>()
        for (i in 0 until kcal.size) {
            entries.add(Entry(i.toFloat(), kcal[i]?.toFloat() ?: 0f))
        }

        // create a LineDataSet object to hold the data and customize the appearance of the line
        val dataSet = LineDataSet(entries, "Monthly Report")
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
        xAxis.labelCount = months.size
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.setAxisMinimum(0f)
        xAxis.setAxisMaximum(months.size.toFloat() - 1)
        xAxis.granularity = 1f

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
                    if (recordDate!!.substring(0, 4) == date.substring(0, 4)) {
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
                                val caloriesBurnt: Long? = workoutDataSnapshot.child("calories_burned").getValue(Long::class.java)
                                val recordDate: String? = recordSnapshot.child("date").getValue(String::class.java)


                                val record = WorkoutRecord(
                                    caloriesBurnt!!,
                                    recordDate.toString()
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


