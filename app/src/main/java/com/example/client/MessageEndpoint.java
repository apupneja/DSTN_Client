package com.example.client;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.facebook.stetho.websocket.SimpleEndpoint;
import com.facebook.stetho.websocket.SimpleSession;

import java.util.ArrayList;

public class MessageEndpoint implements SimpleEndpoint {
    private ArrayList<SimpleSession> sessions = new ArrayList<>();
    private Context context;
    private MessageView mMessageView;
    public MessageEndpoint(Context context) {
        this.context = context;
        this.mMessageView = ((Activity)context).findViewById(R.id.messageTv);
    }

    public void broadcast(final String message) {
        for (SimpleSession session : sessions) {
            session.sendText(message);
        }


        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    mMessageView.appendServerMessage(message);
            }
        });
    }

    @Override
    public void onOpen(SimpleSession session) {
        sessions.add(session);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageView.appendSystemMessage(Resources.getSystem().getString(R.string.msg_client_connected));
            }
        });
    }

    @Override
    public void onMessage(SimpleSession session, final String message) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageView.appendClientMessage(message);
            }
        });
    }

    @Override
    public void onMessage(SimpleSession session, byte[] message, int messageLen) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageView.appendSystemMessage(Resources.getSystem().getString(R.string.msg_client_ignored));
            }
        });
    }

    @Override
    public void onClose(SimpleSession session, int closeReasonCode, String closeReasonPhrase) {
        sessions.remove(session);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageView.appendSystemMessage(Resources.getSystem().getString(R.string.msg_client_disconnected));
            }
        });
    }

    @Override
    public void onError(SimpleSession session, Throwable t) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageView.appendSystemMessage(Resources.getSystem().getString(R.string.msg_client_error));
            }
        });
    }
}