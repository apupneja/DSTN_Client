package com.example.client.currentLocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.birjuvachhani.locus.Locus
import com.example.client.R
import org.w3c.dom.Text

class LocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        val tv = findViewById<TextView>(R.id.tv11)
        Locus.startLocationUpdates(this, onResult = {
            Log.d("uv",it.location.toString())
        })
    }

}