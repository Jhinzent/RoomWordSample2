package com.example.roomwordsample2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Route route);

    @Query("DELETE FROM route_table")
    void deleteAll();

    @Query("SELECT * FROM route_table ORDER BY routeId ASC")
    LiveData<List<Route>> getAlphabetizedWords();

    @Query("SELECT * FROM route_table")
    LiveData<List<Route>> getAllRoutes();

    @Query("SELECT * FROM route_table WHERE routeId = :routeId")
    LiveData<Route> getRouteById(int routeId);

}
