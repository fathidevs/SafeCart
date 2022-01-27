package com.gmail.safecart.blacklist

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class BlacklistViewPagerAdapter(manager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(manager, lifecycle){
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
       return when(position){
            0 -> CountryFragment()
            1 -> ManufacturerFragment()
           else -> Fragment()
       }
    }
}