package com.example.barbershop.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;


/**
 * Created by Harri Pratomo on 23/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class MyShoppingItemAdapter extends RecyclerView.Adapter<MyShoppingItemAdapter.ViewHolder> {



    @NonNull
    @Override
    public MyShoppingItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyShoppingItemAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
