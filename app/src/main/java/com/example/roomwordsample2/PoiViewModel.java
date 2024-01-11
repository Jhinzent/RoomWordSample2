package com.example.roomwordsample2;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PoiViewModel extends AndroidViewModel {
    private PoiRepository poiRepository;
    private LiveData<List<Poi>> allPois;

    public PoiViewModel(Application application) {
        super(application);
        poiRepository = new PoiRepository(application);
        allPois = poiRepository.getAllPois();
    }

    public LiveData<List<Poi>> getAllPois() {
        return allPois;
    }

    public void insertPoi(Poi poi) {
        poiRepository.insertPoi(poi);
    }

    public LiveData<List<Poi>> getPoIsForRoute(int routeId) {
        return poiRepository.getPoisForRoute(routeId);
    }

    public LiveData<Poi> getPoiById(int poiId) {
        return poiRepository.getPoiById(poiId);
    }

    public void updateFotoPath(int poiId, String fotoPath) {
        poiRepository.updateFotoPath(poiId, fotoPath);
    }

    // Weitere Methoden je nach Bedarf
}