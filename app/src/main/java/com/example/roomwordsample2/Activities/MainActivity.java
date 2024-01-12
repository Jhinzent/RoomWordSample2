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
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity {
    private final int NEW_ROUTE_ACTIVITY_REQUEST_CODE = 5;
    private final int VIEW_ROUTE_ACTIVITY_REQUEST_CODE = 2;
    private final int NEW_POI_ACTIVITY_REQUEST_CODE = 3;
    private final int PICK_IMAGE_REQUEST_CODE = 4;
    private static final int CAMERA_REQUEST_CODE = 6;
    private static final int STORAGE_PERMISSION_CODE = 23;

    private RouteViewModel mRouteViewModel;
    private PoiViewModel mPoiViewModel;
    private PolylineOptions userRouteOptions;
    private List<LatLng> waypoints = new ArrayList<>();
    private Marker selectedMarker;
    private GoogleMap googleMap;
    private int currentRouteId;
    private int currentlyShowingPoiId;
    private String mCurrentPhotoPath; // Instanzvariable für den aktuellen Foto-Pfad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestForStoragePermissions();

        currentRouteId = -1;

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

                // Füge die currentRouteId dem Intent als Extra hinzu
                intent.putExtra("ROUTE_ID", currentRouteId);

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

            // Lade POIs für die Route aus der Datenbank und zeige sie auf der Karte an
            mRouteViewModel.getRouteById(currentRouteId).observe(this, route -> {
                if (route != null) {

                    mPoiViewModel.getPoIsForRoute(currentRouteId).observe(this, pois -> {
                        if (pois != null && !pois.isEmpty()) {
                            for (Poi poi : pois) {
                                // Zeige nur die Pois an die zur gerade ausgewählten Route gehören
                                if(poi.getPoiId() == currentRouteId) {
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

        currentlyShowingPoiId = poi.getPoiId();

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
            //checkCameraPermission();
            openCamera();

        });
        // Set other views in the dialog with the data from the poi object
        titleTextView.setText(poi.ort);
        snippetTextView.setText(poi.beschreibung);

        String imagePath = poi.getFotoPath();

        if(imagePath.contains("content")) {
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
        }
        else {
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    // Erstellen Sie eine Datei-Referenz aus dem imagePath
                    File imageFile = new File(imagePath);

                    // Generieren Sie eine content URI für die Datei mit FileProvider
                    Uri imageUri = FileProvider.getUriForFile(
                            this,
                            "com.example.roomwordsample2.fileprovider",
                            imageFile
                    );

                    // Verwenden Sie die content URI, um das Bild zu laden
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // Falls das Bild nicht gefunden wird, können Sie hier eine Benachrichtigung anzeigen oder ein Platzhalterbild setzen
                } catch (Exception e) {
                    System.out.println("HERE IS THE ERROR:" + e);
                    // Generische Exception-Handling, für alle anderen möglichen Fehler
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_ROUTE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("ROUTE_ID")) {
                int routeId = data.getIntExtra("ROUTE_ID", -1);

                mRouteViewModel.getRouteById(routeId).observe(this, new Observer<Route>() {
                    @Override
                    public void onChanged(Route route) {

                        currentRouteId = routeId;
                        if (route != null) {
                            String gpxFilePath = route.getGpxdatei();
                            loadGPXFileAndDrawPolyline(gpxFilePath);
                        }
                    }
                });
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedFileUri = data.getData();
                String selectedFilePath = selectedFileUri.toString();

                // Hier kannst du die Poi-Instanz aus der Datenbank abrufen und aktualisieren
                mPoiViewModel.updateFotoPath(currentlyShowingPoiId, selectedFilePath);

            }
        }

        // Hinzufügen eines neuen Blocks für die Kameraaktivität
        // Hinzufügen eines neuen Blocks für die Kameraaktivität
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Hier ist die URI des Fotos, das Sie gespeichert haben
            Uri imageUri = Uri.parse(mCurrentPhotoPath);

            // Hier können Sie die Poi-Instanz aus der Datenbank abrufen und aktualisieren
            mPoiViewModel.updateFotoPath(currentlyShowingPoiId, imageUri.toString());
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Erstellen Sie eine Datei, um das Bild zu speichern
            Uri photoURI;
            try {
                photoURI = createImageFileUri();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } catch (IOException ex) {
                // Hier könnte ein Fehler beim Erstellen der Datei behandelt werden
            }
        }
    }

    private Uri createImageFileUri() throws IOException {
        // Erstellen Sie einen Bild-Dateinamen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Speichern Sie einen Dateipfad für die Verwendung mit ACTION_VIEW-Intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return FileProvider.getUriForFile(this,
                "com.example.roomwordsample2.fileprovider",  // Diese Zeile korrigieren
                image);
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
}