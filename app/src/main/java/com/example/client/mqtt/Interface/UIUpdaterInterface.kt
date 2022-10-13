package com.example.client.mqtt.Interface

interface UIUpdaterInterface {
        fun resetUIWithConnection(status: Boolean)
        fun updateStatusViewWith(status: String)
        fun update(message: String)
}