package com.mjbaucas.android2pythontcp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static int TCP_SERVER_PORT = -1;
    private static String TCP_SERVER_HOST = null;
    private static int COUNTER_LIMIT = -1;

    private double AVERAGE_TIME = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        binding.connectButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view){
               try {
                   TCP_SERVER_HOST = binding.hostname.getEditText().getText().toString();
                   TCP_SERVER_PORT = Integer.parseInt(binding.port.getEditText().getText().toString());

                   if (TCP_SERVER_HOST != null && TCP_SERVER_PORT != -1) {
                       for (int i = 0; i <= COUNTER_LIMIT; i++) {
                           runTCPClient();
                       }

                       String tempString = "Average Time: " + AVERAGE_TIME / COUNTER_LIMIT;
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
            String outMsg = "TCP connecting to " + TCP_SERVER_HOST + ":" + TCP_SERVER_PORT + System.getProperty("line.separator");
            out.write(outMsg);
            out.flush();
            Log.i("TCPClient", "sent: " + outMsg);
            String inMsg = in.readLine() + System.getProperty("line.separator");
            Log.i("TCPClient", "received: " + inMsg);
            long end = SystemClock.elapsedRealtime();
            AVERAGE_TIME = AVERAGE_TIME + (end - start);
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}