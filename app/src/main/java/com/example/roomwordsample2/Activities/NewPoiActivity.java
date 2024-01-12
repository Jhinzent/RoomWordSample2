package com.example.roomwordsample2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.roomwordsample2.Poi;
import com.example.roomwordsample2.PoiDao;
import com.example.roomwordsample2.R;
import com.example.roomwordsample2.WanderRouteDatabase;

public class NewPoiActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    public static final int PICK_IMAGE_REQUEST_CODE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poi);

        // Empfange die übergebene currentRouteId
        Intent intent = getIntent();
        int routeOwnerId = intent.getIntExtra("ROUTE_ID", -1);

        // Jetzt kannst du die currentRouteId in der NewPoiActivity verwenden

        EditText editOrt = findViewById(R.id.edit_ort);
        EditText editKoordinaten = findViewById(R.id.edit_koordinaten);
        EditText editBeschreibung = findViewById(R.id.edit_beschreibung);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            // Get text from EditTexts
            String ort = editOrt.getText().toString();
            String koordinaten = editKoordinaten.getText().toString();
            String beschreibung = editBeschreibung.getText().toString();
            String foto = "";

            Poi newPoi = new Poi(ort, koordinaten, beschreibung, foto, routeOwnerId);

            // Insert the new Route object into the database
            insertPoi(newPoi);

            finish();
        });
    }

    private void insertPoi(Poi poi) {
        new Thread(() -> {
            WanderRouteDatabase database = WanderRouteDatabase.getDatabase(getApplicationContext());
            PoiDao poiDao = database.poiDao();

            poiDao.insert(poi);

            // Hier kannst du Code einfügen, der nach dem Einfügen der Route ausgeführt werden soll, falls benötigt

            runOnUiThread(() -> {
                // Hier kannst du Code einfügen, der nach dem Einfügen der Route die UI aktualisiert, falls benötigt
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                String imagePath = selectedImageUri.toString();

                // Hier kannst du den Pfad der ausgewählten Bilddatei verwenden oder speichern
                // Zum Beispiel: imageUri wird hier imagePath zugewiesen, du kannst diesen Pfad speichern und verwenden
            }
        }
    }
    public void goBack(View view) {
        onBackPressed(); // This will perform the default "Back" action
    }
}