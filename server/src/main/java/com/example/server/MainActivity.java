package com.example.server;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Button btnSend;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private final String SERVER_IP = "10.0.2.15";
    private final int SERVER_PORT = 10001;
    private String receivedMsg = "";
    private TextView tv_from_server;
    private TextView btn_connect;
    private TextView btn_send;
    private ExecutorService mExecuterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, ServerService.class);
        startService(intent);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tv_from_server = findViewById(R.id.tv_from_server);
        btn_connect = findViewById(R.id.btn_connect);
        btn_send = findViewById(R.id.btn_send);
        mExecuterService = Executors.newCachedThreadPool();
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExecuterService.execute(new ConnectThread());
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExecuterService.execute(new SendThread());
            }
        });

    }

    class ConnectThread implements Runnable {

        @Override
        public void run() {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                Log.i(getLocalClassName(), "connected");
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                receiveMsg();

            } catch (IOException ioe) {

            }
          }

    }

    class SendThread implements  Runnable{
        @Override
        public void run() {
            printWriter.println(new Random().nextInt(20)+ "");
        }
    }


    /**
     * 接收来自服务器的消息
     */
    private void receiveMsg(){
        while(true){
            try {
                if((receivedMsg = bufferedReader.readLine()) != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          tv_from_server.setText(receivedMsg +"\n" + tv_from_server.getText());
                        }
                    });
                }

            }
            catch (IOException ioe){

            }
        }
    }
}
