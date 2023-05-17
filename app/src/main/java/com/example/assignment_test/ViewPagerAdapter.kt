package com.example.assignment_test

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->FragmentSubFragmentDailyDuration()
            1->FragmentSubFragmentWeeklyDuration()
            2->FragmentSubFragmentMonthlyDuration()
            3->FragmentSubFragmentYearlyDuration()
            else->Fragment()
        }
    }
}