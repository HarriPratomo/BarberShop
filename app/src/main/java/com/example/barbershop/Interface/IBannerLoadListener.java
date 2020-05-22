package com.example.barbershop.Interface;

import com.example.barbershop.model.Banner;

import java.util.List;

/**
 * Created by Harri Pratomo on 17/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
