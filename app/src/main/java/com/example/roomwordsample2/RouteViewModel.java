package com.example.roomwordsample2;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RouteViewModel extends AndroidViewModel {
    private RouteRepository routeRepository;
    private LiveData<List<Route>> allRoutes;

    public RouteViewModel(Application application) {
        super(application);
        routeRepository = new RouteRepository(application);
        allRoutes = routeRepository.getAllRoutes();
    }

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }

    public void insert(Route route) { routeRepository.insert(route); }

    public LiveData<Route> getRouteById(int routeId) {
        return routeRepository.getRouteById(routeId);
    }
}

