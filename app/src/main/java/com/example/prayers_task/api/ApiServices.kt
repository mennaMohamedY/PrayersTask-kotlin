package com.example.prayers_task.api

import com.example.prayers_task.model.PrayersResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    @GET("v1/calendar/{year}/{month}")
    fun getAllMonthPrayers(@Path("year") yearr: Number, @Path("month") monthh: Number, @Query("latitude") latitudee: Double,
                           @Query("longitude") longitudee: Double
    ):Call<PrayersResponse>

}