package com.nlpl931.hue_ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ArduinoMain extends AppCompatActivity {

    EditText et;
    Button b1, b2;

    private BluetoothAdapter ba = null;
    private BluetoothSocket bs = null;
    private OutputStream os = null;

    private static final UUID uid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public String addr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_main);

        //addKeyListener(); for edit text

        b1 = findViewById(R.id.send);
        ba = BluetoothAdapter.getDefaultAdapter();
        chkBTstate();
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendData("10");
                Toast.makeText(getBaseContext(), "Sending...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        addr = intent.getStringExtra(MainActivity.DEVICE_ADDR);

        BluetoothDevice bd = ba.getRemoteDevice(addr);
        try{
            bs = bd.createRfcommSocketToServiceRecord(uid);
        }catch (IOException ex1)
        {
            Toast.makeText(getBaseContext(), ex1.toString(), Toast.LENGTH_LONG ).show();
        }

        try {
            bs.connect();
        }catch (IOException e)
        {
            try {
                bs.close();
            }
            catch (IOException e2) {
                Toast.makeText(getBaseContext(), e2.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            os = bs.getOutputStream();
        }catch (IOException osex)
        {
            Toast.makeText(getBaseContext(), osex.toString(), Toast.LENGTH_LONG).show();
        }
        sendData("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try {
            bs.close();
        }catch (IOException ex)
        {
            Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private BluetoothSocket createBS(BluetoothDevice bd)throws Exception
    {
        return bd.createRfcommSocketToServiceRecord(uid);
    }

    private void chkBTstate()
    {
        ba=BluetoothAdapter.getDefaultAdapter();
        if(ba==null)
        {
            Toast.makeText(getBaseContext(), "BT not supported by device", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            if(!ba.isEnabled())
            {
                Intent enBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enBT,1);
            }
        }
    }

    private void sendData(String msg)
    {
        byte[] msgBuffer = msg.getBytes();
        try {
            os.write(msgBuffer);
        }catch (IOException ex)
        {
            Toast.makeText(getBaseContext(),ex.toString(),Toast.LENGTH_LONG).show();
            finish();
        }
    }
/**
    private void addKeyListener()
    {
        et = (EditText) findViewById(R.id.editText);

        // add a keylistener to keep track user input
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // if keydown and send is pressed implement the sendData method
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    //I have put the * in automatically so it is no longer needed when entering text
                    sendData('*' + editText.getText().toString());
                    Toast.makeText(getBaseContext(), "Sending text", Toast.LENGTH_SHORT).show();

                    return true;
                }

                return false;
            }
        });
    }
 **/
}
