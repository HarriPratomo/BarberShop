package com.example.barbershop.model;

/**
 * Created by Harri Pratomo on 22/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class ShoppingItem {
    private String name,image;
    private Long price;

    public ShoppingItem() {
    }

    public ShoppingItem(String name, String image, Long price) {
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
