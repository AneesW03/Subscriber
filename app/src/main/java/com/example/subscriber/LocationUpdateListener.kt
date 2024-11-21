package com.example.subscriber

import com.example.subscriber.models.LocationData

interface LocationUpdateListener {
    fun onNewLocationReceived(id: String)
}