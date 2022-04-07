package com.mjbaucas.android2pythontcp;

import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.mjbaucas.android2pythontcp.databinding.ActivityMainBinding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static int TCP_SERVER_PORT = -1;
    private static String TCP_SERVER_HOST = null;

    private double AVERAGE_TIME = 0.0;
    private double AVERAGE_PROCESSOR_TIME = 0.0;
    private int COUNTER = 0;
    private int TIME = 120000;
    private boolean CONNECTED = false;
    private String SECRETKEY = "5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9";
    private PublicChain PCHAIN;

    /*
        109 - 1080
        235 - 2200
        456 - 4216
        971 - 8800~
        1788 - 16184
        3650 - 33048
    */
    private int SIZEOFBLOCK = 3650;
    private int CHAINMODE = 1;

    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private String dataX = generateData(0) + "default_0";
    private String data = "";
    private static final String[] sizes = {"1K", "2K", "4K", "8K", "16K", "32K", "chain"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(CHAINMODE == 0){
            data1 = generateData(1024);
            data2 = generateData(2048);
            data3 = generateData(4096);
            data4 = generateData(8192);
            data5 = generateData(16384);
            data6 = generateData(32768);
        }

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

        ArrayList<String> trustedList = new ArrayList<String>();
        trustedList.add("default");


        for (int i = 0; i < SIZEOFBLOCK; i++){
            trustedList.add("item" + Integer.toString(i));
        }

        try {
            PCHAIN = new PublicChain(2);
            PCHAIN.genNextBlock(SECRETKEY, trustedList);
            Log.i("Block Size", "size: " + serialize(PCHAIN.getBlock(1).getTransactions()).length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.connectButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view){
               if (CONNECTED == false) {
                   COUNTER = 0;
                   AVERAGE_TIME = 0.0;
                   AVERAGE_PROCESSOR_TIME = 0.0;
                   long start = SystemClock.elapsedRealtime();

                   try {
                       //TCP_SERVER_HOST = binding.hostname.getEditText().getText().toString();
                       //TCP_SERVER_PORT = Integer.parseInt(binding.port.getEditText().getText().toString());

                       TCP_SERVER_HOST = "192.168.2.132";
                       TCP_SERVER_PORT = 5000;

                       if (TCP_SERVER_HOST != null && TCP_SERVER_PORT != -1) {
                           Thread thread = new Thread(new Runnable() {
                               @Override
                               public void run() {
                                   try {
                                       long current = SystemClock.elapsedRealtime();
                                       while (current - start < TIME) {
                                           current = SystemClock.elapsedRealtime();
                                           runTCPClient();
                                       }

                                       runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               String tempString = "Average Time: " + AVERAGE_TIME / COUNTER;
                                               String tempStringP = "Average Processing Time: " + AVERAGE_PROCESSOR_TIME / COUNTER;
                                               binding.averageValue.setText(tempString);
                                               binding.averageProcessorValue.setText(tempStringP);
                                               CONNECTED = false;
                                           }
                                       });
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                               }
                           });
                           thread.start();
                       } else {
                           finish();
                       }
                   } catch (NullPointerException e) {
                       e.printStackTrace();
                   }
               } else {
                   binding.connectButton.setText("Connect");
                   CONNECTED = false;
                   finish();
               }
           }
        });
    }

    private void runTCPClient(){
        try {
            long process_start = SystemClock.elapsedRealtime();  // Include time to connect socket
            Socket s = new Socket(TCP_SERVER_HOST, TCP_SERVER_PORT);
            DataInputStream dataIn = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            //String outMsg = "TCP connecting to " + TCP_SERVER_HOST + ":" + TCP_SERVER_PORT + System.getProperty("line.separator");
            long start;
            String outMsg;
            if (CHAINMODE == 1){
                String newData = data + "_" + PCHAIN.proofOfWork(PCHAIN.getBlock(1));
                start = SystemClock.elapsedRealtime();
                outMsg = newData + System.getProperty("line.separator");
            } else {
                start = SystemClock.elapsedRealtime();
                outMsg = data + System.getProperty("line.separator");
            }
            dataOut.writeInt(outMsg.length());
            dataOut.writeBytes(outMsg);
            dataOut.flush();

            int length = dataIn.readInt();
            byte[] message = new byte[length];
            double server_time = 0.0;
            if(length > 0){
                dataIn.readFully(message, 0, message.length);
                server_time = Integer.parseInt(new String(message, StandardCharsets.UTF_8));
                Log.i("TCPClient", "length: " + (message.length - 1) + " value: " + server_time + " proc diff: " + AVERAGE_PROCESSOR_TIME + " counter: " + COUNTER);
            }
            long end = SystemClock.elapsedRealtime();
            s.close();
            long process_end = SystemClock.elapsedRealtime(); // Include time to close socket
            AVERAGE_TIME = AVERAGE_TIME + (end - start);
            AVERAGE_PROCESSOR_TIME = AVERAGE_PROCESSOR_TIME + ((process_end - process_start) - (end - start)) + server_time;
            COUNTER++;
        } catch (Exception e) {
            String tempString = "Error: " + e.toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.averageValue.setText(tempString);
                }
            });
            e.printStackTrace();
        }
    }

    private String generateData(int size) {
        String temp_string = "";
        for (int i = 0; i <= size - 1; i++) {
            temp_string = temp_string + "x";
        }
        return temp_string;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 0:
                data = data1;
                break;
            case 1:
                data = data2;
                break;
            case 2:
                data = data3;
                break;
            case 3:
                data = data4;
                break;
            case 4:
                data = data5;
                break;
            case 5:
                data = data6;
                break;
            case 6:
                data = dataX;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }
}