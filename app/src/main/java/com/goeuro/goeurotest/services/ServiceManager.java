package com.goeuro.goeurotest.services;

import com.goeuro.goeurotest.dto.Place;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by AhmedSalem on 7/25/15.
 */
public class ServiceManager {

    GoEuroService service;

    public static ServiceManager instance = null;

    private ServiceManager() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.goeuro.com/api/v2")
                .build();

        service = restAdapter.create(GoEuroService.class);
    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    public List<Place> getSuggestedPlaces(String locale, String term) {
        return service.listSuggestedPlaces(locale, term);
    }
}
