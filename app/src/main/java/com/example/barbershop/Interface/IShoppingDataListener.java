package com.example.barbershop.Interface;

import com.example.barbershop.model.ShoppingItem;

import java.util.List;

/**
 * Created by Harri Pratomo on 22/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public interface IShoppingDataListener {
     void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) ;
     void onShoppingDataLoadFailed(String message) ;

}
