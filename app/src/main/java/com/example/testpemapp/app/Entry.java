package com.example.testpemapp.app;

import android.graphics.Bitmap;

import com.parse.ParseUser;


public class Entry {

    public String id;
    public String title;
    public boolean geschenk;
    public Bitmap picture;
    public double price;
    public String description;
    public ParseUser name;


    //zum testen:
//
//    public Entry(String title){
//        this.title = title;
//    }


    public Entry(String id, String title, boolean geschenk, Bitmap picture, double price, String description, ParseUser name) {
        this.id = id;
        this.title = title;
        this.geschenk = geschenk;
        this.picture = picture;

        this.price = price;
        this.description = description;
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setName(ParseUser name) {
        this.name = name;
    }

    String nameString = "";

    public ParseUser getName() {
        return name;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isGeschenk() {
        return geschenk;
    }

    public void setGeschenk(boolean geschenk) {
        this.geschenk = geschenk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Entry{" +
                "title='" + title + '\'' +
                '}';
    }


}
