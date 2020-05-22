package com.example.barbershop.adapter;

import com.example.barbershop.model.Banner;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;


/**
 * Created by Harri Pratomo on 17/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class HomeSliderAdapter extends SliderAdapter {

    List<Banner> bannerList;

    public HomeSliderAdapter(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(bannerList.get(position).getImage());
    }
}
