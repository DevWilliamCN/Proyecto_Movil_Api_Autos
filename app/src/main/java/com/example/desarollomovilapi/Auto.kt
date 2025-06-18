package com.example.desarollomovilapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Auto(
    val marca: String,
    val modelo: String,
    @SerialName("año") val anio: Int,
    val precio: Int,
    @SerialName("rendimiento_km_l") val rendimientoKmL: Double,
    val categoria: String,
    val ranking: Double
)
