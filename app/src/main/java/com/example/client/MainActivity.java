package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button send_location, send_location_simulator;
    private TextView current_status;

    private int pressed = 0, pressedSimulator = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send_location = findViewById(R.id.send_location);
        current_status = findViewById(R.id.current_status);
        //send_location_simulator = findViewById(R.id.send_location_simulator);

        send_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pressed == 0){
                    pressed = 1;
                    current_status.setText("Sending location...");
                    send_location.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    GetLocation getLocation = new GetLocation(MainActivity.this);

                    for(int i=0;i<10;i++){
                        getLocation.getData();
                        Log.d("Location",Integer.toString(getLocation.getId())+" "+Double.toString(getLocation.getLatitude())+" "+Double.toString(getLocation.getLongitude()));
                    }
                }
                else if(pressed == 1){
                    pressed = 0;
                    current_status.setText("Stopped sending location");
                    send_location.setBackgroundColor(getResources().getColor(R.color.purple_200));
                }
            }
        });


    }
}