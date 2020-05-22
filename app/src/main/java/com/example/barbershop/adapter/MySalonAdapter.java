package com.example.barbershop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.barbershop.Common.Common;
import com.example.barbershop.Interface.IRecyclerItemSelectedListener;
import com.example.barbershop.R;
import com.example.barbershop.model.Salon;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.ViewHolder> {
    private Context context;
    private List<Salon> salonList;
    private List<CardView> cardviewList;
    private LocalBroadcastManager localBroadcastManager;

    public MySalonAdapter(Context context, List<Salon> salonList) {
        this.context = context;
        this.salonList = salonList;
        this.cardviewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_salon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.salon_name.setText(salonList.get(position).getName());
        holder.salon_address.setText(salonList.get(position).getAddress());
        if (!cardviewList.contains(holder.cardsalon))
            cardviewList.add(holder.cardsalon);

        holder.setiRecyclerItemSelectedListener((view, pos) -> {
            for (CardView cardView : cardviewList)
                cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.cardsalon.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
            intent.putExtra(Common.KEY_SALON_STORE, salonList.get(pos));
            intent.putExtra(Common.KEY_STEP,1);
            localBroadcastManager.sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_salon_name)
        TextView salon_name;
        @BindView(R.id.txt_salon_address)
        TextView salon_address;
        @BindView(R.id.card_salon)
        CardView cardsalon;

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
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
