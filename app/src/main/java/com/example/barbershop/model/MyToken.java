package com.example.barbershop.model;

import com.example.barbershop.Common.Common;

/**
 * Created by Harri Pratomo on 21/05/2020.
 * <p>
 * harrypratomo135@gmail.com
 */
public class MyToken {
    private String userPhone;
    private Common.TOKEN_TYPE token_type;
    private String token;

    public MyToken() {
    }

    public MyToken(String userPhone, Common.TOKEN_TYPE token_type, String token) {
        this.userPhone = userPhone;
        this.token_type = token_type;
        this.token = token;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Common.TOKEN_TYPE getToken_type() {
        return token_type;
    }

    public void setToken_type(Common.TOKEN_TYPE token_type) {
        this.token_type = token_type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
