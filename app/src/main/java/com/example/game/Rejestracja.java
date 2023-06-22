package com.example.game;


import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Rejestracja extends AppCompatActivity {

    private EditText username, password, email;
    private Button register;
    private DatabaseReference mDatabaseGracz;
    private String USER_KEY = "Gracz";

    private FirebaseAuth Auth;
    private String newMail;
    private boolean state = false;
    private ImageView eye;
    private ArrayList<String> bledneWyniki = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);
        setTitle("Rejestracja");


        //pobieranie elementów z widoku
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        email = findViewById(R.id.editText_email);
        register = findViewById(R.id.button);
        eye = findViewById(R.id.toggle_view2);

        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);
        Auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String mail = email.getText().toString();

                // sprawdzenie czy wszystkie pola zostały wypełnione
                if (user.isEmpty() || pass.isEmpty() || mail.isEmpty()) {

                    // jeśli nie wypełnione, wyświetl komunikat
                    Toast.makeText(Rejestracja.this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                    Log.d("Rejestracja", "Wypełnij wszystkie pola");
                } else  if (pass.length() < 6) {
                    Toast.makeText(Rejestracja.this, "Hasło musi mieć co najmniej 6 znaków", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(mail, pass);
                    // jeśli wszystkie pola zostały wypełnione
                    // sprawdzanie czy użytkownik o podanym loginie istnieje

                    newMail = mail;
                    // убрать все символы после @
                    newMail = newMail.replaceAll("@.*", "");
                    DatabaseReference userExists = mDatabaseGracz.child(newMail);
                    ValueEventListener userListener = new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {

                                // data i czas rejestracji
                                String date = java.text.DateFormat.getDateTimeInstance().format(java.util.Calendar.getInstance().getTime());


                                // nie istnieje, można rejestrować
                                // dodanie użytkownika do bazy danych
                                String id = mDatabaseGracz.getKey();
                                String wyniki;
                                User gracz = new User(id, user, pass, mail, "0","0", "1", "0", "true","0");

//

                                mDatabaseGracz.child(newMail).setValue(gracz);
                                Toast.makeText(Rejestracja.this, "Rejestracja udana", Toast.LENGTH_SHORT).show();


                            } else {
                                // istnieje, wyświetl komunikat
                                Toast.makeText(Rejestracja.this, "Użytkownik o podanych danych już istnieje", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Rejestracja", "Błąd: " + error.getMessage());
                        }

                    };
                    userExists.addListenerForSingleValueEvent(userListener);


                }

            }


        });




    }


    private void registerUser(String mail, String pass) {
        Auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Rejestracja.this, "Udana rejestracja", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(Rejestracja.this, "Błąd rejestracji", Toast.LENGTH_SHORT).show();
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



}