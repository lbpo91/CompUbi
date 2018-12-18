package com.uff.compubiapp;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;


public class AppConfig {

    /*Debug CONSTANT*/
    public static final String DEBUG = "SDDLDebug";

    /*config filename*/
    public static final String PREF_FILE = "Config";

    /*data saved inside the SharedPreferences*/
    public static final String IP_AND_PORT = "ipAndPort";
    public static final String SERVICE_RUNNING = "ServiceRunning";
    public static final String IS_CONNECTED = "isConnected";
    public static final String MSG_COUNTER = "MsgCounter";
    public static final String MSG_INTERVAL = "MsgInterval";

    /*save the UUID for the application*/
    public static final String USER_UUID = "UserUUID";

    /*default value for the MSG_INTERVAL in miliseconds */
    public static final int DEFAULT_MSG_INTERVAL = 30000;

    /*check if it is a valid IP address*/
    public static Boolean isValidIpPort (String s) {
        if (s.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}"))
            return true;
        return false;
    }

    /*validate interval if is a number*/
    public static Boolean isNumber (String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    /*get the ip from the string "192.168.1.10:5500" */
    public static String getIp (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        String ipAndPort = config.getString(AppConfig.IP_AND_PORT, "");
        String[] result = ipAndPort.split(":");
        return result[0];
    }

    /*get the port from the string "192.168.1.10:5500" */
    public static int getPort (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        String ipAndPort = config.getString(AppConfig.IP_AND_PORT, "");
        String[] result = ipAndPort.split(":");
        return Integer.parseInt(result[1]);
    }

    /*save the ip and port to SharedPreferences*/
    public static void saveIpPort (Context c, String ipAndPort) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor writer = config.edit();
        writer.putString(AppConfig.IP_AND_PORT, ipAndPort);
        writer.commit();
    }

    /*get the ip and port from SharedPreferences*/
    public static String getIpPort (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        String ipAndPort = config.getString(AppConfig.IP_AND_PORT, "");
        return ipAndPort;
    }

    /*get the service status*/
    public static Boolean getService (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        Boolean service = config.getBoolean(AppConfig.SERVICE_RUNNING, false);
        return service;
    }

    /*set the service flag*/
    public static Boolean setService (Context c, Boolean flag) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean(AppConfig.SERVICE_RUNNING, flag);
        return writer.commit();
    }

    /*get connection status*/
    public static Boolean getIsConnected (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        Boolean connected = config.getBoolean(AppConfig.SERVICE_RUNNING, false);
        return connected;
    }

    /*set the is connected flag*/
    public static Boolean setIsConnected (Context c, Boolean flag) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean(AppConfig.IS_CONNECTED, flag);
        return writer.commit();
    }

    /*get the message counter*/
    public static int getMsgCounter (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        int counter = config.getInt(AppConfig.MSG_COUNTER, 0);
        return counter;
    }

    /*add one to the counter*/
    public static void addMsgCounter (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        int counter = config.getInt(AppConfig.MSG_COUNTER, 0);
        SharedPreferences.Editor writer = config.edit();
        writer.putInt(AppConfig.MSG_COUNTER, counter++);
        writer.commit();
    }

    /*reset counter*/
    public static void resetMsgCounter (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor writer = config.edit();
        writer.putInt(AppConfig.MSG_COUNTER, 0);
        writer.commit();
    }

    /*get the msg interval*/
    public static int getMsgInterval (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        int interval = config.getInt(AppConfig.MSG_INTERVAL, AppConfig.DEFAULT_MSG_INTERVAL);
        return interval;
    }

    /*set the msg interval*/
    public static void setMsgInterval (Context c, int miliseconds) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor writer = config.edit();
        writer.putInt(AppConfig.MSG_INTERVAL, miliseconds);
        writer.commit();
    }

    /*generate a new UUID*/
    public static UUID generateUuid () {
        return UUID.randomUUID();
    }

    /*set the UUID for this user*/
    public static Boolean setUuid (Context c, UUID id) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor writer = config.edit();
        writer.putString(AppConfig.USER_UUID, id.toString());
        return writer.commit();
    }

    /*get the UUID from the user, if one is not set, create an id*/
    public static UUID getUuid (Context c) {
        SharedPreferences config = c.getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
        String uuid_String = config.getString(AppConfig.USER_UUID, "");
        if (uuid_String.equals("")) { /*no UUID need to create one*/
            UUID newId = generateUuid();
            if (setUuid (c, newId)) /*it was saved successfully*/
                return newId;
            return null;
        }
        else {
            UUID id = UUID.fromString(uuid_String);
            return id;
        }
    }
}
