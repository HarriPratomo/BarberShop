package com.example.barbershop.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.barbershop.Common.Common;
import com.example.barbershop.Common.SpaceItemDecoration;
import com.example.barbershop.Interface.AllSalonListener;
import com.example.barbershop.Interface.IBranchLoadListener;
import com.example.barbershop.R;
import com.example.barbershop.adapter.MySalonAdapter;
import com.example.barbershop.model.Salon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class BookingStep1Fragment extends Fragment implements AllSalonListener, IBranchLoadListener {

    @BindView(R.id.rv_salon)
    RecyclerView rv_salon;
    @BindView(R.id.spinner)
    MaterialSpinner spinner;

    Unbinder unbinder;

    CollectionReference allsalonRef;
    CollectionReference branchRef;

    AllSalonListener allSalonListener;
    IBranchLoadListener iBranchLoadListener;

    static BookingStep1Fragment instance;
    AlertDialog dialog;


    public static BookingStep1Fragment getInstance(){
        if (instance==null)
            instance = new BookingStep1Fragment();
        return instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allsalonRef = FirebaseFirestore.getInstance().collection("AllSalon");
        allSalonListener = this;
        iBranchLoadListener = this;
        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View view =  inflater.inflate(R.layout.fragment_booking_step_one,container,false);
         unbinder = ButterKnife.bind(this, view);
         initView();
         loadAllSalon();
         return view;
    }

    private void initView() {
        rv_salon.setHasFixedSize(true);
        rv_salon.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rv_salon.addItemDecoration(new SpaceItemDecoration(4));
    }

    private void loadAllSalon() {
        allsalonRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> list = new ArrayList<>();
                            list.add("Please choose city");
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                                list.add(documentSnapshot.getId());
                            allSalonListener.onAllSalonLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    allSalonListener.onAllSalonLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position>0){
                    loadBranchOfCity(item.toString());
                }
                else
                    rv_salon.setVisibility(View.GONE);
            }
        });
    }

    private void loadBranchOfCity(String cityName) {
        dialog.show();
        Common.city = cityName;
        branchRef = FirebaseFirestore.getInstance().collection("AllSalon")
                .document(cityName).collection("Branch");
        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Salon> list = new ArrayList<>();
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        Salon salon = documentSnapshot.toObject(Salon.class);
                        salon.setSalonId(documentSnapshot.getId());
                        list.add(salon);
                    }
                    iBranchLoadListener.onBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.onBranchLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<Salon> salonList) {
        MySalonAdapter adapter = new MySalonAdapter(getActivity(), salonList);
        rv_salon.setAdapter(adapter);
        rv_salon.setVisibility(View.VISIBLE);
        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
