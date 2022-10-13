package com.example.client.mqtt.DataClasse

data class MQTTConnectionParams(
    val clientID : String,
    val host : String,
    val topic : String,
    val username : String,
    val password : String
)
