package com.goeuro.goeurotest.services;

import com.goeuro.goeurotest.dto.Place;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

public interface GoEuroService {
    @GET("/position/suggest/{locale}/{term}")
    List<Place> listSuggestedPlaces(@Path("locale") String locale, @Path("term") String term);
}