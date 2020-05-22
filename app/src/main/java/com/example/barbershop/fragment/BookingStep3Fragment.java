package com.example.barbershop.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.barbershop.Common.Common;
import com.example.barbershop.Common.SpaceItemDecoration;
import com.example.barbershop.Interface.ITimeSlotLoadListener;
import com.example.barbershop.R;
import com.example.barbershop.adapter.MyTimeSlotAdapter;
import com.example.barbershop.model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.client.result.CalendarParsedResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class BookingStep3Fragment extends Fragment implements ITimeSlotLoadListener {

    static BookingStep3Fragment instance;
    DocumentReference barberDoc;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.rv_time_slot)
    RecyclerView rv_timeSlot;
    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;
    SimpleDateFormat simpleDateFormat;

    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE,0);
            loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberId(),
                    simpleDateFormat.format(date.getTime()));
        }
    };

    private void loadAvailableTimeSlotOfBarber(String barberId,final  String bookDate) {
        dialog.show();
        ///AllSalon/Jakarta Pusat/Branch/u6OmVkj39FafmW83UzWw/Barbers/YFhehZeajrcNiCUmKZIF
        barberDoc = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentSalon.getSalonId())
                .collection("Barbers")
                .document(Common.currentBarber.getBarberId());

        barberDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists())
                    {
                        CollectionReference date =FirebaseFirestore.getInstance()
                                .collection("AllSalon")
                                .document(Common.city)
                                .collection("Branch")
                                .document(Common.currentSalon.getSalonId())
                                .collection("Barbers")
                                .document(Common.currentBarber.getBarberId())
                                .collection(bookDate);

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty())
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    else
                                    {
                                        List<TimeSlot> timeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot document:task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(e -> {

                        });
                    }
                }
            }
        }).addOnFailureListener(e -> iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage()));
    }


    public static BookingStep3Fragment getInstance(){
        if (instance==null)
            instance = new BookingStep3Fragment();
        return instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iTimeSlotLoadListener =  this;
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot,new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));
        simpleDateFormat = new SimpleDateFormat("dd_MM_YYYY");
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();


    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_booking_step_three, container, false);
        unbinder = ButterKnife.bind(this, view);
        init(view);
        return view;
    }

    private void init(View view) {
        rv_timeSlot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rv_timeSlot.setLayoutManager(gridLayoutManager);
        rv_timeSlot.addItemDecoration(new SpaceItemDecoration(8));

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,2);

        HorizontalCalendar horizontalCalendar= new HorizontalCalendar.Builder(view,R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.bookingDate.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.bookingDate = date;
                    loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberId(),
                            simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext(),timeSlotList);
        rv_timeSlot.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext());
        rv_timeSlot.setAdapter(adapter);

        dialog.dismiss();
    }
}
