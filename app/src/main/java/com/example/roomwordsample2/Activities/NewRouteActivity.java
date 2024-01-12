package com.example.roomwordsample2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
    private final int CHOOSE_GPX_REQUEST_CODE = 1;

    private String gpxFilePath;


    // private EditText mEditWordView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        gpxFilePath = "";

        EditText editBezeichnung = findViewById(R.id.edit_bezeichnung);
        EditText editBeginn = findViewById(R.id.edit_beginn);
        EditText editEnde = findViewById(R.id.edit_ende);
        EditText editDauer = findViewById(R.id.edit_dauer);

        Button chooseGPXButton = findViewById(R.id.button_choose_gpx);
        chooseGPXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*"); // Alle Dateitypen

                // Füge die Berechtigung zum Intent hinzu
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(intent, CHOOSE_GPX_REQUEST_CODE);
            }
        });

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {

            // Get text from EditTexts
            String bezeichnung = editBezeichnung.getText().toString();
            String beginn = editBeginn.getText().toString();
            String ende = editEnde.getText().toString();
            String dauer = editDauer.getText().toString();

            System.out.println("TEEEEST: " + gpxFilePath);

            // Create a Route object with the entered details
            Route newRoute = new Route(bezeichnung, beginn, ende, gpxFilePath, dauer);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_GPX_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedFileUri = data.getData();
                gpxFilePath = selectedFileUri.toString();

            }
        }
    }
}