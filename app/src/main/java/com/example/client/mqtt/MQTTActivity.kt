package com.example.client.mqtt

import android.os.Build
import android.os.Bundle
import android.util.Log
import kotlin.random.Random
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.birjuvachhani.locus.Locus
import com.example.client.GetLocation
import com.example.client.MessageEndpoint
import com.example.client.R
import com.example.client.mqtt.DataClasse.MQTTConnectionParams
import com.example.client.mqtt.Interface.UIUpdaterInterface
import java.time.Instant
import java.util.UUID


class MQTTActivity : AppCompatActivity(), UIUpdaterInterface {

    var mqttManager:MQTTmanager? = null
    lateinit var connectBtn : Button
    lateinit var sendBtn : Button
    var currentLocation = ""
    var lat = 0.0
    var long = 0.0
    lateinit var statusLabl : TextView
    lateinit var messageHistoryView : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mqttactivity)

        connectBtn = findViewById<Button>(R.id.connectBtn)
        sendBtn = findViewById<Button>(R.id.sendBtn)
        statusLabl = findViewById<TextView>(R.id.statusLabl)
        messageHistoryView= findViewById<EditText>(R.id.messageHistoryView)
        // Enable send button and message textfield only after connection
        resetUIWithConnection(false)
        // starts continous location updates
        Locus.startLocationUpdates(this) { result ->
            result.location?.let {
                lat = it.latitude
                long = it.longitude
            }
            result.error?.let { /* Received error! */ }
        }
    }

    // Interface methods
    override fun resetUIWithConnection(status: Boolean) {
        connectBtn.isEnabled      = !status
        sendBtn.isEnabled         = status

        // Update the status label.
        if (status){
            updateStatusViewWith("Connected")
        }else{
            updateStatusViewWith("Disconnected")
        }
    }

    override fun updateStatusViewWith(status: String) {
        statusLabl.text = status
    }

    override fun update(message: String) {

        var text = messageHistoryView.text.toString()
        var newText = """
            $text
            $message
            """
        //var newText = text.toString() + "\n" + message +  "\n"
        messageHistoryView.setText(newText)
        messageHistoryView.setSelection(messageHistoryView.text.length)
    }



    fun connect(view: View){
        var host = "tcp://" + "broker.hivemq.com" + ":1883"
//            var topic = topicField.text.toString()
            var topic = "GeoFence_DSTN_Project1"
            var connectionParams = MQTTConnectionParams("MQTTSample",host,topic,"","")
            mqttManager = MQTTmanager(connectionParams,applicationContext,this)
            mqttManager?.connect()


    }


//    {
//        "type": "record",
//        "namespace": "com.geofen",
//        "name": "geofen",
//        "fields": [
//        {"name": "timestamp", "type": "long"},
//        {"name": "device_id", "type": "string"},
//        {"name": "latitude", "type": "double"},
//        {"name": "longitude", "type": "double"},
//        {"name": "altitude", "type": "double"},
//        ]
//    }




    fun sendMessage(view: View) {
        for (i in 1..10){
            currentLocation = (lat * Random.nextInt(0,10)).toString()+ " " + (long * Random.nextInt(0,10)).toString() + " " + ((long + lat)/2 * Random.nextInt(0,10)).toString()
            var message = System.currentTimeMillis().toString()+ " " + UUID.randomUUID().toString() + currentLocation
            mqttManager?.publish(message = message)
        }

    }




}


