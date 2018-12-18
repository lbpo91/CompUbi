package com.uff.compubiapp.service;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import android.content.Context;
import android.util.Log;

import com.uff.compubiapp.AppConfig;

public class ConnectionListener implements NodeConnectionListener {

    private Context c;

    public ConnectionListener (Context c) {
        this.c = c;
    }

    @Override
    public void connected(NodeConnection nc) {
        ApplicationMessage am = new ApplicationMessage();
        am.setContentObject("ack");
        am.setTagList(new ArrayList<String>());
        am.setSenderID(AppConfig.getUuid(c));
        try {
            nc.sendMessage(am);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SDDL", "Connected and Identified...");
    }

    @Override
    public void disconnected(NodeConnection nc) {
        Log.d("SDDL", "Disconnected...");
    }

    @Override
    public void internalException(NodeConnection nc, Exception e) {
        Log.d("SDDL", "InternalException...");
    }

    @Override
    public void newMessageReceived(NodeConnection nc, Message m) {
        Log.d("SDDL", "NewMessageReceived...");
        //unserialize message
//		String msg = m.getContentObject().toString();
        //send a broadcast message
//		Intent intent = new Intent(ChatActivity.BROADCAST_MSG);
//		intent.putExtra("msg", "Central: "+msg);
//		LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
    }

    @Override
    public void reconnected(NodeConnection nc, SocketAddress s,
                            boolean arg2, boolean arg3) {
        Log.d("SDDL", "Reconnected...");
    }

    @Override
    public void unsentMessages(NodeConnection nc, List<Message> lstMsg) {
        Log.d("SDDL", "UnsetMessages...");
    }

}
