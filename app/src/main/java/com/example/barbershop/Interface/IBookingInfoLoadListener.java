package com.example.barbershop.Interface;

import com.example.barbershop.model.BookingInformation;

/**
 * Created by Harri Pratomo on 20/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public interface IBookingInfoLoadListener {
    void onBookingInfoLoadempty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String documentId);
    void onBookingInfoLoadFailed(String message);
}
