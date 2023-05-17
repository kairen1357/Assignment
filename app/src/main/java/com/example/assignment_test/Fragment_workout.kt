package com.example.assignment_test

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil.setContentView
import com.example.assignment_test.databinding.FragmentReportBinding
import com.example.assignment_test.databinding.FragmentWorkoutBinding

class Fragment_workout : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        binding.indoorLv1Layout.setOnClickListener {
            val intent = Intent(requireActivity(), indoorWorkOutLvl1_list_frag::class.java)
            startActivity(intent)
        }

        binding.indoorLv2Layout.setOnClickListener {
            val intent = Intent(requireActivity(), indoorWorkOutLvl2_list_frag::class.java)
            startActivity(intent)
        }

        binding.indoorLv3Layout.setOnClickListener {
            val intent = Intent(requireActivity(), indoorWorkOutLvl3_list_frag::class.java)
            startActivity(intent)
        }

        binding.outdoorLv2Layout.setOnClickListener {
            val intent = Intent(requireActivity(), outdoorWorkOutLvl1_list_frag::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}