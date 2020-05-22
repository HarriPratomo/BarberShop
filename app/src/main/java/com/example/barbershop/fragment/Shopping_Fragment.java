package com.example.barbershop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.barbershop.Interface.IShoppingDataListener;
import com.example.barbershop.R;
import com.example.barbershop.model.ShoppingItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Harri Pratomo on 22/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class Shopping_Fragment extends Fragment implements IShoppingDataListener {
CollectionReference itemShoppingRef;
IShoppingDataListener iShoppingDataListener;
    Unbinder unbinder;
    @BindView(R.id.chip_groups)
    ChipGroup chip_groups;
    @BindView(R.id.chip_wax)
    Chip chip_wax;
    @OnClick(R.id.chip_wax)
    void waxChipClick(){
        setSelectedChip(chip_wax);
        loadShoppingItem(chip_wax.getText().toString());
    }

    private void loadShoppingItem(String itemMenu) {
                itemShoppingRef = FirebaseFirestore.getInstance().collection("Shopping")
                        .document(itemMenu)
                        .collection("Items");
                itemShoppingRef.get()
                        .addOnFailureListener(e -> iShoppingDataListener.onShoppingDataLoadFailed(e.getMessage())).addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                            {
                                List<ShoppingItem> shoppingItems = new ArrayList<>();
                                for (DocumentSnapshot itemSnapshot:task.getResult()){
                                    ShoppingItem shoppingItem = itemSnapshot.toObject(ShoppingItem.class);
                                    shoppingItems.add(shoppingItem);
                                }
                                iShoppingDataListener.onShoppingDataLoadSuccess(shoppingItems);
                            }
                        });

    }

    private void setSelectedChip(Chip chip) {
        for (int i = 0;i<chip_groups.getChildCount();i++){
            Chip chipItem = (Chip)chip_groups.getChildAt(i);
            if (chipItem.getId() != chip.getId())
            {
                chipItem.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            }
            else
            {
                chipItem.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    public Shopping_Fragment() {

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_shopping, container, false);
        unbinder = ButterKnife.bind(this, view);
        iShoppingDataListener  = this;
        return view;
    }

    @Override
    public void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) {

    }

    @Override
    public void onShoppingDataLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
