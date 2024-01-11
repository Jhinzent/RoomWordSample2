package com.example.roomwordsample2;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "route_table")
public class Route {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "routeId")
    private int routeId;
    public String bezeichnung;
    public String beginn;
    public String ende;
    public String gpxdatei;
    public String dauer;

    public Route(String bezeichnung, String beginn, String ende, String gpxdatei, String dauer) {
        // Initialize the fields here using the passed parameters
        this.bezeichnung = bezeichnung;
        this.beginn = beginn;
        this.ende = ende;
        this.gpxdatei = gpxdatei;
        this.dauer = dauer;
    }

    public Route(@NonNull int route) {this.routeId = route;}

    public void setRouteId(int id) {
        routeId = id;
    }

    public int getRouteId() {
        return this.routeId;
    }

    public String getRouteBezeichnung() { return this.bezeichnung; }

    public String getGpxdatei() { return this.gpxdatei;
    }
}
