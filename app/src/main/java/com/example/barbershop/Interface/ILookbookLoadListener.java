package com.example.barbershop.Interface;

import com.example.barbershop.model.Banner;

import java.util.List;

/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public interface ILookbookLoadListener {
    void onLookBookLoadSuccess(List<Banner> banners);
    void onLookBookLoadFailed(String message);
}
