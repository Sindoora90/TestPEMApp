package com.example.testpemapp.app;

public class Person {

    String id;
    String name;
    String telnr;
    String email;
    String adresse;


    // Konstruktor nur fuer FriendActivity zum finden der freunde
    public Person(String name, String telnr) {
        this.name = name;
        this.telnr = telnr;
    }

    public Person(String id, String name, String telnr, String email, String adresse) {
        this.id = id;
        this.name = name;
        this.telnr = telnr;
        this.email = email;
        this.adresse = adresse;
    }


    // getter und setter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTelnr() {
        return telnr;
    }

    public String getEmail() {
        return email;
    }

    public String getAdresse() {
        return adresse;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelnr(String telnr) {
        this.telnr = telnr;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}
