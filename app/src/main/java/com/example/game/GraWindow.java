package com.example.game;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GraWindow extends AppCompatActivity {

    // zmienne do wyswietlania
    private TextView textViewScore, textViewOperacja, textViewWynik;
    private EditText wpisywanieWyniku;

    // polaczenie z baza danych
    private DatabaseReference mDatabaseGracz;
    private String USER_KEY = "Gracz";

    private DatabaseReference mDatabaseZadania;
    private String USER_KEY_ZADANIA = "Zadaia_dla_nowych_graczy";

    // dane gracza
    public String username;
    public String score;
    public String czyNowy;
    public String iloscBlednych;
    public String sredniCzas;
    public String lvl;

    // zmienne do obliczania
    private String operacja;
    private int  suma;
    private int numerZadania = 0;
    int numerZadaniaZadania = 0;
    private String sprawdzenie_wyniku;

    int licznik_poprawnych_odpowiedzi_pod_rzad = 0;
    int licznik_blednych_odpowiedzi_pod_rzad = 0;




    private String email;
    public  int punkty;
    public int licznik_poprawnych_odpowiedzi;
    public int licznik_blednych_odpowiedzi;

    // zmienne do wyswietlania timera
    private int seconds;
    private boolean running;
    private int czyJestNowy;

    // array list z czasem odpowiedzi
    private ArrayList<Integer> listaCzasow = new ArrayList<Integer>();
    private ArrayList<Integer> jakaOdpowiedz = new ArrayList<Integer>();

    private  ArrayList<String> listaZadan = new ArrayList<>();
    private ArrayList<Float> listaOdpowiedziZad = new ArrayList<>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gra_window);

        // usuniecie status bar
        getSupportActionBar().hide();
        // usuniecie paska zadan
        hideSystemBars();


        textViewWynik = findViewById(R.id.textViewWynik);

        openKeyboard();



        // początek gry
        getNameAndScore();

        // pobieramy z bazy danych informacje czy gracz jest nowy czy nie

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        email = firebaseAuth.getCurrentUser().getEmail();
        // dalej pobieramy dane gracza z bazy
        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);
        email = email.replaceAll("@.*", "");
        DatabaseReference userExists = mDatabaseGracz.child(email);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // zapisujemy dane gracza do zmiennych
                // sprawdzenie czy gracz jest nowy
                czyNowy = dataSnapshot.child("newUser").getValue(String.class);
                if (czyNowy.equals("true")) {
                    czyJestNowy = 1;
                    // wyswietlenie okienka z testem
                    nowyUserTestAlert();
                } else {
                    // wyswietlenie okienka z gra
                    // pokazujemy textViev "znak rownania"
                    TextView textView = findViewById(R.id.textViewZnakRownania);
                    textView.setVisibility(View.VISIBLE);
                    gamePhisics();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        };
        userExists.addListenerForSingleValueEvent(valueEventListener);




        //gamePhisics();

        // dajemy mozliwosc klikniecia na textView zeby zaczac nowa gre
        textViewWynik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textViewWynik.getText().toString().isEmpty()) {
                    Toast.makeText(GraWindow.this, "pole nie może być puste żeby zacząć nową grę!", Toast.LENGTH_SHORT).show();
                } else {
                    textViewWynik.setText("");
                    wpisywanieWyniku.setText("");
                    openKeyboard();

                    // sprawdzamy czy gracz jest nowy czy nie

                    if (czyJestNowy == 1) {
                        // wyswietlenie okienka z testem
                        nowyUserTest();
                    } else {
                        // wyswietlenie okienka z gra
                        gamePhisics();
                    }


                }


            }
        });



    }




    // jezeli użytkownik jest pierszy raz w grze to musi przejsc test dla sprawdzenia poziomu umiejetnosci
    public void nowyUserTestAlert(){

        // wyswietlenie informacji ze uzytkownik jest 1 raz w grze
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Witaj " + username + "!");
        builder.setMessage("Jesteś nowym użytkownikiem, musisz przejść test aby sprawdzić swoje umiejętności");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // zaczynamy test
                nowyUserTest();


            }
        });
        builder.show();


    }

    // ---------------------------------------------------------------------------------------------
    // test dla nowego gracza
    public void nowyUserTest(){

        Log.d("test", "nowyUserTest: ");


        // tworzymy zadania roznyc poziomow trudnosci od 1 do 10 i zapisywujemy je listy
        // lista 1
        ArrayList<String> listaZadan = new ArrayList<String>();
        // lista odpowiedzi na zadania
        ArrayList<String> listaOdpowiedzi = new ArrayList<String>();
        listaZadan.add("2+3"); // latwy
        listaOdpowiedzi.add("5");
        listaZadan.add("9-5"); // latwy
        listaOdpowiedzi.add("4");
        listaZadan.add("4*5"); // sredni
        listaOdpowiedzi.add("20");
        listaZadan.add("20/4"); // sredni
        listaOdpowiedzi.add("5");
        listaZadan.add("Oblicz pole kwadratu o boku 6"); // trudny
        listaOdpowiedzi.add("36");
        listaZadan.add("Oblicz objętość prostopadłościanu o bokach 3, 4 i 5"); // trudny
        listaOdpowiedzi.add("60");
        listaZadan.add("Oblicz wartość wyrażenia 2x^2 + 3x - 4 dla x = 3"); // bardzo trudny
        listaOdpowiedzi.add("23");
        listaZadan.add("Oblicz wartość wyrażenia 2x^2 + 3x - 4 dla x = 5"); // bardzo trudny
        listaOdpowiedzi.add("49");
        listaZadan.add("Oblicz wartość wyrażenia 2x^3 + 4x^2 - 5x + 1 dla x = 2"); // ekstra trudny
        listaOdpowiedzi.add("23");
        listaZadan.add("Oblicz wartość wyrażenia (x^2 + 2x - 3) / (x - 1) dla x = 2"); // ekstra trudny
        listaOdpowiedzi.add("4");

        if (numerZadania<10) {
            // wyswietlenie textViev "znak rownania"
            TextView textView = findViewById(R.id.textViewZnakRownania);
            textView.setVisibility(View.VISIBLE);

            // start timera
            resetTimer();
            runTimer();
            startTimer();



            // wyswietlanie danych
            textViewOperacja = findViewById(R.id.textViewOperacje);
            // jezeli zadanie jest dlugie i nie miesci sie, mniejszymy rozmiar tekstu
            if (listaZadan.get(numerZadania).length() > 10) {
                textViewOperacja.setTextSize(30);
            } else {
                textViewOperacja.setTextSize(50);
            }

            // wyswietlenie zadania
            textViewOperacja.setText(listaZadan.get(numerZadania));




            wpisywanieWyniku = findViewById(R.id.wpisywanieWyniku);
            // otwieranie klawiatury automatycznie
            wpisywanieWyniku.requestFocus();


            // podtwierdzenie wpisanego wyniku przez nacisniecie enter
            wpisywanieWyniku.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        sprawdzenie_wyniku = wpisywanieWyniku.getText().toString();
                        // sprawdzenie czy wpisany wynik jest poprawny
                        stopTimer();
                        if (sprawdzenie_wyniku.equals(String.valueOf(listaOdpowiedzi.get(numerZadania)))) {
                            textViewWynik = findViewById(R.id.textViewWynik);
                            // wyswietlenie wyniku
                            textViewWynik.setText("Poprawny wynik");
                            numerZadania++;
                            listaCzasow.add(seconds);
                            jakaOdpowiedz.add(1) ;
                            // text zielony
                            textViewWynik.setTextColor(getResources().getColor(R.color.green));

                            // licznik poprawnych odpowiedzi
                            licznik_poprawnych_odpowiedzi++;

                        } else {

                            // wyswietlenie wyniku
                            textViewWynik.setText("Nipoprawnie! Poprawny wynik to: " + listaOdpowiedzi.get(numerZadania));
                            // text zielony
                            numerZadania++;
                            listaCzasow.add(seconds);
                            jakaOdpowiedz.add(0);
                            textViewWynik.setTextColor(getResources().getColor(R.color.red));
                            licznik_blednych_odpowiedzi++;


                        }
                    }
                    return false;
                }
            });


        } else if (numerZadania == 10){
            // koniec testu
            numerZadania++;
            zakonczenieTestu();
        }


    }



    // metoda pokazujaca ze test jest zakonczony i wyswietla poziom trudnosci
    public void zakonczenieTestu(){
        int sumaCzasow = 0;

        // ilosc poprawnych odpowiedzi w typie float
        float poprawneOdpowiedzi = licznik_poprawnych_odpowiedzi;
        // sredni czas odpowiedzi w typie float

        for (int i = 0; i < listaCzasow.size(); i++) {
             sumaCzasow += listaCzasow.get(i);
        }

        float sredniCzasOdpowiedzi = sumaCzasow / listaCzasow.size();

        // obliczenie poziomu trudnosci
        float poziomTrudnosciFloat = obliczPoziom(poprawneOdpowiedzi, sredniCzasOdpowiedzi);
        String poziomTrudnosci = String.valueOf(poziomTrudnosciFloat);




        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Koniec testu");
        builder.setMessage("Gratulacje! Twój poziom trudności to: " + poziomTrudnosci + "\n" + "Poprawnych odpowiedzi: " + licznik_poprawnych_odpowiedzi + "\n" + "Niepoprawnych odpowiedzi: " + licznik_blednych_odpowiedzi + "\n" + "Średni czas odpowiedzi: " + sredniCzasOdpowiedzi + "s");
        listaCzasow.clear();
        licznik_poprawnych_odpowiedzi = 0;
        licznik_blednych_odpowiedzi = 0;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // zamykanie okna dialogowego
                dialog.dismiss();

                // dodajemy do bazy info ze uzytkownik juz nie jest nowy
                mDatabaseGracz.child(email).child("newUser").setValue(String.valueOf("false"));
                czyJestNowy = 0;

                // grzechodzimy do gry
                //czyszczenie list z zadaniem i odpowiedzia
                listaZadan.clear();
                listaOdpowiedziZad.clear();
                gamePhisics();
            }
        });
        builder.show();
    }

    // -------------------------------------  KONIEC TESTU ----------------------------------------------


// -------------------------------------  SIEC NEURONOWA ----------------------------------------------
    float obliczPoziom(float poprawneOdpowiedzi, float sredniCzasOdpowiedzi){


        Context context = getApplicationContext(); // Получение контекста приложения
        SiecNeuronowa siecNeuronowa = new SiecNeuronowa(context); // Создание объекта SiecNeuronowa

        String aktualy_poziomString = lvl;
        // string to float
        float aktualy_poziom = Float.parseFloat(aktualy_poziomString);
        float poziomTrudnosciFloat = siecNeuronowa.predict(poprawneOdpowiedzi, sredniCzasOdpowiedzi, aktualy_poziom);


        // zapisujemy do bazy danych
        mDatabaseGracz.child(email).child("lvl").setValue(String.valueOf(poziomTrudnosciFloat));

        return poziomTrudnosciFloat;

    }

// -------------------------------------  KONIEC SIECI NEURONOWEJ ----------------------------------------------
    public void gamePhisics(){


        Log.d("test", "gamePhisics: ");



        // --- tablice z zadaniami dla poziomów trudności 1-10
        // w przyszlosci bedzie to pobierane z bazy danych lub z pliku
        // teraz zadania sa testowe. finalne zadania beda zmienione

        int poziomTrudnosciZadan = Math.round(Float.parseFloat(lvl));

        if (poziomTrudnosciZadan == 1) {
            listaZadan.add("1+1");
            listaOdpowiedziZad.add(2f);
            listaZadan.add("2+3");
            listaOdpowiedziZad.add(5f);
            listaZadan.add("4-2");
            listaOdpowiedziZad.add(2f);
            listaZadan.add("3*2");
            listaOdpowiedziZad.add(6f);
            listaZadan.add("10/2");
            listaOdpowiedziZad.add(5f);
            listaZadan.add("2^3");
            listaOdpowiedziZad.add(8f);
            listaZadan.add("7-5");
            listaOdpowiedziZad.add(2f);
            listaZadan.add("6*3");
            listaOdpowiedziZad.add(18f);
            listaZadan.add("8/4");
            listaOdpowiedziZad.add(2f);
            listaZadan.add("5^2");
            listaOdpowiedziZad.add(25f);
        } if (poziomTrudnosciZadan == 2) {
            listaZadan.add("3+4");
            listaOdpowiedziZad.add(7f);
            listaZadan.add("5+6");
            listaOdpowiedziZad.add(11f);
            listaZadan.add("7-3");
            listaOdpowiedziZad.add(4f);
            listaZadan.add("4*3");
            listaOdpowiedziZad.add(12f);
            listaZadan.add("12/2");
            listaOdpowiedziZad.add(6f);
            listaZadan.add("2^4");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("9-5");
            listaOdpowiedziZad.add(4f);
            listaZadan.add("8*3");
            listaOdpowiedziZad.add(24f);
            listaZadan.add("16/4");
            listaOdpowiedziZad.add(4f);
            listaZadan.add("3^3");
            listaOdpowiedziZad.add(27f);
        } else if (poziomTrudnosciZadan == 3){
            listaZadan.add("3+3");
            listaOdpowiedziZad.add(6f);
            listaZadan.add("3*3");
            listaOdpowiedziZad.add(9f);
            listaZadan.add("3+3");
            listaOdpowiedziZad.add(6f);
            listaZadan.add("3*3");
            listaOdpowiedziZad.add(9f);
            listaZadan.add("3+3");
            listaOdpowiedziZad.add(6f);
            listaZadan.add("3*3");
            listaOdpowiedziZad.add(9f);
            listaZadan.add("3+3");
            listaOdpowiedziZad.add(6f);
            listaZadan.add("3*3");
            listaOdpowiedziZad.add(9f);
            listaZadan.add("3+3");
            listaOdpowiedziZad.add(6f);
            listaZadan.add("3*3");
            listaOdpowiedziZad.add(9f);
        } else if (poziomTrudnosciZadan == 4){
            listaZadan.add("4+4");
            listaOdpowiedziZad.add(8f);
            listaZadan.add("4*4");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("4+4");
            listaOdpowiedziZad.add(8f);
            listaZadan.add("4*4");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("4+4");
            listaOdpowiedziZad.add(8f);
            listaZadan.add("4*4");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("4+4");
            listaOdpowiedziZad.add(8f);
            listaZadan.add("4*4");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("4+4");
            listaOdpowiedziZad.add(8f);
            listaZadan.add("4*4");
            listaOdpowiedziZad.add(16f);
        } else if (poziomTrudnosciZadan == 5){
            listaZadan.add("5+5");
            listaOdpowiedziZad.add(10f);
            listaZadan.add("5*5");
            listaOdpowiedziZad.add(25f);
            listaZadan.add("5+5");
            listaOdpowiedziZad.add(10f);
            listaZadan.add("5*5");
            listaOdpowiedziZad.add(25f);
            listaZadan.add("5+5");
            listaOdpowiedziZad.add(10f);
            listaZadan.add("5*5");
            listaOdpowiedziZad.add(25f);
            listaZadan.add("5+5");
            listaOdpowiedziZad.add(10f);
            listaZadan.add("5*5");
            listaOdpowiedziZad.add(25f);
            listaZadan.add("5+5");
            listaOdpowiedziZad.add(10f);
            listaZadan.add("5*5");
            listaOdpowiedziZad.add(25f);
        } else if (poziomTrudnosciZadan == 6){
            listaZadan.add("6+6");
            listaOdpowiedziZad.add(12f);
            listaZadan.add("6*6");
            listaOdpowiedziZad.add(36f);
            listaZadan.add("6+6");
            listaOdpowiedziZad.add(12f);
            listaZadan.add("6*6");
            listaOdpowiedziZad.add(36f);
            listaZadan.add("6+6");
            listaOdpowiedziZad.add(12f);
            listaZadan.add("6*6");
            listaOdpowiedziZad.add(36f);
            listaZadan.add("6+6");
            listaOdpowiedziZad.add(12f);
            listaZadan.add("6*6");
            listaOdpowiedziZad.add(36f);
            listaZadan.add("6+6");
            listaOdpowiedziZad.add(12f);
            listaZadan.add("6*6");
            listaOdpowiedziZad.add(36f);
        } else if (poziomTrudnosciZadan == 7){
            listaZadan.add("7+7");
            listaOdpowiedziZad.add(14f);
            listaZadan.add("7*7");
            listaOdpowiedziZad.add(49f);
            listaZadan.add("7+7");
            listaOdpowiedziZad.add(14f);
            listaZadan.add("7*7");
            listaOdpowiedziZad.add(49f);
            listaZadan.add("7+7");
            listaOdpowiedziZad.add(14f);
            listaZadan.add("7*7");
            listaOdpowiedziZad.add(49f);
            listaZadan.add("7+7");
            listaOdpowiedziZad.add(14f);
            listaZadan.add("7*7");
            listaOdpowiedziZad.add(49f);
            listaZadan.add("7+7");
            listaOdpowiedziZad.add(14f);
            listaZadan.add("7*7");
            listaOdpowiedziZad.add(49f);
        } else if (poziomTrudnosciZadan == 8){
            listaZadan.add("8+8");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("8*8");
            listaOdpowiedziZad.add(64f);
            listaZadan.add("8+8");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("8*8");
            listaOdpowiedziZad.add(64f);
            listaZadan.add("8+8");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("8*8");
            listaOdpowiedziZad.add(64f);
            listaZadan.add("8+8");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("8*8");
            listaOdpowiedziZad.add(64f);
            listaZadan.add("8+8");
            listaOdpowiedziZad.add(16f);
            listaZadan.add("8*8");
            listaOdpowiedziZad.add(64f);
        } else if (poziomTrudnosciZadan == 9){
            listaZadan.add("9+9");
            listaOdpowiedziZad.add(18f);
            listaZadan.add("9*9");
            listaOdpowiedziZad.add(81f);
            listaZadan.add("9+9");
            listaOdpowiedziZad.add(18f);
            listaZadan.add("9*9");
            listaOdpowiedziZad.add(81f);
            listaZadan.add("9+9");
            listaOdpowiedziZad.add(18f);
            listaZadan.add("9*9");
            listaOdpowiedziZad.add(81f);
            listaZadan.add("9+9");
            listaOdpowiedziZad.add(18f);
            listaZadan.add("9*9");
            listaOdpowiedziZad.add(81f);
            listaZadan.add("9+9");
            listaOdpowiedziZad.add(18f);
            listaZadan.add("9*9");
            listaOdpowiedziZad.add(81f);
        } else if (poziomTrudnosciZadan == 10){
            listaZadan.add("10+10");
            listaOdpowiedziZad.add(20f);
            listaZadan.add("10*10");
            listaOdpowiedziZad.add(100f);
            listaZadan.add("10+10");
            listaOdpowiedziZad.add(20f);
            listaZadan.add("10*10");
            listaOdpowiedziZad.add(100f);
            listaZadan.add("10+10");
            listaOdpowiedziZad.add(20f);
            listaZadan.add("10*10");
            listaOdpowiedziZad.add(100f);
            listaZadan.add("10+10");
            listaOdpowiedziZad.add(20f);
            listaZadan.add("10*10");
            listaOdpowiedziZad.add(100f);
            listaZadan.add("10+10");
            listaOdpowiedziZad.add(20f);
            listaZadan.add("10*10");
            listaOdpowiedziZad.add(100f);
        }



        if (numerZadaniaZadania<10) {

            // wyswietlenie textViev "znak rownania"
            TextView textView = findViewById(R.id.textViewZnakRownania);
            textView.setVisibility(View.VISIBLE);

            // start timera
            resetTimer();
            runTimer();
            startTimer();



            // wyswietlanie danych
            textViewOperacja = findViewById(R.id.textViewOperacje);
            // jezeli zadanie jest dlugie i nie miesci sie, mniejszymy rozmiar tekstu
            if (listaZadan.get(numerZadaniaZadania).length() > 10) {
                textViewOperacja.setTextSize(30);
            } else {
                textViewOperacja.setTextSize(50);
            }

            // wyswietlenie zadania
            textViewOperacja.setText(listaZadan.get(numerZadaniaZadania));




            wpisywanieWyniku = findViewById(R.id.wpisywanieWyniku);
            // otwieranie klawiatury automatycznie
            wpisywanieWyniku.requestFocus();


            // podtwierdzenie wpisanego wyniku przez nacisniecie enter
            wpisywanieWyniku.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        sprawdzenie_wyniku = wpisywanieWyniku.getText().toString();
                        // sprawdzamy czy nie ma kropki w wyniku jezeli nie ma to dodajemy .0 jezeli jest to nic nie robimy
                        if (!sprawdzenie_wyniku.contains(".")) {
                            sprawdzenie_wyniku = sprawdzenie_wyniku + ".0";
                        }
                        // sprawdzenie czy wpisany wynik jest poprawny
                        stopTimer();
                        if (sprawdzenie_wyniku.equals(String.valueOf(listaOdpowiedziZad.get(numerZadaniaZadania)))) {
                            textViewWynik = findViewById(R.id.textViewWynik);
                            // wyswietlenie wyniku
                            textViewWynik.setText("Poprawny wynik");
                            numerZadaniaZadania++;
                            listaCzasow.add(seconds);
                            jakaOdpowiedz.add(1) ;
                            // text zielony
                            textViewWynik.setTextColor(getResources().getColor(R.color.green));

                            // licznik poprawnych odpowiedzi
                            licznik_poprawnych_odpowiedzi++;
                            licznik_poprawnych_odpowiedzi_pod_rzad++;
                            licznik_blednych_odpowiedzi_pod_rzad = 0;
                            dodawajPunkty(seconds);
                            achivmentsIloscPorawnych(licznik_poprawnych_odpowiedzi_pod_rzad);
                            achivmentsCzas(seconds);

                            //czyszczenie list z zadaniem i odpowiedzia
                            listaZadan.clear();
                            listaOdpowiedziZad.clear();

                        } else {

                            // wyswietlenie wyniku
                            textViewWynik.setText("Nipoprawnie! Poprawny wynik to: " + listaOdpowiedziZad.get(numerZadaniaZadania));
                            // text zielony
                            numerZadaniaZadania++;
                            listaCzasow.add(seconds);
                            jakaOdpowiedz.add(0);
                            textViewWynik.setTextColor(getResources().getColor(R.color.red));
                            licznik_blednych_odpowiedzi++;
                            licznik_blednych_odpowiedzi_pod_rzad ++;
                            odejmowaniePunktow();

                            //czyszczenie list z zadaniem i odpowiedzia
                            listaZadan.clear();
                            listaOdpowiedziZad.clear();




                        }
                    }
                    return false;
                }
            });


        } else if (numerZadaniaZadania == 10){
            numerZadaniaZadania = 0;
            //czyszczenie list z zadaniem i odpowiedzia
            listaZadan.clear();
            listaOdpowiedziZad.clear();
            zakonczenieTestu();

        }


        }





    // funkcja dla ponownego generowania danych, oraz czyszczenia wpisanego wyniku
    public void nowaGra() {
        wpisywanieWyniku.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    // rozpoczecie nowej gry
                    textViewWynik.setText("");
                    // czysczenie edittext
                    wpisywanieWyniku.setText("");

                    // generowanie nowych danych
                    //czyszczenie list z zadaniem i odpowiedzia
                    listaZadan.clear();
                    listaOdpowiedziZad.clear();
                    gamePhisics();

                }
                return false;
            }
        });


    }

    // metoda pobierająca klucz zalogowanego gracza, dalej zwraca się do bazy i pobiera username oraz punkty
    public void getNameAndScore() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // dostajemy z bazy zalogowanego uzytkownika
        email = firebaseAuth.getCurrentUser().getEmail();

        // dalej pobieramy dane gracza z bazy
        mDatabaseGracz = FirebaseDatabase.getInstance().getReference(USER_KEY);

        // usuwamy znaki po @
        email = email.replaceAll("@.*", "");

        // szukamy w bazie danych gracza o podanym emailu
        DatabaseReference userExists = mDatabaseGracz.child(email);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // zapisujemy dane gracza do zmiennych
                username = dataSnapshot.child("name").getValue(String.class);
                score = dataSnapshot.child("score").getValue(String.class);
                lvl = dataSnapshot.child("lvl").getValue(String.class);
//                // wyswietlenie ilosci punktow
                textViewScore = findViewById(R.id.textViewPoints);
                textViewScore.setText(username + ": " + score);



                // pobieramy ilosc blednych wynikow na danym poziomie
                iloscBlednych = dataSnapshot.child("bledneWyniki").getValue(String.class);
                // pobieramy sredni czas na danym poziomie
                sredniCzas = dataSnapshot.child("sredniCzasOdpowiedzi").getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        };
        userExists.addListenerForSingleValueEvent(valueEventListener);

    }


    public void dodawajPunkty(int time) {
        // dodawanie punktow

        // zapisujemy do czasowej zmiennej ilosc punktow
        punkty = Integer.parseInt(score);

        // sprawdzanie za ile czasu odpowiedz została wpisana
        if (time >= 0 && time <= 10) {
            punkty += 10;
        } else if (time > 10 && time <= 20) {
            punkty += 5;
        } else if (time > 20 && time <= 30) {
            punkty += 2;
        } else {
            punkty += 1;
        }

        // zapis do bazy danych ilosc punktow
        mDatabaseGracz.child(email).child("score").setValue(String.valueOf(punkty));


        // wyswietlenie ilosci punktow
        textViewScore.setText(username + ": " + punkty);
        // ponowne generowanie danych
        // i zwracanie sie do bazy o nowa ilosc pkt (wiem ze mozna po prostu przechowywać ilosc pkt, ale nie chcem)
        getNameAndScore();
        nowaGra();


    }

    public void odejmowaniePunktow() {
        // odejmowanie punktow
        punkty = Integer.parseInt(score);
        punkty -= 5;
        // zapis do bazy
        mDatabaseGracz.child(email).child("score").setValue(String.valueOf(punkty));
        // zapis do bazy danych ilosci blednych wynikow
        mDatabaseGracz.child(email).child("bledneWyniki").setValue(String.valueOf(iloscBlednych));


        // wyswietlenie ilosci punktow
        textViewScore.setText(username + ": " + punkty);
        // ponowne generowanie danych
        getNameAndScore();
        //koniecGry(punkty);
        nowaGra();
    }


    public void startTimer() {
        // start timera
        running = true;

    }

    public void stopTimer() {
        // stop timera
        running = false;
    }

    public void resetTimer() {
        // reset timera
        running = false;
        seconds = 00;
    }




    // metoda do wyswietlenia sekund
    public void runTimer() {
        // uruchomienie licznika
        final TextView textView = findViewById(R.id.timer);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secon = seconds % 60;

                // dajemy format do wyswietlenia
                String time = String.format("%02d:%02d:%02d", hours, minutes, secon);
                textView.setText(time);
                if (running) {
                    // jesli timer jest uruchomiony
                    seconds++;
                    handler.postDelayed(this, 1000);
                }
            }

        });

    }


    public void  achivmentsIloscPorawnych(int poprawne) {
        // za ilość poprawnych odpowiedzi pod rząd (10) dodawać 20 punktów
        // za ilość poprawnych odpowiedzi pod rząd (20) dodawać 40 punktów
        // za ilość poprawnych odpowiedzi pod rząd (30) dodawać 60 punktów
        // za ilość poprawnych odpowiedzi pod rząd (40) dodawać 80 punktów
        // za ilość poprawnych odpowiedzi pod rząd (50) dodawać 100 punktów

        if (poprawne == 10) {
            punkty = Integer.parseInt(score);
            punkty += 20;
            Toast.makeText(this, "Dodano 20 punktów za 10 poprawnych odpowiedzi pod rząd", Toast.LENGTH_SHORT).show();
        }
        if (poprawne == 20) {
            punkty = Integer.parseInt(score);
            punkty += 40;
            Toast.makeText(this, "Dodano 40 punktów za 20 poprawnych odpowiedzi pod rząd", Toast.LENGTH_SHORT).show();
        }
        if (poprawne == 30) {
            punkty = Integer.parseInt(score);
            punkty += 60;
            Toast.makeText(this, "Dodano 60 punktów za 30 poprawnych odpowiedzi pod rząd", Toast.LENGTH_SHORT).show();
        }
        if (poprawne == 40) {
            punkty = Integer.parseInt(score);
            punkty += 80;
            Toast.makeText(this, "Dodano 80 punktów za 40 poprawnych odpowiedzi pod rząd", Toast.LENGTH_SHORT).show();
        }
        if (poprawne >= 50) {
            punkty = Integer.parseInt(score);
            punkty += 100;
            Toast.makeText(this, "Dodano 100 punktów za 50 i więcej poprawnych odpowiedzi pod rząd", Toast.LENGTH_SHORT).show();
        }

        // zapis do bazy
        mDatabaseGracz.child(email).child("score").setValue(String.valueOf(punkty));




    }

    public void achivmentsCzas(int time) {
        // za czas mniejszy od 5 sekund dodawać 10 punktów

        if (time < 5) {
            punkty = Integer.parseInt(score);
            punkty += 5;
            Toast.makeText(this, "Dodano 5 punktów za mniejszy czas", Toast.LENGTH_SHORT).show();
            mDatabaseGracz.child(email).child("score").setValue(String.valueOf(punkty));
        }

    }
    // metoda do ukrycia pasku
    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // konfig zachowania ukrytych pasków systemowych
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Ukryj zarówno pasek stanu, jak i pasek nawigacyjny
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    public void openKeyboard() {
        // otwieranie klawiatury
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }





}
