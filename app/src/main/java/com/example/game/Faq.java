package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Faq extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        // usuwanie tytułu
        getSupportActionBar().hide();

    }

}