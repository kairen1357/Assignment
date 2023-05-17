package com.example.assignment_test

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.assignment_test.databinding.FragmentMonthlyDurationChartBinding
import com.example.assignment_test.databinding.FragmentMonthlyKcalChartBinding
import com.example.assignment_test.databinding.FragmentWeeklyDurationChartBinding
import com.example.assignment_test.databinding.FragmentWeeklyKcalChartBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class FragmentSubFragmentWeeklyKcal : Fragment() {

    private lateinit var binding : FragmentWeeklyKcalChartBinding
    private lateinit var line_chart: LineChart

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeeklyKcalChartBinding.inflate(layoutInflater)

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
                val changedTextView=binding.weekRangeTextview.text.toString()
                if(currentTextView == changedTextView)
                {
                    Toast.makeText(requireContext(),"$currentTextView + $changedTextView",Toast.LENGTH_LONG).show()
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
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDate(date:LocalDate) {
        val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = startOfWeek.plusDays(6)
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val startFormatted = startOfWeek.format(formatter)
        val endFormatted = endOfWeek.format(formatter)
        val year=date.year
        binding.weekRangeTextview.text = "$startFormatted - $endFormatted"
        binding.yearTextview.text = "$year"
    }

    override fun onResume() {
        super.onResume()
        setUpLineChart()
    }

    private fun setUpLineChart(){
        val chart = binding.lineChart

        // create a list of days to use as x-axis labels
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        // create a list of durations for each day of the week (you'll need to replace this with your actual data)
        val durations = listOf(2f, 4.3f, 6.9f,10.5f, 12f, 13.1f,21.3f)

        // create an ArrayList of Entry objects to represent the data points on the chart
        val entries = ArrayList<Entry>()
        for (i in 0 until daysOfWeek.size) {
            entries.add(Entry(i.toFloat(), durations[i]))
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

}