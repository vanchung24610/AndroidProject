package com.example.testwifiswitchh;

import java.lang.reflect.Method;

import org.apache.http.impl.conn.Wire;

import android.R.bool;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Build;
import android.provider.Settings;
import android.provider.DocumentsContract.Root;

public class MainActivity extends ActionBarActivity { 
	static Switch switch_wifi;
	static Switch switch_bluetooth;
	static WifiManager wifi;
	static BluetoothAdapter mBluetoothAdapter;
	static Button change_brightness;
	static Switch switch_mobile_data;
	static Method dataConnSwitchmethod;
    static Class telephonyManagerClass;
    static Object ITelephonyStub;
    static Class ITelephonyClass;
    static TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //init switch to on/off default with wifi
            switch_wifi=(Switch)rootView.findViewById(R.id.switch_wifi);
            wifi=(WifiManager) rootView.getContext().getSystemService(Context.WIFI_SERVICE);
            if(wifi.isWifiEnabled()){
        		switch_wifi.setChecked(true);
        	}else{
        		switch_wifi.setChecked(false);
        	}
            
            //init switch to on/off default with bluetooth
            switch_bluetooth=(Switch)rootView.findViewById(R.id.switch_bluetooth);
            mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            //have bluetooth
            if(mBluetoothAdapter != null){
            	if(mBluetoothAdapter.isEnabled()){
            		switch_bluetooth.setChecked(true);
            	}else{
            		switch_bluetooth.setChecked(false);
            	}
            }
            //init change screen brightness
            change_brightness = (Button) rootView.findViewById(R.id.change_brightness);
            //init mobile data connect
            switch_mobile_data = (Switch) rootView.findViewById(R.id.switch_mobile_data);
            telephonyManager = (TelephonyManager) getActivity().getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED){
            	switch_mobile_data.setChecked(true);
            }else{
                switch_mobile_data.setChecked(true);
            }
            return rootView;
                        
        }
        
    }
    public void switchWifi(View view){
    	//set click on/off wifi
    	switch_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked){
					switch_wifi.setChecked(true);
					wifi.setWifiEnabled(true);
					Toast.makeText(getApplicationContext(), "On wifi",Toast.LENGTH_LONG).show();
				}
				else{
					switch_wifi.setChecked(false);
					wifi.setWifiEnabled(false);
					Toast.makeText(getApplicationContext(), "Off wifi",Toast.LENGTH_LONG).show();
				}
				
			}
		});
    }
    
    //set screen brightness
    public void setBrightness(View view){
    	change_brightness.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				changeBrightness();
				changeLockScreenTimeOut();
			}
		});
    }	
    public void changeBrightness(){
    	//change screen brightness
    	Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, 20);
    	Toast.makeText(this, "Changed",Toast.LENGTH_LONG).show();;
    }
    public void changeLockScreenTimeOut(){
    	//change lock screen time out
    	Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 20);
    }
    //set state for mobile data connect
    //still pending
    public void switchMobileData(View view){
    	switch_mobile_data.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				try{telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
			    Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
			    getITelephonyMethod.setAccessible(true);
			    ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
			    ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
				
				if(isChecked){
					dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("enableDataConnectivity"); 
				}else{
					dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
				}
				dataConnSwitchmethod.setAccessible(true);
			    dataConnSwitchmethod.invoke(ITelephonyStub);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
    }
    
    public void switchBluetooth(View view){
    	//set click on/off bluetooth
    	switch_bluetooth.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked){
					switch_bluetooth.setChecked(true);
					mBluetoothAdapter.enable();
					Toast.makeText(getApplicationContext(), "Off bluetooth",Toast.LENGTH_LONG).show();
				}
				else{
					switch_bluetooth.setChecked(false);
					mBluetoothAdapter.disable();
					Toast.makeText(getApplicationContext(), "Off bluetooth",Toast.LENGTH_LONG).show();
				}
				
			}
		});
    }
}
