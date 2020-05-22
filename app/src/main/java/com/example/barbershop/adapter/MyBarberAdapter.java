package com.example.barbershop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.barbershop.Common.Common;
import com.example.barbershop.Interface.IRecyclerItemSelectedListener;
import com.example.barbershop.R;
import com.example.barbershop.model.Barber;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.ViewHolder> {

    private List<Barber> barberList;
    private Context context;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyBarberAdapter(List<Barber> barberList, Context context) {
        this.barberList = barberList;
        this.context = context;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyBarberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_barber, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyBarberAdapter.ViewHolder holder, int position) {
        Barber barber = barberList.get(position);
        holder.nameBarber.setText(barber.getName());
        holder.rtb_barber.setRating((float)(barber.getRating()));
        if (!cardViewList.contains(holder.cardbarber)){
            cardViewList.add(holder.cardbarber);
        }
        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView: cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }
                holder.cardbarber.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_BARBER_SELECTED,barberList.get(pos));
                intent.putExtra(Common.KEY_STEP,2);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.rtb_barber)
        RatingBar rtb_barber;
        @BindView(R.id.txt_name_barber)
        TextView nameBarber;
        @BindView(R.id.card_barber)
        CardView cardbarber;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
