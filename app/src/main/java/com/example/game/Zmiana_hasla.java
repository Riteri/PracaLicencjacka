package com.example.game;

import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Zmiana_hasla extends AppCompatActivity {

    private EditText username_zmiana,email_zmiana;
    private Button btn_zmiana;
    private DatabaseReference mDatabaseGracz;
    private String USER_KEY = "Gracz";
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zmiana_hasla);
        setTitle("Zmiana hasła");


        username_zmiana = findViewById(R.id.editText_username_zmiana);
        email_zmiana = findViewById(R.id.editText_email_zmiana);
        btn_zmiana = findViewById(R.id.button_zmiana);

        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);
        Auth = FirebaseAuth.getInstance();

        btn_zmiana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username_zmiana.getText().toString();
                String emai = email_zmiana.getText().toString();

                if (user.isEmpty() || emai.isEmpty()){
                    // jest puste
                    Toast.makeText(Zmiana_hasla.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();

                } else {
                    // pobieramy haslo
                    pobieranieHasla(user,emai);

                }
            }
        });
    }

    public  void pobieranieHasla(String username, String email_adress){
        // pobieramy dane z bazy danych
        String email = email_adress.replaceAll("@.*", "");


        mDatabaseGracz.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user = snapshot.child("name").getValue().toString();
                String haslo = snapshot.child("password").getValue().toString();
                String email = snapshot.child("mail").getValue().toString();
                Log.d("Zmiana hasła", "user: " + user + " haslo: " + haslo);

                // wysylamy na mail wygenerowany kod
                wysylamyMaila(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void wysylamyMaila(String email) {

        // generujemy 8 znakowy kod
        int kod = (int) (Math.random() * 100000000);
        // zmieniamy kod na string
        String kod_string = String.valueOf(kod);

        String toEmail = email;
        String subject = "Zmiana hasła w grze matematycznej";
        String body = "Szanowny użytkowniku," +
                "\n\nWysyłamy Ci kod do zmiany hasła w grze matematycznej. Poniżej znajdziesz kod :" +
                "\n\nKod: " + kod_string  +
                "\n\nJeśli nadal masz problemy ze zmianą hasła, skontaktuj się z naszym zespołem wsparcia technicznego." +
                "\n\nDziękujemy za korzystanie z naszej gry matematycznej." +
                "\n\nPozdrawiamy,\nZespół Gra Matematyczna";

        EmailSender.sendEmail(toEmail, subject, body);

        // wyswietlamy komunikat ze wyslano maila
        Toast.makeText(Zmiana_hasla.this, "Wysłano maila", Toast.LENGTH_SHORT).show();

        // otwieramy nowe okno z wpisaniem kodu i nowym haslem
        Intent intent = new Intent(Zmiana_hasla.this, NoweHaslo.class);
        intent.putExtra("kod", kod_string);
        intent.putExtra("email", email);
        startActivity(intent);


    }

}