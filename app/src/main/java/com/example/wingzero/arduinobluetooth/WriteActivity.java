package com.example.wingzero.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class WriteActivity extends AppCompatActivity {

    Button btnsend;
    EditText inputvalue;
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Write Activity");

        Intent newint = getIntent();
        address = newint.getStringExtra(activity_device.EXTRA_ADDRESS); //receive the address of the bluetooth device

        btnsend = (Button)findViewById(R.id.send_bluetooth);
        inputvalue=(EditText)findViewById(R.id.write_data);

        final String senddata = inputvalue.getText().toString().toLowerCase();

        new ConnectBT().execute(); //Call the class to connect

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendata(senddata);
            }
        });

    }

    private  void sendata(String data)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(data.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    //message for error
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected


        @Override
        protected Void doInBackground(Void... devices) //the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
        }
    }
}
