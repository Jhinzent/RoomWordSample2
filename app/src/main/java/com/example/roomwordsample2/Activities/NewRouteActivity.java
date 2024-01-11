package com.example.roomwordsample2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.roomwordsample2.R;
import com.example.roomwordsample2.Route;
import com.example.roomwordsample2.RouteDao;
import com.example.roomwordsample2.WanderRouteDatabase;

public class NewRouteActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    private final int VIEW_POI_ACTIVITY_REQUEST_CODE = 1;


    // private EditText mEditWordView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        EditText editBezeichnung = findViewById(R.id.edit_bezeichnung);
        EditText editBeginn = findViewById(R.id.edit_beginn);
        EditText editEnde = findViewById(R.id.edit_ende);
        EditText editDauer = findViewById(R.id.edit_dauer);

        // mEditWordView = findViewById(R.id.edit_word);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {

            // Get text from EditTexts
            String bezeichnung = editBezeichnung.getText().toString();
            String beginn = editBeginn.getText().toString();
            String gpxDatei = "0";
            String ende = editEnde.getText().toString();
            String dauer = editDauer.getText().toString();

            // Create a Route object with the entered details
            Route newRoute = new Route(bezeichnung, beginn, ende, gpxDatei, dauer);

            // Insert the new Route object into the database
            insertRoute(newRoute);

            finish();
        });
    }

    private void insertRoute(Route route) {
        new Thread(() -> {
            WanderRouteDatabase database = WanderRouteDatabase.getDatabase(getApplicationContext());
            RouteDao routeDao = database.routeDao();

            routeDao.insert(route);

            // Hier kannst du Code einfügen, der nach dem Einfügen der Route ausgeführt werden soll, falls benötigt

            runOnUiThread(() -> {
                // Hier kannst du Code einfügen, der nach dem Einfügen der Route die UI aktualisiert, falls benötigt
            });
        }).start();
    }

    public void goBack(View view) {
        onBackPressed(); // This will perform the default "Back" action
    }
}