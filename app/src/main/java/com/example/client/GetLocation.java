package com.example.client;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetLocation {
    //get the longitude and latitude from the file

    private double longitude;
    private double latitude;
    private int id;
    private String nextLine;

    private BufferedReader reader;
    public GetLocation(Context context){
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("data.csv")));
            reader.readLine();
        } catch (IOException e) {
            Log.d("GetLocation", e.getMessage());
        }
    }

    public void getData(){
        try {
            nextLine=reader.readLine();
            while (nextLine!= null) {
                // nextLine[] is an array of values from the line
                String[] row = nextLine.split(",");
                Log.i("check", nextLine);
                longitude = Double.parseDouble(row[0]);
                latitude = Double.parseDouble(row[1]);
                id = Integer.parseInt(row[2]);
                nextLine = reader.readLine();
                break;
            }
        } catch (IOException e) {
            Log.d("GetLocation", e.getMessage());
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getId() {
        return id;
    }
}
