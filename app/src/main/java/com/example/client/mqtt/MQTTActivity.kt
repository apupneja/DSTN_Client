package com.example.client.mqtt

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.birjuvachhani.locus.Locus
import com.example.client.GetLocation
import com.example.client.MessageEndpoint
import com.example.client.R
import com.example.client.mqtt.DataClasse.MQTTConnectionParams
import com.example.client.mqtt.Interface.UIUpdaterInterface

class MQTTActivity : AppCompatActivity(), UIUpdaterInterface {

    var mqttManager:MQTTmanager? = null
    lateinit var connectBtn : Button
    lateinit var sendBtn : Button
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
        // starts continuos location updates
        Locus.startLocationUpdates(this) { result ->
            result.location?.let {

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

    fun sendMessage(view: View) {
        val getLocation = GetLocation(this)
        val mEndpoint = MessageEndpoint(this)
        getLocation.getData()
        for (i in 0..99) {
            getLocation.getData()
            if (mEndpoint != null) {
                mqttManager?.publish(Integer.toString(getLocation.id) + " " + java.lang.Double.toString(getLocation.latitude) + " " + java.lang.Double.toString(getLocation.longitude))
            }
        }
    }


}
