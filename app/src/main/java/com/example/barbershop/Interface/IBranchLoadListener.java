package com.example.barbershop.Interface;

import com.example.barbershop.model.Salon;

import java.util.List;

/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
