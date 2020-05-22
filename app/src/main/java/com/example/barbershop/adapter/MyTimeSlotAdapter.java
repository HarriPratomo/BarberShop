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
import com.example.barbershop.model.TimeSlot;

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
public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.ViewHolder> {

    private Context context;
    private List<TimeSlot> timeSlotList;
    private List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyTimeSlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTimeSlotAdapter.ViewHolder holder, final int position) {
        holder.txt_timeslot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());
        if (timeSlotList.size() == 0) {
            holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.description_time.setText("Available");
            holder.description_time.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_timeslot.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            for (TimeSlot slotValue : timeSlotList) {
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == position) {
                    holder.cardTimeSlot.setTag(Common.DISABLE_TAG);
                    holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.description_time.setText("Full");
                    holder.description_time.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.txt_timeslot.setTextColor(context.getResources().getColor(android.R.color.white));
                }
            }
        }
        if (!cardViewList.contains(holder.cardTimeSlot))
            cardViewList.add(holder.cardTimeSlot);


           holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
               @Override
               public void onItemSelectedListener(View view, int pos) {
                   for (CardView cardView:cardViewList) {
                       if (cardView.getTag() == null)
                           cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                   }
                   holder.cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                   Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                   intent.putExtra(Common.KEY_TIME_SLOT,position);//check if fail
                   intent.putExtra(Common.KEY_STEP,3);
                   localBroadcastManager.sendBroadcast(intent);
               }
           });

    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_time_slot)
        TextView txt_timeslot;
        @BindView(R.id.txt_time_slot_description)
        TextView description_time;
        @BindView(R.id.card_time_slot)
        CardView cardTimeSlot;
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
