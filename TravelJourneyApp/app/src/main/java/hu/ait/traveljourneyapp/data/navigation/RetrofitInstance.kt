package hu.ait.traveljourneyapp.data.navigation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    val api: GeocodingService by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }
}