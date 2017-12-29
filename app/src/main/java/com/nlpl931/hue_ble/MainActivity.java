package com.nlpl931.hue_ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    boolean isConnected=false;
    private BluetoothAdapter ba = null;
    private BluetoothSocket bs = null;
    private OutputStream os = null;
    public String addr = "98:D3:33:80:68:00";
    private static final UUID uid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        Button set = findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected)
                    sendData("1223--0");
                else
                    Toast.makeText(getBaseContext(), "Connect first!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        chkBTstate();
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

    private void connect(MenuItem item)
    {
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
        sendData("x0");
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
        if (id == R.id.find) {
            connect(item);
            isConnected=true;
            return true;
        }
        if (id == R.id.disconnect)
        {
            try{
                bs.close();
                isConnected = false;
            }catch (IOException es)
            {
                Toast.makeText(getBaseContext(), es.toString(), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
