package com.example.testpemapp.app;

import android.graphics.Bitmap;

import com.parse.ParseUser;

/**
 * Created by Sindoora on 22.05.14.
 */
public class Entry {

    public int id;
    public String title;
    public boolean geschenk;
    public Bitmap picture;
    public double price;
    public String description;
    public ParseUser name;


    //zum testen:

    public Entry(String title){
        this.title = title;
    }



    public Entry(int id, String title, boolean geschenk,Bitmap picture, double price, String description, ParseUser name){
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

//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
//        // später wenns richtig geht muss hier der index iwie mitgeschickt werden oder so bzw die id über des intent hier ankommen..
//        query.whereEqualTo("objectID", name);
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, ParseException e) {
//                nameString = parseObject.getString("username");
//                //ParseUser p = parseObject.get("user");
//            }
//        });
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereEqualTo("gender", "female");
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e == null) {
//                    // The query was successful.
//                } else {
//                    // Something went wrong.
//                }
//            }
//        });



        return name;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
