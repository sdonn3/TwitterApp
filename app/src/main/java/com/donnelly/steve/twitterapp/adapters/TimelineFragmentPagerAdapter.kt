package com.donnelly.steve.twitterapp.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.donnelly.steve.twitterapp.HomeFragment
import com.donnelly.steve.twitterapp.MentionsFragment

class TimelineFragmentPagerAdapter(fm: FragmentManager, val context: Context) : FragmentPagerAdapter(fm) {

    lateinit var homeFragment: HomeFragment
    lateinit var mentionsFragment: MentionsFragment

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> {
                homeFragment = HomeFragment.newInstance()
                homeFragment
            }
            1 -> {
                mentionsFragment = MentionsFragment.newInstance()
                mentionsFragment
            }
            else -> {
                null
            }
        }
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    companion object {
        const val PAGE_COUNT = 2
        val tabTitles = arrayListOf("HOME", "MENTIONS")
    }
}