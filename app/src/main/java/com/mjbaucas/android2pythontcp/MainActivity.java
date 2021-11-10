package com.mjbaucas.android2pythontcp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mjbaucas.android2pythontcp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static int TCP_SERVER_PORT = -1;
    private static String TCP_SERVER_HOST = null;

    private String data128 = generateData(128);
    private String data256 = generateData(256);
    private String data512 = generateData(512);
    private String data1024 = generateData(1024);
    private String data = "";
    private static final String[] sizes = {"128", "256", "512", "1024"};

    private double AVERAGE_TIME = 0.0;
    private int COUNTER = 0;
    private int TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sizes);
        binding.selectSize.setAdapter(adapter);
        binding.selectSize.setOnItemSelectedListener(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        binding.connectButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view){
               COUNTER = 0;
               AVERAGE_TIME = 0.0;
               long start = SystemClock.elapsedRealtime();

               try {
                   TCP_SERVER_HOST = binding.hostname.getEditText().getText().toString();
                   TCP_SERVER_PORT = Integer.parseInt(binding.port.getEditText().getText().toString());

                   if (TCP_SERVER_HOST != null && TCP_SERVER_PORT != -1) {
                       long current = SystemClock.elapsedRealtime();
                       while (current - start < TIME){
                           current = SystemClock.elapsedRealtime();
                           COUNTER++;
                           runTCPClient();
                       }

                       String tempString = "Average Time: " + AVERAGE_TIME / COUNTER;
                       binding.averageValue.setText(tempString);

                       binding.disconnectButton.setOnClickListener((new View.OnClickListener() {
                           public void onClick(View view) {
                               finish();
                           }
                       }));
                   } else {
                       finish();
                   }
               } catch (NullPointerException e){
                   e.printStackTrace();
               }
           }
        });
    }

    private void runTCPClient(){
        try {
            long start = SystemClock.elapsedRealtime();
            Socket s = new Socket(TCP_SERVER_HOST, TCP_SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            //String outMsg = "TCP connecting to " + TCP_SERVER_HOST + ":" + TCP_SERVER_PORT + System.getProperty("line.separator");
            String outMsg = data + System.getProperty("line.separator");
            out.write(outMsg);
            out.flush();
            Log.i("TCPClient", "sent: " + outMsg);
            String inMsg = in.readLine() + System.getProperty("line.separator");
            Log.i("TCPClient", "received: " + inMsg);
            long end = SystemClock.elapsedRealtime();
            AVERAGE_TIME = AVERAGE_TIME + (end - start);
            s.close();
        } catch (Exception e) {
            String tempString = "Error: " + e.toString();
            binding.averageValue.setText(tempString);
            e.printStackTrace();
        }
    }

    private String generateData(int size) {
        String temp_string = "";
        for (int i = 0; i <= size; i++) {
            temp_string = temp_string + "x";
        }
        return temp_string;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id){
        switch (position) {
            case 0:
                data = data128;
                break;
            case 1:
                data = data256;
                break;
            case 2:
                data = data512;
                break;
            case 3:
                data = data1024;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}