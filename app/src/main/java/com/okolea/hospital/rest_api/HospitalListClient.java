package com.okolea.hospital.rest_api;


import com.okolea.hospital.models.DetailResult;
import com.okolea.hospital.models.DistanceResult;
import com.okolea.hospital.models.PlaceList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Mayank Gupta on 11-12-2017.
 */

public interface HospitalListClient {

    @GET("place/nearbysearch/json")
    Call<PlaceList> getNearbyHospitals(@QueryMap Map<String, String> params);

    @GET("distancematrix/json")
    Call<DistanceResult> getHospitalDistances(@QueryMap Map<String, String> params);

    @GET("place/details/json")
    Call<DetailResult> getHospitalDetails(@QueryMap Map<String, String> params);
}
