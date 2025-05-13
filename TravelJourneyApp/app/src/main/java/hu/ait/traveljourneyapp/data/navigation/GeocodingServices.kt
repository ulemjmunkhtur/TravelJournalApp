package hu.ait.traveljourneyapp.data.navigation

import hu.ait.traveljourneyapp.data.network.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("json")
    suspend fun getCoordinates(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}