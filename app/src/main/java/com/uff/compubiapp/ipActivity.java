package com.uff.compubiapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ipActivity extends Activity {
	
	TextView txtOldIp;
	
	Button btnValidate;
	Button btnBack;
	
	EditText ipAddress;
	
	Intent iMain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_ip);
		
		txtOldIp = (TextView) findViewById(R.id.txtOldIp);
		btnValidate = (Button) findViewById(R.id.btnValidate);
		btnBack = (Button) findViewById(R.id.btnBack);
		ipAddress = (EditText) findViewById(R.id.ipAddress);
		
		btnValidate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String ipAndPort = ipAddress.getText().toString();
				if (AppConfig.isValidIpPort(ipAndPort)) {
					// write ip and port to preferences
					AppConfig.saveIpPort(getApplicationContext(), ipAndPort);
					// change activity
					iMain = new Intent(getApplication(), MainActivity.class);
					startActivity(iMain);
				}
				else {
					txtOldIp.setText("IP: Inválido.");
				}
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iMain = new Intent(getApplication(), MainActivity.class);
				startActivity(iMain);
			}
		});
		
		//Verify for an IP address, if everything is OK show it
		if (hasIp()) {
			SharedPreferences config = getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
			String ipAndPort = config.getString(AppConfig.IP_AND_PORT, "");
			txtOldIp.setText("IP: "+ipAndPort);
		}
		else {
			txtOldIp.setText("IP: Em Branco.");
		}
	}
	
	//check fo an IP
	private Boolean hasIp () {
		SharedPreferences config = getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
		String ip = config.getString(AppConfig.IP_AND_PORT, "");
		if (AppConfig.isValidIpPort(ip))
			return true;
		return false;
	}
}