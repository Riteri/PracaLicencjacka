package com.example.game;


import android.content.Intent;

import android.os.Bundle;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Logowanie extends AppCompatActivity {

    private EditText username, password;
    private Button btnLogin;
    private DatabaseReference mDatabaseGracz;
    private String USER_KEY = "Gracz";
    // stringy do przechowywania danych z bazy
    public String usernameGetDataBase = "", passwordGetDataBase = "";

    private FirebaseAuth Auth;
    private boolean state = false;
    ImageView eye;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
        setTitle("Logowanie");


        //pobieranie elementów z widoku
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        btnLogin = findViewById(R.id.button);
        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);
        Auth = FirebaseAuth.getInstance();
        eye = findViewById(R.id.toggle_view1);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pobieranie danych z pola tekstowego
                String user = username.getText().toString();
                String pass = password.getText().toString();

                // sprawdzenie czy wszystkie pola zostały wypełnione
                if(user.isEmpty() || pass.isEmpty()){
                    // jeśli nie wypełnione, wyświetl komunikat
                    Toast.makeText(Logowanie.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();

                }else{
                    loginUser(user, pass);



                }


            }
        });


    }



    public  void openGame() {
        Intent intent = new Intent(this, GraWindow.class);
        startActivity(intent);

    }

    private void loginUser(String user, String pass) {
        // sprawdzamy czy login i hasło są poprawne
        Auth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // jeśli tak, przekierowujemy do gry
                    openGame();
                }
                else {
                    // jeśli nie, wyświetl komunikat
                    Toast.makeText(Logowanie.this, "Nieprawidłowy login lub hasło", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void show_pass(View view){
        if(!state){
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setSelection(password.getText().length());
            eye.setImageResource(R.drawable.eye);
        }
        else{
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setSelection(password.getText().length());
            eye.setImageResource(R.drawable.eye_off);
        }
        state = !state;
    }

    public void open_rejestration(View view){
        Intent intent = new Intent(this,Rejestracja.class);
        startActivity(intent);
    }


    public void open_przypomnienie(View view){
        Intent intent = new Intent(this, Przypomnienie_hasla.class);
        startActivity(intent);

    }

    public void open_zmiana_hasla(View view){
        Intent intent = new Intent(this, Zmiana_hasla.class);
        startActivity(intent);

    }





}