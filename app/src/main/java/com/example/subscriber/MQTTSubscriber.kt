package com.example.subscriber

import android.content.Context
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class MQTTSubscriber(context: Context, updater: LocationUpdateListener) {
    private val update: LocationUpdateListener = updater
    private val mqttClient: Mqtt5AsyncClient = MqttClient.builder()
        .useMqttVersion5()
        .identifier("815000000")
        .serverHost("broker.sundaebytestt.com")
        .serverPort(1883)
        .buildAsync()

    private val dbHelper = DatabaseHelper(context, null)

    init {
        connectAndSubscribe()
    }

    private fun connectAndSubscribe() {
        mqttClient.connect().whenComplete { _, throwable ->
            if (throwable != null) {
                println("Failed to connect to MQTT broker: ${throwable.message}")
            } else {
                println("Connected to MQTT broker!")

                mqttClient.subscribeWith()
                    .topicFilter("assignment/location")
                    .callback { publish: Mqtt5Publish ->
                        val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                        val json = JSONObject(message)
                        dbHelper.createLocation(json.getDouble("latitude"), json.getDouble("longitude"), json.getString("id"), json.getInt("timestamp"), json.getDouble("speed"))
                        update.onNewLocationReceived(json.getString("id"))
                    }
                    .send()
                    .whenComplete { _, subThrowable ->
                        if (subThrowable != null) {
                            println("Failed to subscribe: ${subThrowable.message}")
                        } else {
                            println("Subscribed successfully to topic: assignment/location")
                        }
                    }
            }
        }
    }
}