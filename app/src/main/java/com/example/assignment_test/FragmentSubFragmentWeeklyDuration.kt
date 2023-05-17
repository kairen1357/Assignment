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
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.assignment_test.databinding.FragmentWeeklyDurationChartBinding
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Arrays

class FragmentSubFragmentWeeklyDuration : Fragment() {
    data class WorkoutRecord(val caloriesBurnt: Long, val date: String, val duration: Long)

    private lateinit var binding : FragmentWeeklyDurationChartBinding
    private lateinit var line_chart: LineChart
    private lateinit var date_:LocalDate

    interface DataRetrievalCallback {
        fun onDataRetrieved(workoutRecords: List<WorkoutRecord>)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeeklyDurationChartBinding.inflate(layoutInflater)

        line_chart = binding.lineChart
        binding.rightButton.setOnClickListener{
            setUpLineChart()
        }

        binding.rightButton.setColorFilter(Color.parseColor("#808080"))

        val dateTextView = binding.weekRangeTextview

        // Set initial date to today's date
        val currentDate = LocalDate.now()
        updateDate(currentDate)
        val currentTextView=binding.weekRangeTextview.text.toString()

        binding.rightButton.setOnClickListener {
            val textViewDate = LocalDate.parse(dateTextView.text.toString().substringBefore("-")+binding.yearTextview.text.toString(),
                DateTimeFormatter.ofPattern("dd MMM yyyy"))
            val nextDate= textViewDate.plusWeeks(1)
            if(nextDate <= currentDate){
                updateDate(nextDate)
                binding.lineChart.clear()
                setUpLineChart()
                val changedTextView=binding.weekRangeTextview.text.toString()
                if(currentTextView == changedTextView)
                {
                    binding.rightButton.setColorFilter(Color.parseColor("#808080"))
                }
            }
        }

        binding.leftButton.setOnClickListener {
            val currentDate = LocalDate.parse(dateTextView.text.toString().substringBefore("-")+binding.yearTextview.text.toString(),
                DateTimeFormatter.ofPattern("dd MMM yyyy"))
            val prevDate= currentDate.minusWeeks(1)
            binding.rightButton.clearColorFilter()
            updateDate(prevDate)
            binding.lineChart.clear()
            setUpLineChart()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDate(date:LocalDate) {
        val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        date_=startOfWeek
        val endOfWeek = startOfWeek.plusDays(6)
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val startFormatted = startOfWeek.format(formatter)
        val endFormatted = endOfWeek.format(formatter)
        val year=date.year
        binding.weekRangeTextview.text = "$startFormatted - $endFormatted"
        binding.yearTextview.text = "$year"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
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
        val totalDuration = workoutRecords.sumBy { it.duration!!.toInt() }.toFloat()
        val hoursDuration=(totalDuration/3600)

        if(hoursDuration >= 1){
            val minutes = ((hoursDuration - hoursDuration.toInt())*60).toInt()
            binding.totalDuration.text= hoursDuration.toInt().toString() + " Hours and  " + minutes.toString() + " minutes"
        }
        else if(hoursDuration < 1){
            val minutes = (totalDuration/60).toInt()
            binding.totalDuration.text= minutes.toString() + " minutes"
        }
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val totalduration = arrayOfNulls<Int>(7)


        Arrays.fill(totalduration, 0)

        for (rec in workoutRecords) {
            for(i in 0..6){
                if(LocalDate.parse(rec.date, dateFormat)== LocalDate.now().plusDays(i.toLong()))
                {
                    totalduration[i] = totalduration[i]!! + rec.duration.toInt()
                }

            }
        }

        for (i in 0 until 7) {
            if(totalduration[i]!=null){
                entries.add(Entry(i.toFloat(), totalduration[i]!!.toFloat()))

            }
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
        xAxis.labelCount = daysOfWeek.size
        xAxis.valueFormatter = IndexAxisValueFormatter(daysOfWeek)
        xAxis.setAxisMinimum(0f)
        xAxis.setAxisMaximum(daysOfWeek.size.toFloat() - 1)
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
        val date=date_
        val dates = (0..6).map { date.plusDays(it.toLong()).format(dateFormat) }
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
                    if (dates.contains(recordDate)) {
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
                                val duration: Long? = workoutDataSnapshot.child("duration").getValue(Long::class.java)
                                val caloriesBurnt: Long? = workoutDataSnapshot.child("calories_burned").getValue(Long::class.java)
                                val recordDate: String? = recordSnapshot.child("date").getValue(String::class.java)

                                val record = WorkoutRecord(
                                    caloriesBurnt!!,
                                    recordDate.toString(),
                                    duration!!,

                                )
                                workoutRecords.add(record)

                            }
                            callback.onDataRetrieved(workoutRecords)

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
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