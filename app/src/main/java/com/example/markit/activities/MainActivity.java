package com.example.markit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.markit.R;
import com.example.markit.databinding.ActivityMainBinding;
import com.example.markit.fragment.FragmentHome;
import com.example.markit.fragment.FragmentMessage;
import com.example.markit.fragment.FragmentSearch;
import com.example.markit.fragment.FragmentSettings;
import com.example.markit.utilities.AdapterViewPager;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Fragment> fragmentsArrayList = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentsArrayList.add(new FragmentHome());
        fragmentsArrayList.add(new FragmentSearch());
        fragmentsArrayList.add(new FragmentMessage());
        fragmentsArrayList.add(new FragmentSettings());

        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragmentsArrayList);

        binding.pagerMain.setAdapter(adapterViewPager);
        setListeners();



    }

    private void setListeners(){
        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itHome:
                        binding.pagerMain.setCurrentItem(0);
                        break;
                    case R.id.itSearch:
                        binding.pagerMain.setCurrentItem(1);
                        break;
                    case R.id.itMessage:
                        binding.pagerMain.setCurrentItem(2);
                        break;
                    case R.id.itSettings:
                        binding.pagerMain.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        binding.pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.bottomNavBar.setSelectedItemId(R.id.itHome);
                        break;
                    case 1:
                        binding.bottomNavBar.setSelectedItemId(R.id.itSearch);
                        break;
                    case 2:
                        binding.bottomNavBar.setSelectedItemId(R.id.itMessage);
                        break;
                    case 3:
                        binding.bottomNavBar.setSelectedItemId(R.id.itSettings);
                        break;
                    default:
                        binding.bottomNavBar.setSelectedItemId(R.id.itHome);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }
}