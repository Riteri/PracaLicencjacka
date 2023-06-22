package com.example.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;

import java.util.List;


public class TopUsers extends AppCompatActivity {

    private DatabaseReference mDatabaseGracz;
    private String USER_KEY = "Gracz";

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    // licznik wyświetlanych wyników
    private int  licznik = 0;
    Dialog myDialog;


    // lista wyników
    List<String> lista = new ArrayList<>();
    List<String> listaNiePosortowana = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_users);
        // usuwanie title bar
        getSupportActionBar().hide();

        listView = findViewById(R.id.listViewTopUsers);
        lista = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listView.setAdapter(arrayAdapter);

        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);


        //getData();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // sprawdzenie czy lista jest pusta
                // jeśli nie, usuń wszystkie elementy
                if (lista.size()>0) lista.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    // sprawdzamy czy użytkownik nie jest pusty
                    assert user != null;
                    // dodanie do listy wszystkich użytkowników
                    licznik +=1;


                    // dodawanie do listy nazw i wyników użytkowników
                    listaNiePosortowana.add( user.name + ": " + user.score);




                }
                // odświeżenie listy
                arrayAdapter.notifyDataSetChanged();


                // sortowanie listy wyników wg wyników użytkowników (od najwyższego) i nazw użytkowników
                Collections.sort(listaNiePosortowana, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        String[] split1 = o1.split(": ");
                        String[] split2 = o2.split(": ");
                        return Integer.parseInt(split2[1]) - Integer.parseInt(split1[1]);
                    }
                });


                // dodawanie do listy numerów porządkowych
                for (int i = 0; i < listaNiePosortowana.size(); i++) {
                    lista.add(i, i+1 + ". " + listaNiePosortowana.get(i));
                }
                // zatrzymanie animacji
                ladowanieStop();

            }

            // jeśli wystąpi błąd
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // wyświetlenie błędu
                Log.e("TAG", "onCancelled", error.toException());
            }


        };

        mDatabaseGracz.addValueEventListener(valueEventListener);






        // obsluga klikniecia na element listy
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(TopUsers.this);
            builder.setTitle("Informacje o użytkowniku");
            builder.setMessage( "Nazwa użytkownika: " + item.substring(3, item.indexOf(":")) + "\n" + "Wynik: " + item.substring(item.indexOf(":") + 2)+ "\n" );
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();

        });







    }

    private void getData() {
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // sprawdzenie czy lista jest pusta
//                // jeśli nie, usuń wszystkie elementy
//                if (lista.size()>0) lista.clear();
//
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    User user = ds.getValue(User.class);
//                    // sprawdzamy czy użytkownik nie jest pusty
//                    assert user != null;
//                    // dodanie do listy wszystkich użytkowników
//                    licznik +=1;
//
//
//                    // dodawanie do listy nazw i wyników użytkowników
//                    listaNiePosortowana.add( user.name + ": " + user.score);
//
//
//
//
//                }
//                // odświeżenie listy
//                arrayAdapter.notifyDataSetChanged();
//
//
//                // sortowanie listy wyników wg wyników użytkowników (od najwyższego) i nazw użytkowników
//                Collections.sort(listaNiePosortowana, new Comparator<String>() {
//                    @Override
//                    public int compare(String o1, String o2) {
//                        String[] split1 = o1.split(": ");
//                        String[] split2 = o2.split(": ");
//                        return Integer.parseInt(split2[1]) - Integer.parseInt(split1[1]);
//                    }
//                });
//
//
//                // dodawanie do listy numerów porządkowych
//                for (int i = 0; i < listaNiePosortowana.size(); i++) {
//                    lista.add(i, i+1 + ". " + listaNiePosortowana.get(i));
//                }
//                // zatrzymanie animacji
//                ladowanieStop();
//
//            }
//
//            // jeśli wystąpi błąd
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // wyświetlenie błędu
//                Log.e("TAG", "onCancelled", error.toException());
//            }
//
//
//        };
//
//        mDatabaseGracz.addValueEventListener(valueEventListener);

    }

    // funkcja zatrzymująca animację
    public void ladowanieStop(){
        ProgressBar progressBar = findViewById(R.id.progressBar10);
        progressBar.setVisibility(ProgressBar.INVISIBLE);



    }











}