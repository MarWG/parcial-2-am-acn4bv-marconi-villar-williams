package com.example.eternal_games.model;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Producto implements Serializable {
    @Exclude
    public String id;
    public String title;
    public String description;
    public int price;
    //public String code;
    public boolean status;
    public String platform;
    public boolean topSell;
    public String genre;
    public String category;
    // Imagen local por default
    public int img;
    @Exclude
    // Imagen web (URL)
    public String imgUrl;
    public Integer cantidad; //solo usado en compras por el momento

}