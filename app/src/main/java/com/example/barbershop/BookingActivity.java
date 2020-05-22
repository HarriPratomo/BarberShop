package com.example.barbershop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.barbershop.Common.Common;
import com.example.barbershop.Common.NonSwipeViewPager;
import com.example.barbershop.adapter.MyViewPagerAdapter;
import com.example.barbershop.model.Barber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference barberRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.viewPager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_prev_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

    @OnClick(R.id.btn_previous_step)
    void previousStep() {
        if (Common.step == 3 || Common.step > 0) {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if (Common.step<3)
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick() {
        if (Common.step < 3 || Common.step == 0) {
            Common.step++;
            if (Common.step == 1) {
                if (Common.currentSalon != null)
                    loadBarberBySalon(Common.currentSalon.getSalonId());
            } else if (Common.step == 2) {
                if (Common.currentBarber != null) {
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberId());
                }
            }
            else if (Common.step == 3) {
                if (Common.currentTimeSlot != -1) {
                   confirmBooking();
                }
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfBarber(String barberId) {
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadBarberBySalon(String salonId) {
        dialog.show();
        if (!TextUtils.isEmpty(Common.city)) {
            barberRef = FirebaseFirestore.getInstance().collection("AllSalon")
                    .document(Common.city)
                    .collection("Branch")
                    .document(salonId)
                    .collection("Barbers");
            barberRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<Barber> barbers = new ArrayList<>();
                    for (QueryDocumentSnapshot barbersnapshot : task.getResult()) {
                        Barber barber = barbersnapshot.toObject(Barber.class);
                        barber.setPassword("");
                        barber.setBarberId(barbersnapshot.getId());
                        barbers.add(barber);
                    }
                    Intent intent = new Intent(Common.KEY_BARBER_LOAD_DONE);
                    intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE, barbers);
                    localBroadcastManager.sendBroadcast(intent);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }

    }

    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if (step == 1)
                Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step == 2)
                Common.currentBarber = intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
            else if (step == 3)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT,-1);
            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(this);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));
        setUpstepView();
        setColorButton();
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);//must declared with ...fragment
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                stepView.go(position, true);
                if (position == 0)
                    btn_prev_step.setEnabled(false);
                else
                    btn_prev_step.setEnabled(true);
                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled()) {
            btn_next_step.setBackgroundResource(R.color.colorPrimary);
        } else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_prev_step.isEnabled()) {
            btn_prev_step.setBackgroundResource(R.color.colorPrimary);
        } else {
            btn_prev_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setUpstepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Salon");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }
}
