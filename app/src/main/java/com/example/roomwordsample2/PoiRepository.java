package com.example.roomwordsample2;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoiRepository {

    private PoiDao mPoiDao;
    private LiveData<List<Poi>> mAllPois;

    public PoiRepository(Application application) {
        WanderRouteDatabase db = WanderRouteDatabase.getDatabase(application);
        mPoiDao = db.poiDao();
        mAllPois = mPoiDao.getAlphabetizedWords();
    }

    public LiveData<List<Poi>> getAllPois() {
        return mAllPois;
    }

    public LiveData<List<Poi>> getPoisForRoute(int routeId) {
        return mPoiDao.getPoIsForRoute(routeId);
    }

    public void insertPoi(Poi poi) {
        WanderRouteDatabase.databaseWriteExecutor.execute(() -> {
            mPoiDao.insert(poi);
        });
    }

    // Hier ist die neue Methode zum Aktualisieren des fotoPath-Attributs
    public void updateFotoPath(int poiId, String fotoPath) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            mPoiDao.updateFotoPath(poiId, fotoPath);
        });
    }

    public LiveData<Poi> getPoiById(int poiId) {
        return mPoiDao.getPoiById(poiId);
    }
}