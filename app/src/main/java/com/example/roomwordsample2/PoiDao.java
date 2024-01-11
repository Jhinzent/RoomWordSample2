package com.example.roomwordsample2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface PoiDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Poi poi);

    @Query("DELETE FROM poi_table")
    void deleteAll();

    @Query("SELECT * FROM poi_table ORDER BY poi ASC")
    LiveData<List<Poi>> getAlphabetizedWords();

    @Query("SELECT * FROM poi_table WHERE routeOwnerId = :routeId")
    LiveData<List<Poi>> getPoisForRoute(int routeId);

    @Query("SELECT * FROM poi_table WHERE routeOwnerId = :routeId")
    LiveData<List<Poi>> getPoIsForRoute(int routeId);

    @Query("UPDATE poi_table SET fotoPath = :fotoPath WHERE poi = :poiId")
    void updateFotoPath(int poiId, String fotoPath);

    @Query("SELECT * FROM poi_table WHERE poi = :poiId")
    LiveData<Poi> getPoiById(int poiId);
}


