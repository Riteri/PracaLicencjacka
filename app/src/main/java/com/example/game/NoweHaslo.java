package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NoweHaslo extends AppCompatActivity {
    private boolean state1 = false;
    private boolean state2 = false;

    private EditText haslo1,haslo2, kodWpisany;
    private Button btn_nowe_haslo;
    private DatabaseReference mDatabaseGracz;
    private String USER_KEY = "Gracz";
    private FirebaseAuth Auth;
    ImageView eye1;
    ImageView eye2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowe_haslo);
        setTitle("Zmiana hasła");

        haslo1 = findViewById(R.id.new_password1);
        haslo2 = findViewById(R.id.new_password2);
        kodWpisany = findViewById(R.id.kod_wpisany);
        btn_nowe_haslo = findViewById(R.id.button_new_password);

        eye1 = findViewById(R.id.toggle_view1);
        eye2 = findViewById(R.id.toggle_view2);

        // odbieramy kod z poprzedniego activity
        String kod_wyslany = getIntent().getStringExtra("kod");
        String email = getIntent().getStringExtra("email");

        // sprawdzamy czy wzyskie pola sa wypelnione

        btn_nowe_haslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sprawdzenie(kod_wyslany, email);
            }
        });


    }


    public void sprawdzenie (String kod_wyslany, String email){
        String haslo1_wpisane = haslo1.getText().toString();
        String haslo2_wpisane = haslo2.getText().toString();
        String kod_wpisany = kodWpisany.getText().toString();

        if (haslo1_wpisane.isEmpty() || haslo2_wpisane.isEmpty() || kod_wpisany.isEmpty()){
            // jest puste
            Toast.makeText(NoweHaslo.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
        } else {
            // sprawdzamy czy hasla sa takie same
            if (haslo1_wpisane.equals(haslo2_wpisane)){
                // hasla sa takie same
                // sprawdzamy kod który wyslalismy na email
                // jesli sie zgadza to zmieniamy haslo
                // jesli nie to wyswietlamy komunikat ze kod jest zly
                if (kod_wpisany.equals(kod_wyslany)) {
                    // sprawdzamy czy haslo jest dluzsze niz 6 znakow
                    if (haslo1_wpisane.length() < 6){
                        Toast.makeText(NoweHaslo.this, "Hasło musi mieć conajmniej 6 znaków", Toast.LENGTH_SHORT).show();
                    } else {
                        // haslo jest dluzsze niz 6 znakow
                        // zmieniamy haslo
                        Toast.makeText(NoweHaslo.this, "Hasło zostało zmienione", Toast.LENGTH_SHORT).show();
                        zmienHaslo_w_bazie(haslo1_wpisane, email);
                    }
                } else {
                    // kod jest zly
                    Toast.makeText(NoweHaslo.this, "Kod jest zły", Toast.LENGTH_SHORT).show();
                }

            } else {
                // hasla nie sa takie
                Toast.makeText(NoweHaslo.this, "Hasła nie są takie same", Toast.LENGTH_SHORT).show();
            }

        }

    }

    // zmieniamy haslo w bazie danych
    public void zmienHaslo_w_bazie (String haslo, String email){
        // pobieramy email z poprzedniego activity
        // usuwamy znali po @
        email = email.replaceAll("@.*", "");
        // zmieniamy haslo w bazie danych nazwa uzytkownika to email bez znaku @
        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);
        mDatabaseGracz.child(email).child("password").setValue(haslo);
    }


    public void show_pass1(View view){
        if(!state1){
            haslo1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            haslo1.setSelection(haslo1.getText().length());
            eye1.setImageResource(R.drawable.eye);

        }
        else{
            haslo1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            haslo1.setSelection(haslo1.getText().length());
            eye1.setImageResource(R.drawable.eye_off);
        }
        state1 = !state1;
    }

    public void show_pass2(View view){
        if(!state2){
            haslo2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            haslo2.setSelection(haslo2.getText().length());
            eye2.setImageResource(R.drawable.eye);

        }
        else{
            haslo2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            haslo2.setSelection(haslo2.getText().length());
            eye2.setImageResource(R.drawable.eye_off);
        }
        state2 = !state2;
    }
}