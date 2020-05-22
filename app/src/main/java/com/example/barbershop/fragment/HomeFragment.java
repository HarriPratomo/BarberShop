package com.example.barbershop.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barbershop.BookingActivity;
import com.example.barbershop.Common.Common;
import com.example.barbershop.Interface.IBannerLoadListener;
import com.example.barbershop.Interface.IBookingInfoLoadListener;
import com.example.barbershop.Interface.IBookingInformationChangeListener;
import com.example.barbershop.Interface.ILookbookLoadListener;
import com.example.barbershop.R;
import com.example.barbershop.adapter.HomeSliderAdapter;
import com.example.barbershop.adapter.LookBookAdapter;
import com.example.barbershop.model.Banner;
import com.example.barbershop.model.BookingInformation;
import com.example.barbershop.service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IBannerLoadListener, ILookbookLoadListener, IBookingInfoLoadListener, IBookingInformationChangeListener {

    private Unbinder unbinder;
    private AlertDialog dialog;
    @BindView(R.id.layout_user_information)
    LinearLayout layout_user;
    @BindView(R.id.textUserName)
    TextView username;
    @BindView(R.id.banner_slider)
    Slider bannerSlider;
    @BindView(R.id.rv_look_book)
    RecyclerView rv_look_book;
    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_barber)
    TextView txt_salon_barber;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;

    @OnClick(R.id.btn_delete_booking)
    void deleteBooking() {
        deleteBookingFromBarber(false);
    }

    @OnClick(R.id.btn_change_booking)
    void changeBooking() {
        changeBookingFromUser(false);
    }

    private void changeBookingFromUser(boolean b) {
        AlertDialog.Builder dialogs = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Hey !")
                .setMessage("Do you really want change the old booking?\nBecause we will delete the old booking information\nJust Confirm")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBookingFromBarber(true);
                    }
                });
        dialogs.show();

    }


    @OnClick(R.id.card_viewbooking)
    void booking() {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    CollectionReference bannerRef, lookbookref;
    IBannerLoadListener iBannerLoadListener;
    ILookbookLoadListener iLookbookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;

    public HomeFragment() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        lookbookref = FirebaseFirestore.getInstance().collection("lookbook");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
    }

    private void deleteBookingFromBarber(boolean isChange) {

        if (Common.currentBooking != null) {
            dialog.show();
            DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Barbers")
                    .document(Common.currentBooking.getBarberId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());

            barberBookingInfo.delete().addOnFailureListener(e ->
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(aVoid -> {
                        deleteBookingfromUser(isChange);
                    })
                    .addOnSuccessListener(aVoid -> {
                        Paper.init(getActivity());
                        Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                        getActivity().getContentResolver().delete(eventUri, null, null);
                        Toast.makeText(getContext(), "Success Delete Booking", Toast.LENGTH_SHORT).show();
                        loadUserBooking();


                    });

        } else {
            Toast.makeText(getContext(), "Current Booking must be null", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBookingfromUser(boolean isChange) {
        if (!TextUtils.isEmpty(Common.currentBookingId)) {
            DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);

            userBookingInfo.delete().addOnFailureListener((Exception e) -> {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Paper.init(getActivity());
                    Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                    getActivity().getContentResolver().delete(eventUri, null, null);
                    Toast.makeText(getContext(), "Success Delete Booking", Toast.LENGTH_SHORT).show();
                    loadUserBooking();

                    if (isChange)
                        iBookingInformationChangeListener.onBookingInformationChange();
                    dialog.dismiss();

                }
            });
        } else {
            dialog.dismiss();
            Toast.makeText(getContext(), "Booking information must not be empty..", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadUserBooking() {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp todayTimeStamp = new Timestamp(calendar.getTime());
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", todayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.getId());
                                break;
                            }
                        } else
                            iBookingInfoLoadListener.onBookingInfoLoadempty();
                    }
                }).addOnFailureListener(e -> iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iLookbookLoadListener = this;
        iBookingInfoLoadListener = this;
        iBookingInformationChangeListener = this;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            setUserInformation();
            loadBanner();
            loadLookBook();
            loadUserBooking();
        }
        return view;
    }

    private void loadLookBook() {
        lookbookref.get()
                .addOnCompleteListener(task -> {
                    List<Banner> lookbooks = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot bannerSnapshot : task.getResult()) {
                            Banner banner = bannerSnapshot.toObject(Banner.class);
                            lookbooks.add(banner);
                        }
                        iLookbookLoadListener.onLookBookLoadSuccess(lookbooks);
                    }
                }).addOnFailureListener(e -> iLookbookLoadListener.onLookBookLoadFailed(e.getMessage()));
    }

    private void loadBanner() {
        bannerRef.get()
                .addOnCompleteListener(task -> {
                    List<Banner> banners = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot bannerSnapshot : task.getResult()) {
                            Banner banner = bannerSnapshot.toObject(Banner.class);
                            banners.add(banner);
                        }
                        iBannerLoadListener.onBannerLoadSuccess(banners);
                    }
                }).addOnFailureListener(e -> iBannerLoadListener.onBannerLoadFailed(e.getMessage()));
    }

    private void setUserInformation() {
        layout_user.setVisibility(View.VISIBLE);
        username.setText(Common.currentUser.getName());
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        bannerSlider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLookBookLoadSuccess(List<Banner> banners) {
        rv_look_book.setHasFixedSize(true);
        rv_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_look_book.setAdapter(new LookBookAdapter(banners, getActivity()));
    }

    @Override
    public void onLookBookLoadFailed(String message) {

    }

    @Override
    public void onBookingInfoLoadempty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String bookingId) {
        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookingId;
        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_barber.setText(bookingInformation.getBarberName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();
        txt_time_remain.setText(dateRemain);
        card_booking_info.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInformationChange() {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }
}
