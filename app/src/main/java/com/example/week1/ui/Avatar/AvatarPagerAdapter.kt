package com.example.week1.ui.Avatar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AvatarPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = listOf(
        ColorFragment(),
        HatFragment(),
        GlassesFragment(),
        ClothFragment(),
        BackgroundFragment()
    )

    private val fragmentTitleList = listOf(
        "색상",
        "모자",
        "안경",
        "의상",
        "배경"
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun getTitle(position: Int): String {
        return fragmentTitleList[position]
    }
}
