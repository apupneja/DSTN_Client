package com.example.client.mqtt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.client.R
import com.example.client.mqtt.DataClasse.MQTTConnectionParams
import com.example.client.mqtt.Interface.UIUpdaterInterface

class MQTTActivity : AppCompatActivity(), UIUpdaterInterface {

    var mqttManager:MQTTmanager? = null
    lateinit var ipAddressField : EditText
    lateinit var topicField : EditText
    lateinit var messageField : EditText
    lateinit var connectBtn : Button
    lateinit var sendBtn : Button
    lateinit var statusLabl : TextView
    lateinit var messageHistoryView : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mqttactivity)

        ipAddressField = findViewById<EditText>(R.id.ipAddressField)
        topicField = findViewById<EditText>(R.id.topicField)
        messageField = findViewById<EditText>(R.id.messageField)
        connectBtn = findViewById<Button>(R.id.connectBtn)
        sendBtn = findViewById<Button>(R.id.sendBtn)
        statusLabl = findViewById<TextView>(R.id.statusLabl)
        messageHistoryView= findViewById<EditText>(R.id.messageHistoryView)
        // Enable send button and message textfield only after connection
        resetUIWithConnection(false)
    }

    // Interface methods
    override fun resetUIWithConnection(status: Boolean) {
        ipAddressField.isEnabled  = !status
        topicField.isEnabled      = !status
        messageField.isEnabled    = status
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
        if (!(ipAddressField.text.isNullOrEmpty() && topicField.text.isNullOrEmpty())) {
            var host = "tcp://" + ipAddressField.text.toString() + ":1883"
            var topic = topicField.text.toString()
            var connectionParams = MQTTConnectionParams("MQTTSample",host,topic,"","")
            mqttManager = MQTTmanager(connectionParams,applicationContext,this)
            mqttManager?.connect()
        }else{
            updateStatusViewWith("Please enter all valid fields")
        }

    }

    fun sendMessage(view: View){

        mqttManager?.publish(messageField.text.toString())

        messageField.setText("")
    }
}
