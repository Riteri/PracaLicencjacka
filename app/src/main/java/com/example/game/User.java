package com.example.game;



public class User {
    public String id, name, password, mail;
    public  String score;
    public String bledneWyniki;
    public String lvl, date, newUser;
    public String sredniCzasOdpowiedzi;


    public User() {
    }

    public User(String id, String name, String password, String mail, String score, String bledneWyniki, String lvl, String date, String newUser,String sredniCzasOdpowiedzi) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.mail = mail;
        this.score = score;
        this.bledneWyniki = bledneWyniki;
        this.lvl = lvl;
        this.date = date;
        this.newUser = newUser;
        this.sredniCzasOdpowiedzi = sredniCzasOdpowiedzi;



    }


}
