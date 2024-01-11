package com.example.roomwordsample2;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

@Entity(tableName = "poi_table")
public class Poi {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "poi")
    private int poiId;

    public String ort;
    public String koordinaten;
    public String beschreibung;
    public String fotoPath; // Pfad zum im Dateisystem gespeicherten Foto
    public int routeOwnerId;

    public Poi(String ort, String koordinaten, String beschreibung, String fotoPath, int routeOwnerId) {
        // Initialize the fields here using the passed parameters
        this.ort = ort;
        this.koordinaten = koordinaten;
        this.beschreibung = beschreibung;
        this.fotoPath = fotoPath;
        this.routeOwnerId = routeOwnerId;
    }

    public void setPoiId(int id) {
        poiId = id;
    }

    public int getPoiId() {
        return this.poiId;
    }

    public String getOrt() { return this.ort; }

    public String getCoordinates() {
        return koordinaten;
    }

    public void setCoordinates(String coordinates) {
        this.koordinaten = coordinates;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public String getFotoPath() { return this.fotoPath;
    }
}