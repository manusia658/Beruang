package com.innovatech.beruang;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.zip.Inflater;

public class PageAdapter extends FragmentPagerAdapter {

    int number_tabs;
    BottomNavigationView navigationView;

    public PageAdapter(FragmentManager fragmentManager, int number_tabs, BottomNavigationView navigationView){
        super(fragmentManager);
        this.number_tabs = number_tabs;
        this.navigationView = navigationView;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Mainfragment();
            case 1:
                return new LeftFragment();
            default:
                return new Mainfragment();
        }
    }

    @Override
    public int getCount() {
        return number_tabs;
    }
}
