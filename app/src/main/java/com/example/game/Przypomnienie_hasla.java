package com.example.game;

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


public class Przypomnienie_hasla extends AppCompatActivity {
    private EditText username,email;
    private Button btn;
    private DatabaseReference mDatabaseGracz;
    private String USER_KEY = "Gracz";
    private FirebaseAuth Auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przypomnienie_hasla);
        setTitle("Przypomnienie hasła");

        username = findViewById(R.id.editText_username);
        email = findViewById(R.id.editText_email);
        btn = findViewById(R.id.button);

        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);
        Auth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String emai = email.getText().toString();

                if (user.isEmpty() || emai.isEmpty()){
                    // jest puste
                    Toast.makeText(Przypomnienie_hasla.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();

                } else {
                    // wysylmy haslo na mail
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
                Log.d("Przypomnienie_hasla", "user: " + user + " haslo: " + haslo);

                // wysylamy haslo na maila
                wysylamyMaila(email,haslo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    public void wysylamyMaila(String email, String haslo) {

        String toEmail = email;
        String subject = "Przypomnienie hasła w grze matematycznej";
        String body = "Szanowny użytkowniku,\n\nPrzypominamy Ci Twoje hasło do gry matematycznej. Poniżej znajdziesz Twoje dane logowania:\n\nNazwa użytkownika: " + username.getText().toString() + "\nHasło: " + haslo +
                "\n\nJeśli nadal masz problemy z zalogowaniem się do gry, skontaktuj się z naszym zespołem wsparcia technicznego." +
                "\n\nDziękujemy za korzystanie z naszej gry matematycznej." +
                "\n\nPozdrawiamy,\nZespół Gra Matematyczna";

        EmailSender.sendEmail(toEmail, subject, body);

        // wyswietlamy komunikat ze wyslano maila
        Toast.makeText(Przypomnienie_hasla.this, "Wysłano maila", Toast.LENGTH_SHORT).show();


    }

}