package com.example.barbershop.Interface;

import java.util.List;

/**
 * Created by Harri Pratomo on 18/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public interface AllSalonListener {
    void onAllSalonLoadSuccess(List<String> areaNameList);
    void onAllSalonLoadFailed(String message);
}
