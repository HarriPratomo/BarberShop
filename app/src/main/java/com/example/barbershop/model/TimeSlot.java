package com.example.barbershop.model;

/**
 * Created by Harri Pratomo on 19/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class TimeSlot {
    private Long slot;

    public TimeSlot() {
    }

    public TimeSlot(Long slot) {
        this.slot = slot;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }
}
