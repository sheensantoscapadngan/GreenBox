package com.example.android.greenbox;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ConnectPageAdapter.ConnectCallback{

    private ImageView menu,connect,blackpopup;
    private ConstraintLayout connectLayout,mainLayout;
    private DrawerLayout drawerLayout;
    private RecyclerView connectRecyclerView;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private Set<BluetoothDevice> deviceList;
    private ArrayList<String> nameList,addressList;
    private ConnectPageAdapter adapter;
    private static final UUID myUUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private SendReceive sendReceive;
    private Handler handler;
    private String phText,temperatureText,moistureText,dummyText = "";
    private TextView ph,temperature,moisture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        activateListeners();
        setupRecyclerView();
        setupBluetooth();
        loadBluetoothList();
    }

    private void setupRecyclerView() {

        adapter = new ConnectPageAdapter(addressList,nameList,this);
        connectRecyclerView.setAdapter(adapter);
        connectRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void setupBluetooth() {

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = myBluetoothAdapter.getBondedDevices();

    }

    private void activateListeners() {

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(Gravity.START);

            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                blackpopup.setVisibility(View.VISIBLE);
                connectLayout.setVisibility(View.VISIBLE);

            }
        });

    }

    private void setupViews() {

        menu = (ImageView) findViewById(R.id.imageViewMainMenu);
        connect = (ImageView) findViewById(R.id.imageViewMainConnect);
        connectLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutMainConnect);
        mainLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutMain);
        blackpopup = (ImageView) findViewById(R.id.imageViewMainBlackPopup);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMain);
        connectRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewConnect);

        ph = (TextView) findViewById(R.id.textViewPh);
        temperature = (TextView) findViewById(R.id.textViewTemperature);
        moisture = (TextView) findViewById(R.id.textViewMoisture);

        nameList = new ArrayList<>();
        addressList = new ArrayList<>();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        byte[] readBuff = (byte[]) msg.obj;
                        String message = new String(readBuff, 0, msg.arg1);
                        Log.d("MAIN", "MESSAGE IS " + message);

                }
            }
        };


    }

    private void loadBluetoothList() {

        if(deviceList.size() > 0){
            for(BluetoothDevice device : deviceList){
                addressList.add(device.getAddress());
                nameList.add(device.getName());
            }
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onBackPressed() {

        if(blackpopup.getVisibility() == View.VISIBLE){

            blackpopup.setVisibility(View.GONE);
            connectLayout.setVisibility(View.GONE);

        }else{
            MainActivity.super.onBackPressed();
        }


    }

    @Override
    public void notifyBluetoothConnect(String address) {

        BluetoothDevice bt = myBluetoothAdapter.getRemoteDevice(address);

        try{

            bluetoothSocket = bt.createInsecureRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();

            sendReceive = new SendReceive(bluetoothSocket);
            sendReceive.start();

            connectLayout.setVisibility(View.GONE);

        }catch(IOException e){
            e.printStackTrace();
        }


        connectLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);

    }

    public class SendReceive extends Thread{

        InputStream inputStream;
        OutputStream outputStream;
        BluetoothSocket socket;

        public SendReceive(BluetoothSocket socket){

            this.socket = socket;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        public void run(){

            byte[] buffer = new byte[256];
            int bytes;

            while(true){
                try{

                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();

                }catch (Exception e){
                    break;
                }
            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
