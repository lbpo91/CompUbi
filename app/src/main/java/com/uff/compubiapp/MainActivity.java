package com.uff.compubiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uff.compubiapp.service.ConnectionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

//    //RecyclerViewList
//    private List<ListItem> alertList = new ArrayList<>();
//    private RecyclerView alertRecyclerView;
//    private RecyclerViewVerticalListAdapter alertAdapter;
//    private Button addBtn;


    //UI Buttons
    Button btnStart, btnStop, btnReset, btnInterval, btnIP;
    //Text Views
    TextView txtIP, txtServiceStatus, txtMsgCount, txtMsgInterval
            , txtIsConnected;
    //Intents
    Intent iService, iIPAct;

    //handler to update UI
    final Handler uiHandler = new Handler();

    //Timer to update UI
    Timer uiTimer;
    TimerTask uiTimerTask;

    //Message interval is the same as update
    int screenRefreshRate = 1000; /*1 second*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        alertRecyclerView = findViewById(R.id.idRecyclerViewVerticalList);
//        // add a divider after each item for more clarity
//        alertRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
//        alertAdapter = new RecyclerViewVerticalListAdapter(alertList, getApplicationContext());
//        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
//        alertRecyclerView.setLayoutManager(verticalLayoutManager);
//        alertRecyclerView.setAdapter(alertAdapter);
//
//        addBtn = findViewById(R.id.idAddButton);
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ListItem alerta = new ListItem("Alerta adicionado", "-22.978011,-43.228914");
//                alertList.add(alerta);
//
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//
//                alertAdapter.notifyDataSetChanged();
//                r.play();
//            }
//        });
//
//        populateList();

        //link with the UI componentes
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop  = (Button) findViewById(R.id.btnStop);
        btnIP = (Button) findViewById(R.id.btnIP);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnInterval = (Button) findViewById(R.id.btnInterval);
        txtIP = (TextView) findViewById(R.id.txtIp);
        txtServiceStatus = (TextView) findViewById(R.id.txtServiceStatus);
        txtIsConnected = (TextView) findViewById(R.id.txtIsConnected);
        txtMsgCount = (TextView) findViewById(R.id.txtMsgCount);
        txtMsgInterval = (TextView) findViewById(R.id.txtMsgInterval);
        //create the intents
        iService = new Intent(this, ConnectionService.class);
        iIPAct = new Intent(this, ipActivity.class);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SDDL", "Start Clicked");
                if (!hasIpAndPort()) {
                    startActivity(iIPAct);
                }
                else {
                    startService(iService);
                    /*change the ui ot service started*/
                    AppConfig.setService(getApplicationContext(), true);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SDDL", "Stop Clicked");
                stopService(iService);
                AppConfig.setService(getApplicationContext(), false);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SDDL", "Reset clicked");
                // TODO add reset button
            }
        });

        btnInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SDDL", "Interval clicked");
                // TODO add change interval
            }
        });

        btnIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SDDL", "IP btn Clicked");
                startActivity(iIPAct);
            }
        });

        //Check if there is an IP address already set
        if (!hasIpAndPort()) {
            startActivity(iIPAct);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    /*start the timer*/
    private void startTimer () {
        uiTimerTask = new TimerTask() {
            public void run () {
                updateUi();
            }
        };
        uiTimer = new Timer();
        uiTimer.schedule(uiTimerTask, 0, screenRefreshRate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* check if there is an IP and Port on the config file */
    private Boolean hasIpAndPort () {
        String ip = AppConfig.getIpPort(getApplicationContext());
        if (AppConfig.isValidIpPort(ip))
            return true;
        return false;
    }

    /* print information on screen about ip and service */
    private void updateUi () {
        uiHandler.post(uiRunnable);
    }

    //Runnable to update the UI
    final Runnable uiRunnable = new Runnable() {
        public void run () {
            SharedPreferences config = getSharedPreferences(AppConfig.PREF_FILE, Context.MODE_PRIVATE);
            // get the ip address
            String ipAndPort = config.getString(AppConfig.IP_AND_PORT, "");
            txtIP.setText("IP: "+ipAndPort);
            // get the status os the service
            Boolean service = AppConfig.getService(getApplicationContext());
            if (service)
                txtServiceStatus.setText("Serviço: Ativado");
            else
                txtServiceStatus.setText("Serviço: Desativado");
            // check if it is connected
            Boolean isConnected = AppConfig.getIsConnected(getApplicationContext());
            if (isConnected)
                txtIsConnected.setText("Conectado: SIM");
            else
                txtIsConnected.setText("Conectado: NÃO");
            // counter of the messages
            int count = config.getInt("counter", 0);
            txtMsgCount.setText("Mensagens Enviadas: "+count);
            // get the interval between messages
            int interval = config.getInt("interval", 0);
            if (interval == 0)
                txtMsgInterval.setText("Intervalo das mensagens: --");
            else
                txtMsgInterval.setText("Intervalo das mensagens: "+interval);
        }
    };

    @Override
    protected void onPause() {
        //stop timer
        uiTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onStop() {
        //stop timer
        uiTimer.cancel();
        super.onStop();
    }

//    private void populateList(){
//        ListItem alerta1 = new ListItem("Alerta 1", "-22.978011,-43.228914");
//        ListItem alerta2 = new ListItem("Alerta 2", "-22.981801,-43.189344");
//        alertList.add(alerta1);
//        alertList.add(alerta2);
//
//        alertAdapter.notifyDataSetChanged();
//    }
}
