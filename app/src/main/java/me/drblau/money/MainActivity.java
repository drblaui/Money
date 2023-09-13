package me.drblau.money;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import me.drblau.money.databinding.ActivityMainBinding;
import me.drblau.money.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    //TODO: allow parsing JSON files
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        me.drblau.money.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle(), this);
        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        new TabLayoutMediator(tabs, viewPager, (tab, pos) -> tab.setText(sectionsPagerAdapter.getPageTitle(pos))).attach();
    }
}