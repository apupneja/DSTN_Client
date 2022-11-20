package com.example.client

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.birjuvachhani.locus.Locus
import com.example.client.mqtt.MQTTActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val internetBtn = findViewById<CardView>(R.id.connect_through_internet_cv)
        val usbBtn = findViewById<CardView>(R.id.connect_through_usb_cv)
        val locTv= findViewById<TextView>(R.id.location_tv)
        internetBtn.setOnClickListener {
            startActivity(Intent(this,MQTTActivity::class.java))
        }
        usbBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        // starts continuos location updates
        Locus.startLocationUpdates(this) { result ->
            result.location?.let {
                locTv.text = it.latitude.toString() + "  " + it.longitude.toString() }
            result.error?.let { /* Received error! */ }
        }

    }
}