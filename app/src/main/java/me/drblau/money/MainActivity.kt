package me.drblau.money

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import me.drblau.money.databinding.ActivityMainBinding
import me.drblau.money.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, lifecycle, this)
        val viewPager = binding.viewPager
        viewPager.setAdapter(sectionsPagerAdapter)

        val tabs = binding.tabs

        TabLayoutMediator(tabs, viewPager) { tab, pos ->
            tab.setText(
                sectionsPagerAdapter.getPageTitle(
                    pos
                )
            )
        }.attach()
    }
}