package dev.harshdalwadi.nasaapp.api

import dev.harshdalwadi.nasaapp.api.URLFactory.EP_FETCH_NASA_DATA
import dev.harshdalwadi.nasaapp.models.RespNasaDataItem
import retrofit2.http.GET

interface ApiServices {

    @GET(EP_FETCH_NASA_DATA)
    suspend fun fetchNasaData(): List<RespNasaDataItem>
}
