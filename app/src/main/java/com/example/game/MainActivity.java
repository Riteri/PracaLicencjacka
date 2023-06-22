package com.example.game;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnLogin = findViewById(R.id.button6);
        hideSystemBars();
        sprawdzenie();





    }
    public void sprawdzianie_internet(){
        
    }


    public void onClickRejestracja(View view) {
        Intent intent = new Intent(this, Rejestracja.class);
        startActivity(intent);
    }

    public void onClickLogowanie(View view) {
        // sprawdzanie czy uzytkownik jest zalogowany
        // jezeli nie to otwórz okno logowania, jzeli tak to przejdz do gry
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, GraWindow.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Logowanie.class);
            startActivity(intent);

        }

    }

    public void openTopUsers(View view) {
        Intent intent = new Intent(this, TopUsers.class);
        startActivity(intent);
    }


    public void openFaq(View view) {
        Intent intent = new Intent(this, Faq.class);
        startActivity(intent);
    }

    public void wyloguj(View view) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Nie jesteś zalogowany", Toast.LENGTH_SHORT).show();


        }


    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }


    // sprawdzenie czy jest zalogowany w osobnym wątku
    public void sprawdzenie() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        btnLogin.setText("graj!");
                        break;
                    } else {
                        btnLogin.setText("zaloguj");
                        break;
                    }
                }

                handler.postDelayed(this, 1);
            }
        }, 1);


    }



}