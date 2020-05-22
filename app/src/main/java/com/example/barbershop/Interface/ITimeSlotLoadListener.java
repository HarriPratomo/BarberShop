package com.example.barbershop.Interface;

import com.example.barbershop.model.TimeSlot;

import java.util.List;

/**
 * Created by Harri Pratomo on 19/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
