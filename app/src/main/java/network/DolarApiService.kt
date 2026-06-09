package com.stefano.dolarapp.network

import com.stefano.dolarapp.model.DolarResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DolarApiService {

    // CORRECCIÓN: El endpoint oficial de la API para el listado completo es v1/dolares
    @GET("v1/dolares")
    suspend fun getCotizaciones(): List<DolarResponse> // Nota: Si te devuelve una lista [], cambialo a List<DolarResponse>

    companion object {
        private const val BASE_URL = "https://dolarapi.com/"

        fun create(): DolarApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DolarApiService::class.java)
        }
    }
}