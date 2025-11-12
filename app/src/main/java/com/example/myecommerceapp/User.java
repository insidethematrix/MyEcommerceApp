package com.example.myecommerceapp;

public class User {
    private String userName;
    private String userId;
    private String email;
    private ShoppingCart userCard;

    public User(String userName, String userId, String email ){
        this.userName=userName;
        this.userId=userId;
        this.email=email;
        this.userCard=new ShoppingCart();

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ShoppingCart getUserCard() {
        return userCard;
    }
}
