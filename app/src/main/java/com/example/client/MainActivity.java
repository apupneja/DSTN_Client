package com.example.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.stetho.websocket.SimpleEndpoint;
import com.facebook.stetho.websocket.SimpleSession;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String ADDRESS = "msg-test";
    private LocalWebSocketServer mServer;
    private MessageEndpoint mEndpoint;

    private Button send_location, send_location_simulator;
    private TextView current_status;
    private MessageView mMessageView;

    private static final String UUID_STRING = "00000000-0000-0000-0000-00000000ABCD";

    private int pressed = 0, pressedSimulator = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send_location = findViewById(R.id.send_location);
        current_status = findViewById(R.id.current_status);
        mMessageView = findViewById(R.id.messageTv);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        //startServer();

        send_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pressed == 0) {
                    pressed = 1;
                    current_status.setText("Sending location...");
                    send_location.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    GetLocation getLocation = new GetLocation(MainActivity.this);

                    if (mEndpoint != null) {
                        for (int i = 0; i < 100; i++) {
                            getLocation.getData();
                            Log.d("Location", Integer.toString(getLocation.getId()) + " " + Double.toString(getLocation.getLatitude()) + " " + Double.toString(getLocation.getLongitude()));

                            if (mEndpoint != null) {
                                mEndpoint.broadcast(Integer.toString(getLocation.getId()) + " " + Double.toString(getLocation.getLatitude()) + " " + Double.toString(getLocation.getLongitude()));
                            }
                        }
                    } else if (adapter.isEnabled()) {
                        for (int i = 0; i < 100; i++) {
                            getLocation.getData();
                            Log.d("Location", Integer.toString(getLocation.getId()) + " " + Double.toString(getLocation.getLatitude()) + " " + Double.toString(getLocation.getLongitude()));

                            new SendMessageToServer().execute(Integer.toString(getLocation.getId()) + " " + Double.toString(getLocation.getLatitude()) + " " + Double.toString(getLocation.getLongitude()));
                        }
                    }

                } else if (pressed == 1) {
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

    private class SendMessageToServer extends AsyncTask<String, Void, String> {
        String TAG = "BLUETOOTH";

        @Override
        protected String doInBackground(String... msg) {
            Log.d(TAG, "doInBackground");
            BluetoothSocket clientSocket = null;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            mBluetoothAdapter.enable();

            // Client knows the server MAC address
            BluetoothDevice mmDevice = mBluetoothAdapter.getRemoteDevice("00:e1:8c:3c:ea:fa");
            Log.d(TAG, "got hold of remote device");
            try {
                // UUID string same used by server
                clientSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID
                        .fromString(UUID_STRING));

                Log.d(TAG, "bluetooth socket created");

                mBluetoothAdapter.cancelDiscovery(); 	// Cancel, discovery slows connection

                clientSocket.connect();
                Log.d(TAG, "connected to server");

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                out.writeUTF(msg[0]); 			// Send message to server
                Log.d(TAG, "Message Successfully sent to server");
                return in.readUTF();            // Read response from server
            } catch (Exception e) {

                Log.d(TAG, "Error creating bluetooth socket");
                Log.d(TAG, e.getMessage());
                return "";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute");
        }

    }

}

