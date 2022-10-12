package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.stetho.websocket.SimpleEndpoint;
import com.facebook.stetho.websocket.SimpleSession;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String ADDRESS = "msg-test";
    private LocalWebSocketServer mServer;
    private MessageEndpoint mEndpoint;

    private Button send_location, send_location_simulator;
    private TextView current_status;
    private MessageView mMessageView;

    private int pressed = 0, pressedSimulator = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send_location = findViewById(R.id.send_location);
        current_status = findViewById(R.id.current_status);
        mMessageView = findViewById(R.id.messageTv);
        //send_location_simulator = findViewById(R.id.send_location_simulator);

        startServer();

        send_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pressed == 0){
                    pressed = 1;
                    current_status.setText("Sending location...");
                    send_location.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    GetLocation getLocation = new GetLocation(MainActivity.this);

                    for(int i=0;i<100;i++){
                        getLocation.getData();
                        Log.d("Location",Integer.toString(getLocation.getId())+" "+Double.toString(getLocation.getLatitude())+" "+Double.toString(getLocation.getLongitude()));

                        if (mEndpoint != null) {
                            mEndpoint.broadcast(Integer.toString(getLocation.getId())+" "+Double.toString(getLocation.getLatitude())+" "+Double.toString(getLocation.getLongitude()));
                        }
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
    private void startServer() {
        mServer = LocalWebSocketServer.createAndStart(this, ADDRESS, mEndpoint = new MessageEndpoint());
        mMessageView.appendSystemMessage(getString(R.string.msg_server_started, ADDRESS));
    }

    @Override
    protected void onDestroy() {
        mServer.stop();
        super.onDestroy();
    }

    /**
     * A simple message server endpoint
     *
     * Be aware of that the endpoint's callbacks will be called on a non-ui thread.
     * So you should not do ui-operations directly in these callbacks.
     */
    public class MessageEndpoint implements SimpleEndpoint {
        private ArrayList<SimpleSession> sessions = new ArrayList<>();


        void broadcast(final String message) {
            for (SimpleSession session : sessions) {
                session.sendText(message);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageView.appendServerMessage(message);
                }
            });
        }

        @Override
        public void onOpen(SimpleSession session) {
            sessions.add(session);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageView.appendSystemMessage(getString(R.string.msg_client_connected));
                }
            });
        }

        @Override
        public void onMessage(SimpleSession session, final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageView.appendClientMessage(message);
                }
            });
        }

        @Override
        public void onMessage(SimpleSession session, byte[] message, int messageLen) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageView.appendSystemMessage(getString(R.string.msg_client_ignored));
                }
            });
        }

        @Override
        public void onClose(SimpleSession session, int closeReasonCode, String closeReasonPhrase) {
            sessions.remove(session);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageView.appendSystemMessage(getString(R.string.msg_client_disconnected));
                }
            });
        }

        @Override
        public void onError(SimpleSession session, Throwable t) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessageView.appendSystemMessage(getString(R.string.msg_client_error));
                }
            });
        }
    }

}

