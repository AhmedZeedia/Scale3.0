package com.example.scale30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;;

import com.starmicronics.starmgsio.ConnectionInfo;
import com.starmicronics.starmgsio.StarDeviceManager;
import com.starmicronics.starmgsio.StarDeviceManagerCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView lstvw;
    private static final String DEVICE_NAME_KEY    = "DEVICE_NAME_KEY";
    private static final String IDENTIFIER_KEY     = "IDENTIFIER_KEY";
    private static final String INTERFACE_TYPE_KEY = "INTERFACE_TYPE_KEY";
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    //private ArrayAdapter aAdaper;
    //private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    private StarDeviceManager mStarDeviceManager;
    private List<Map<String, String>> mDataMapList;
    private SimpleAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0x00);

        }

        ListView discoveredListView = findViewById(R.id.DiscoveredListView);

        mDataMapList = new ArrayList<>();

        mAdapter = new SimpleAdapter(
                this,
                mDataMapList,
                R.layout.list_discovered_row,
                /*new String[] {INTERFACE_TYPE_KEY, IDENTIFIER_KEY,},
                new int[] {R.id.InterfaceTypeTextView, R.id.IdentifierTextView});*/
                new String[] { INTERFACE_TYPE_KEY,  DEVICE_NAME_KEY, IDENTIFIER_KEY},
                new int[] { R.id.InterfaceTypeTextView, R.id.DeviceNameTextView,
                        R.id.IdentifierTextView});

        discoveredListView.setAdapter(mAdapter);

        discoveredListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mDataMapList.clear();
                mAdapter.notifyDataSetChanged();

                mStarDeviceManager.stopScan();

                TextView identifierTextView    = view.findViewById(R.id.IdentifierTextView);
                String   identifier            = identifierTextView.getText().toString();
                TextView interfaceTypeTextView = view.findViewById(R.id.InterfaceTypeTextView);
                String   interfaceType         = interfaceTypeTextView.getText().toString();

                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra(Main2Activity.IDENTIFIER_BUNDLE_KEY,  identifier);
                intent.putExtra(Main2Activity.INTERFACE_TYPE_BUNDLE_KEY, interfaceType);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStarDeviceManager = new StarDeviceManager(MainActivity.this,
                StarDeviceManager.InterfaceType.All);

        mStarDeviceManager.scanForScales(mStarDeviceManagerCallback);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mStarDeviceManager.stopScan();

    }
    private final StarDeviceManagerCallback mStarDeviceManagerCallback = new StarDeviceManagerCallback() {
        @Override
        public void onDiscoverScale(@NonNull ConnectionInfo connectionInfo) {
            super.onDiscoverScale(connectionInfo);
            Map<String, String> item = new HashMap<>();
            item.put(INTERFACE_TYPE_KEY, connectionInfo.getInterfaceType().name());
            item.put(DEVICE_NAME_KEY, connectionInfo.getDeviceName());
            item.put(IDENTIFIER_KEY,  connectionInfo.getIdentifier());



            if(!mDataMapList.contains(item)) {
                mDataMapList.add(item);
                mAdapter.notifyDataSetChanged();
            }
        }
    };
}
