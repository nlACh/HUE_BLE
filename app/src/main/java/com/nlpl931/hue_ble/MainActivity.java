package com.nlpl931.hue_ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static String DEVICE_ADDR;

    ListView plv;
    TextView tpairedDev;

    private BluetoothAdapter mBT;
    private ArrayAdapter<String> pairedDevArrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        pairedDevArrAdapter = new ArrayAdapter<String>(this,R.layout.dev_list);
        plv = (ListView) findViewById(R.id.pairedDevices);

        plv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int arg, long l) {
                tpairedDev = (TextView) findViewById(R.id.title_paired_devices);
                tpairedDev.setText("Connecting...");

                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) view).getText().toString();
                Log.w("info:",info);
                //String address = info.substring(info.length() - 17);
                String address = "98:D3:33:80:68:00";

                // Make an intent to start next activity while taking an extra which is the MAC address.
                Intent i = new Intent(getBaseContext(), ArduinoMain.class);
                i.putExtra(DEVICE_ADDR, address);
                startActivity(i);
            }
        });

        plv.setAdapter(pairedDevArrAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        chkBTstate();

        pairedDevArrAdapter.clear();
        mBT = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBT.getBondedDevices();

        if (pairedDevices.size()>0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevArrAdapter.add(device.getName() + "\n");
            }
        }
        else
            pairedDevArrAdapter.add("no devices....");

    }

    private void chkBTstate()
    {
        mBT=BluetoothAdapter.getDefaultAdapter();
        if(mBT==null)
        {
            Toast.makeText(getBaseContext(), "BT not supported by device", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            if(!mBT.isEnabled())
            {
                Intent enBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enBT,1);
            }
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
}
