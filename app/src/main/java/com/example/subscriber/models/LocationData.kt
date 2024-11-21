package com.example.subscriber.models
import com.google.android.gms.maps.model.LatLng

data class LocationData(val id: String, val latitude: Double, val longitude: Double, val timestamp: Int, val speed: Double)