package com.example.android.popularmovies.main.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stuartwhitcombe on 26/08/16.
 */
public class MovieReleaseDates {
    Map<String, Map<Integer, String>> relDateMap = new HashMap<String, Map<Integer, String>>();

    public void add(String country, Integer type, String relDate) {
        Map<Integer, String> typeMap = relDateMap.get(country);
        if(typeMap == null) {
            typeMap = new HashMap<Integer, String>();
            relDateMap.put(country, typeMap);
        }
        typeMap.put(type, relDate);
    }

    public String getRelDate(String country, int type) {
        Map<Integer, String> typeMap = relDateMap.get(country);
        if(typeMap == null)
            return null;
        else
            return typeMap.get(type);
    }
}
