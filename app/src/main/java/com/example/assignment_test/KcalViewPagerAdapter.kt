package com.example.assignment_test

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class KcalViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->FragmentSubFragmentDailyKcal()
            1->FragmentSubFragmentWeeklyKcal()
            2->FragmentSubFragmentMonthlyKcal()
            3->FragmentSubFragmentYearlyKcal()
            else->Fragment()
        }
    }
}