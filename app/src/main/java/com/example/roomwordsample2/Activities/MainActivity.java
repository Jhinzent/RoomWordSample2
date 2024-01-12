package com.example.roomwordsample2.Activities;// Füge deine Imports hinzu

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.roomwordsample2.Poi;
import com.example.roomwordsample2.PoiViewModel;
import com.example.roomwordsample2.R;
import com.example.roomwordsample2.Route;
import com.example.roomwordsample2.RouteViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private final int NEW_ROUTE_ACTIVITY_REQUEST_CODE = 5;
    private final int VIEW_ROUTE_ACTIVITY_REQUEST_CODE = 2;
    private final int NEW_POI_ACTIVITY_REQUEST_CODE = 3;
    private final int PICK_IMAGE_REQUEST_CODE = 4;
    private static final int CAMERA_REQUEST_CODE = 6;
    private static final int STORAGE_PERMISSION_CODE = 23;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    private RouteViewModel mRouteViewModel;
    private PoiViewModel mPoiViewModel;
    private PolylineOptions userRouteOptions;
    private List<LatLng> waypoints = new ArrayList<>();
    private Marker selectedMarker;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestForStoragePermissions();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(gMap -> {
            googleMap = gMap;

            // Zeichne die Polyline auf der Karte
            if (userRouteOptions != null && !waypoints.isEmpty() && googleMap != null) {
                googleMap.addPolyline(userRouteOptions);

                // Setze die Kameraansicht auf den ersten Punkt der Polyline
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waypoints.get(0), 10f));
            }

            // Setze den OnInfoWindowClickListener hier, nachdem die Karte geladen wurde
            if (googleMap != null) {
                googleMap.setOnInfoWindowClickListener(marker -> {
                    openImageChooser();
                    // Hier kannst du weitere Aktionen für den Klick auf das Popup durchführen
                });
            }
        });


        Button createRouteButton = findViewById(R.id.create_route);
        createRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewRouteActivity.class);
                startActivityForResult(intent, NEW_ROUTE_ACTIVITY_REQUEST_CODE);
            }
        });

        Button viewRouteButton = findViewById(R.id.open_route);
        viewRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewRouteActivity.class);
                startActivityForResult(intent, VIEW_ROUTE_ACTIVITY_REQUEST_CODE);
            }
        });

        Button createPoiButton = findViewById(R.id.create_poi);
        createPoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPoiActivity.class);
                startActivityForResult(intent, NEW_POI_ACTIVITY_REQUEST_CODE);
            }
        });

        mRouteViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        mPoiViewModel = new ViewModelProvider(this).get(PoiViewModel.class);
    }

    private void loadGPXFileAndDrawPolyline(String gpxFileName) {

        Uri fileUri = Uri.parse(gpxFileName);
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, fileUri);

        // Check for READ_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
            return;
        }

        try {
            // Open file using openFileDescriptor
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(documentFile.getUri(), "r");
            if (pfd == null) {
                // Handle the case where opening the file descriptor fails
                return;
            }



            FileDescriptor fd = pfd.getFileDescriptor();
            InputStream inputStream = new FileInputStream(fd);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            List<LatLng> points = new ArrayList<>();
            boolean isInTrkpt = false;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("<trkpt")) {
                    isInTrkpt = true;

                    int latStartIndex = line.indexOf("lat=") + 5;
                    int latEndIndex = line.indexOf("\"", latStartIndex);
                    int lonStartIndex = line.indexOf("lon=") + 5;
                    int lonEndIndex = line.indexOf("\"", lonStartIndex);

                    double lat = Double.parseDouble(line.substring(latStartIndex, latEndIndex));
                    double lon = Double.parseDouble(line.substring(lonStartIndex, lonEndIndex));

                    points.add(new LatLng(lat, lon));
                } else if (line.contains("</trkpt>")) {
                    isInTrkpt = false;
                } else if (isInTrkpt && line.contains("<ele>")) {
                    int eleStartIndex = line.indexOf("<ele>") + 5;
                    int eleEndIndex = line.indexOf("</ele>");

                    // Optional: Parse and use elevation if needed
                    // double elevation = Double.parseDouble(line.substring(eleStartIndex, eleEndIndex));

                    // If elevation is needed, modify the LatLng creation accordingly:
                    // points.add(new LatLng(lat, lon, elevation));
                }
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            // Zeichne die Polyline auf der Karte
            drawPolylineOnMap(points);

            int routeId = 1;

            // Lade POIs für die Route aus der Datenbank und zeige sie auf der Karte an
            mRouteViewModel.getRouteById(routeId).observe(this, route -> {
                if (route != null) {

                    mPoiViewModel.getPoIsForRoute(routeId).observe(this, pois -> {
                        if (pois != null && !pois.isEmpty()) {
                            for (Poi poi : pois) {
                                String poiCoordinatesString = poi.getCoordinates();

                                String[] parts = poiCoordinatesString.split(", ");

                                double latitude = Double.parseDouble(parts[0]);
                                double longitude = Double.parseDouble(parts[1]);

                                LatLng poiCoordinates = new LatLng(latitude, longitude);

                                if (poiCoordinates != null) {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(poiCoordinates)
                                            .title(poi.ort) // Setze den Titel des POIs
                                            .snippet(poi.beschreibung); // Setze eine Beschreibung des POIs

                                    // Füge den Marker zur Karte hinzu
                                    Marker marker = googleMap.addMarker(markerOptions);

                                    marker.setTag(poi); // Assuming 'poi' is your POI object

                                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            Poi selectedPoi = (Poi) marker.getTag();
                                            showDialogWithButtons(selectedPoi);
                                            return true; // Return true to indicate that the click event has been handled
                                        }
                                    });

                                    // Benutzerdefiniertes InfoWindow-Layout setzen
                                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                        @Override
                                        public View getInfoWindow(Marker marker) {
                                            // Hier wird das Standard-InfoWindow nicht verwendet, also wird null zurückgegeben.
                                            return null;
                                        }

                                        @Override
                                        public View getInfoContents(Marker marker) {

                                            return null;
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void showDialogWithButtons(Poi poi) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.poi_item); // You need to create this layout

        Button chooseImageButton = dialog.findViewById(R.id.chooseImageButton);
        Button makeImageButton = dialog.findViewById(R.id.open_Camera_Button);
        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        TextView snippetTextView = dialog.findViewById(R.id.snippetTextView);
        ImageView imageView = dialog.findViewById(R.id.imageView);

        chooseImageButton.setOnClickListener(view -> {
            openImageChooser();
            dialog.dismiss();
        });

        makeImageButton.setOnClickListener(view -> {
            checkCameraPermission();

        });
        // Set other views in the dialog with the data from the poi object
        titleTextView.setText(poi.ort);
        snippetTextView.setText(poi.beschreibung);

        String imagePath = poi.getFotoPath();
        System.out.println("TEST" + imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imagePath);
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                System.out.println("HERE IS THE ERROR:" + e);
                // Generische Exception-Handling, für alle anderen möglichen Fehler
            }
        }
        dialog.show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Alle Dateitypen

        // Füge die Berechtigung zum Intent hinzu
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_ROUTE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("ROUTE_ID")) {
                int routeId = data.getIntExtra("ROUTE_ID", -1);

                mRouteViewModel.getRouteById(routeId).observe(this, new Observer<Route>() {
                    @Override
                    public void onChanged(Route route) {
                        if (route != null) {
                            gpxFilePath = route.getGpxdatei();
                            loadGPXFileAndDrawPolyline(gpxFilePath);
                        }
                    }
                });
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();

                // Take persistable URI permission so you can access the file later
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);

                // Now you can use the URI to access the file
                // Set the image in your ImageView or save the URI to use later
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Das Foto wurde erfolgreich aufgenommen
            if (data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // Speichere das Foto und aktualisiere den Datenbankpfad
                // savePhotoAndUpdateDatabase(photo);
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedFileUri = data.getData();
                String selectedFilePath = selectedFileUri.toString();

                int poiId = 1;

                // Hier kannst du die Poi-Instanz aus der Datenbank abrufen und aktualisieren
                mPoiViewModel.updateFotoPath(poiId, selectedFilePath);

            }
        }
    }

    private void drawPolylineOnMap(List<LatLng> points) {
        if (points != null && !points.isEmpty() && googleMap != null) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.BLUE);
            polylineOptions.width(5f);

            for (LatLng latLng : points) {
                polylineOptions.add(latLng);
            }

            // Füge die Polyline zur Karte hinzu
            googleMap.addPolyline(polylineOptions);

            // Setze die Kameraansicht auf den ersten Punkt der Polyline oder Mittelpunkt der Strecke
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 15f));
        }
    }

    private void requestForStoragePermissions() {
        // Check if the app has storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            // Permission already granted
            // You can perform any actions that require storage access here
        }
    }

    private void savePhotoAndUpdateDatabase(Bitmap photo) {
        // Hier kannst du das Foto in einem Dateispeicher oder in der Datenbank speichern
        // Beispiel: Speichere das Bild im internen Dateispeicher der App
        String photoPath = savePhotoToInternalStorage(photo);

        // Aktualisiere den Datenbankpfad
        int poiId = 1;  // Passe dies entsprechend deinem Szenario an
        mPoiViewModel.updateFotoPath(poiId, photoPath);

        // Lade das aktualisierte Bild in dein Image View
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(photo);
    }

    private String savePhotoToInternalStorage(Bitmap photo) {
        // Hier implementiere die Logik zum Speichern des Bildes im internen Speicher
        // und gib den Dateipfad zurück
        // Beispiel: Speichere das Bild im internen Cache-Verzeichnis
        File cacheDir = getCacheDir();
        File photoFile = new File(cacheDir, "photo.jpg");

        try (FileOutputStream fos = new FileOutputStream(photoFile)) {
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return Uri.fromFile(photoFile).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

        private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            openCamera();
        }
    }

    private void openCamera() {
        // Intent to open the camera app
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

        @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted
                openCamera();
            } else {
                // Camera permission denied
                // Handle accordingly (e.g., show a message to the user)
            }
        }
    }


}