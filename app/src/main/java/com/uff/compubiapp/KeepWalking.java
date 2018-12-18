package com.uff.compubiapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class KeepWalking extends Service {
	
	public static String TAG = "SDDL_LONG_RUNNING_SERVICE";
	
	private Handler handler;
	
	private Timer updateTimer;
	
	private void showMsgOnScreen () {
		handler.post(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(KeepWalking.this, "Alive...", Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}
	
	@Override
	public void onCreate() {
		handler = new Handler();
		updateTimer = new Timer("sddlServiceUpdate");
		updateTimer.scheduleAtFixedRate(doRefresh, 0, 5000);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Context c = getApplicationContext();
		Toast.makeText(c, "SDDL Long Running Service: Started", Toast.LENGTH_SHORT).show();
		// If we get killed, after returning from here, restart
	    return START_STICKY;
	}
	
	private TimerTask doRefresh = new TimerTask() {
		public void run() {
			showMsgOnScreen();
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		updateTimer.cancel();
		Context c = getApplicationContext();
		Toast.makeText(c, "SDDL Long Running Service: Stopped", Toast.LENGTH_SHORT).show();
	}
}