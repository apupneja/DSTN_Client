package com.example.client.mqtt.DataClasse

data class MQTTConnectionParams(
    val clientId : String,
    val host : String,
    val topic : String,
    val username : String,
    val password : String
)
