package com.example.barbershop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.barbershop.R;
import com.example.barbershop.model.Banner;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class LookBookAdapter extends RecyclerView.Adapter<LookBookAdapter.ViewHolder> {

    private List<Banner> bannerList;
    private Context context;

    public LookBookAdapter(List<Banner> bannerList, Context context) {
        this.bannerList = bannerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_look_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(bannerList.get(position).getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_look_book)
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
