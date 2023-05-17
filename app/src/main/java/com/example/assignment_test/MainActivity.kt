package com.example.assignment_test

import Fragment_report
import MineFragment
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.assignment_test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.selectedItemId=R.id.home
        replaceFragment(FragmentHomePage())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.workout->replaceFragment(Fragment_workout())
                R.id.settings->replaceFragment(MineFragment())
                R.id.home->replaceFragment(FragmentHomePage())
                R.id.report->replaceFragment(Fragment_report())
                else->{

                }
            }
            true
        }

        val sharedPreferences=getSharedPreferences("Mode", Context.MODE_PRIVATE)
        val nightMode = sharedPreferences.getBoolean("night",false)

        if(nightMode)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }

    private fun replaceFragment(fragment : Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

}