package com.example.desarollomovilapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import android.util.Log

object ApiService {
    private const val BASE_URL = "https://api-william.datapiwilliam.workers.dev/api"

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getAutos(): List<Auto> {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/autos")
            val autos: List<Auto> = response.body()
            Log.d("ApiService", "Autos recibidos: $autos")
            autos
        } catch (e: Exception) {
            Log.e("ApiService", "Error al obtener autos", e)
            emptyList()
        }
    }
}
