package com.example.barbershop.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.barbershop.Common.Common;
import com.example.barbershop.Common.SpaceItemDecoration;
import com.example.barbershop.R;
import com.example.barbershop.adapter.MyBarberAdapter;
import com.example.barbershop.model.Barber;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class BookingStep2Fragment extends Fragment {

    static BookingStep2Fragment instance;
    @BindView(R.id.rv_barber)
    RecyclerView rv_barber;
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver barberdoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Barber> barberArrayList = intent.getParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE);
            MyBarberAdapter myBarberAdapter = new MyBarberAdapter(barberArrayList, getContext());
            rv_barber.setAdapter(myBarberAdapter);
        }
    };

    public static BookingStep2Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(barberdoneReceiver, new IntentFilter(Common.KEY_BARBER_LOAD_DONE));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(barberdoneReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_booking_step_two, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        rv_barber.setHasFixedSize(true);
        rv_barber.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rv_barber.addItemDecoration(new SpaceItemDecoration(4));
    }
}
