package com.uff.compubiapp.service;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.uff.compubiapp.AppConfig;
import com.uff.compubiapp.model.SDDLLocation;

public class ConnectionService extends Service implements LocationListener {

    private NodeConnection connection;
    private ConnectionListener listener;
    private SocketAddress socket;

    private Thread t;
    private volatile Boolean keepRunning = true;
    private volatile Boolean isConnected = false;

    private ArrayList<Message> lstMsg = new ArrayList<Message>();

    /*Message interval = position update*/
    private int msgInterval = 1000 * 30; /*30 seconds*/

    private LocationManager locationManager;
    private SDDLLocation loc;

    private UUID uuid;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        /*instantiate the listener for this connection*/
        listener = new ConnectionListener(getApplicationContext());

        Log.d("SDDL", "<< Service Created >>");
        t = new Thread(new Runnable() {
            public void run() {
                try {
                    /*create a new MR-UDP connection*/
                    connection = new MrUdpNodeConnection(uuid                                                                                                                                                                                                                                                                                                                                                                                   );
                    /*assign the listener to the connection created*/
                    connection.addNodeConnectionListener(listener);
                    /*obtain from the SharedPreferences the IP and PORT*/
                    String ip = AppConfig.getIp(getApplicationContext());
                    int port = AppConfig.getPort(getApplicationContext());
                    /*create the socket and assign the ip and port*/
                    socket = new InetSocketAddress(ip, port);
                    /*assign the socket with the connection*/
                    connection.connect(socket);
                    isConnected = true;
                    /*set the connection flagh on the UI*/
                    AppConfig.setIsConnected(getApplicationContext(), true);
                    /*set the msg interval between updates*/
                    AppConfig.setMsgInterval(getApplicationContext(), msgInterval);
                    while (keepRunning) {
                        /*disconnect and set the thread to null*/
                        if (!isConnected) {
                            keepRunning = false;
                            connection.disconnect();
                            /*set the is connected to false*/
                            AppConfig.setIsConnected(getApplicationContext(), false);
                            stopThread(t);
                        }
                        /*connected send all the msgs on the queue*/
                        if (isConnected) {
                            while (lstMsg.size() > 0) {
                                connection.sendMessage(lstMsg.get(0));
                                Log.d(AppConfig.DEBUG, ">>> Message sent to Gateway");
                                lstMsg.remove(0);
                                //update the message counter
                                AppConfig.addMsgCounter(getApplicationContext());
                            }
                        }
                        synchronized (t) {
                            t.wait();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*start the thread*/
        t.start();

        /*create the UUID and save it*/
        if (AppConfig.getUuid(getApplicationContext()) != null)
            Log.d(AppConfig.DEBUG, "<< Valid set UUID >>");
        else
            Log.d(AppConfig.DEBUG, "<< !!Warning!! UUID is NULL >>");

        /*location code*/
        // Getting LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // force GPS
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, msgInterval, 0, this);
    }

    /*stop the thread*/
    private synchronized void stopThread (Thread t) {
        if (t != null) {
            t = null;
        }
    }

    /*create the Application Message and add to the queue*/
    private void createAndQueueMsg (Serializable s, UUID sender) {
        /*create the application message*/
        ApplicationMessage am = new ApplicationMessage();
        am.setContentObject(s);
        am.setTagList(new ArrayList<String>());
        am.setSenderID(sender);
        /*add to the queue*/
        lstMsg.add(am);
        /*notify thread to send message*/
        synchronized(t) {
            t.notify();
        }
    }

    /*set the flag to disconnect*/
    private void disconnect () {
        isConnected = false;
        locationManager.removeUpdates(this);
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Log.d(AppConfig.DEBUG, "<< onStartCommand >>");
        return START_STICKY; /*if we get killed, restart service*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*when the service is killed, disconnect*/
    @Override
    public void onDestroy() {
        disconnect();
        synchronized(t) {
            t.notify();
        }
        /*set the UI to service not running*/
        AppConfig.setService(getApplicationContext(), false);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(AppConfig.DEBUG, ">>> New location onLocationChanged");
        /*get sender UUID*/
        if (uuid == null) { /*if null create a new one and save it*/
            uuid = AppConfig.generateUuid();
            AppConfig.setUuid(getApplicationContext(), uuid);
        }

        /*Obtain the type of connectivity*/
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        /*create the message*/
        loc = new SDDLLocation();
        loc.setUuid(uuid.toString());
        loc.setLatitude(location.getLatitude());
        loc.setLongitude(location.getLongitude());
        loc.setAccuracy(location.getAccuracy());
        loc.setDatetime(new Date());
        loc.setBearing(location.getBearing());
        loc.setProvider(location.getProvider());
        loc.setSpeed(location.getSpeed());
        loc.setAltitude(location.getAltitude());
        /*set the connection type*/
        for (NetworkInfo ni : netInfo) {
            if (ni.isConnected()) {
                if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
                    loc.setConnectionType("MOBILE :: "+ni.getSubtypeName());
            }
            else
                loc.setConnectionType(ni.getTypeName());
        }

        /* set the battery status */
        IntentFilter battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent iBatt = this.registerReceiver(null, battFilter);
        int level = iBatt.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        float scale = iBatt.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int battLevel = (int) ((level/scale)*100);
        loc.setBatteryPercent(battLevel);

        /* set the battery charging */
        int charging = iBatt.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        loc.setCharging(charging == BatteryManager.BATTERY_STATUS_CHARGING);

        Log.d(AppConfig.DEBUG, loc.toString());

        /*send the message*/
        createAndQueueMsg(loc, uuid);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

}
