package com.example.busbashlog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleListResponse(
    val results: List<Vehicle> = emptyList()
)

@Serializable
data class Vehicle(
    val id: Long? = null,
    val slug: String? = null,
    @SerialName("fleet_code") val fleetCode: String? = null,
    @SerialName("fleet_number") val fleetNumber: Int? = null,
    val reg: String? = null,
    @SerialName("vehicle_type") val vehicleType: VehicleType? = null,
    val livery: Livery? = null,
    val operator: Operator? = null,
    val garage: Garage? = null
) {
    val display: String
        get() = listOfNotNull(fleetCode ?: fleetNumber?.toString(), reg).joinToString(" • ")
}

@Serializable
data class VehicleType(val name: String? = null, val fuel: String? = null, @SerialName("double_decker") val doubleDecker: Boolean? = null)
@Serializable
data class Livery(val name: String? = null)
@Serializable
data class Operator(val id: String? = null, val name: String? = null)
@Serializable
data class Garage(val name: String? = null)

@Serializable
data class LogEntry(
    val id: String,
    val timestamp: Long,
    val fleetCode: String?,
    val reg: String?,
    val operatorName: String?,
    val typeName: String?,
    val notes: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)
