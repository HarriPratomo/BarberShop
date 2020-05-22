package com.example.barbershop.adapter;

import com.example.barbershop.fragment.BookingStep1Fragment;
import com.example.barbershop.fragment.BookingStep2Fragment;
import com.example.barbershop.fragment.BookingStep3Fragment;
import com.example.barbershop.fragment.BookingStep4Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {

    public MyViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return BookingStep1Fragment.getInstance();
            case 1:
                return BookingStep2Fragment.getInstance();
            case 2:
                return BookingStep3Fragment.getInstance();
            case 3:
                return BookingStep4Fragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
