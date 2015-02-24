package com.technicallycovered.electropimonitor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class PingService extends IntentService {

    private static boolean isLooping = false;

    public PingService() {
        super("PingService");
        wifiWait(0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isLooping) {
            isLooping = true;
            Log.i(Constants.LOG_TAG, "Starting initial run");
            mPrefs = PreferenceManager.getDefaultSharedPreferences(PingService.this);
        }
    }

    public void keepGoing(final int time_ms){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run()
            {
                try {
                    postWait(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, time_ms);
    }

    public void wifiWait(final int time_ms)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run()
            {
                if (isWiFiConnected()) {
                    Log.i(Constants.LOG_TAG, "Wifi is up!");

                    keepGoing(0);
                }
                else {
                    Log.i(Constants.LOG_TAG, "STILL NO WIFI, WHAT THE FUCK. I LIKE WIFI. OH WELL.");
                }
            }
        }, time_ms);
    }

    private SharedPreferences mPrefs;

    public void postWait(final int time_ms)
    {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(PingService.this);
        Handler handler2 = new Handler(Looper.getMainLooper());
        handler2.postDelayed(new Runnable() {
            public void run() {
                (new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                            Log.d(Constants.LOG_TAG, "Running Job");
                                String epiip = mPrefs.getString(Constants.EPiIP, "");
                                String devName = mPrefs.getString(Constants.DEVICE_NAME, "");
                                if (epiip.length() > 0 && devName.length() > 0) {
                                    try {
                                        HttpClient client = new DefaultHttpClient();
                                        String getURL = "http://%s/checkIn.php?type=android&deviceNickname=%s";
                                        HttpGet get = new HttpGet(String.format(getURL, epiip, devName));
                                        client.execute(get);
                                        Log.d(Constants.LOG_TAG, "Sent checkIn to EPI");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            return null;
                    }
                }
                ).execute();
            }
        }, time_ms);
    }

    private boolean isWiFiConnected()
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mWifi.isConnected() == true){
            String SSIDon = getWifiName(getApplicationContext()).replace("*","");
            String SSID = mPrefs.getString(Constants.SSID, "");
            Log.d(Constants.LOG_TAG, "Connected to "+SSIDon+", Saved is "+SSID);
            if(String.valueOf(SSID).trim().contentEquals(String.valueOf(SSIDon).trim())) {
                Log.d(Constants.LOG_TAG, "We're home!");
                return true;
            }
            else{
                Log.d(Constants.LOG_TAG, "We've got WiFi, but we're not home.");
                return false;
            }
        }
        else{
            return false;
        }
    }

    public String getWifiName(Context context) {
        try {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(PingService.this);

            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (manager.isWifiEnabled()) {
                WifiInfo wifiInfo = manager.getConnectionInfo();
                if (wifiInfo != null) {
                    NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                    if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        return wifiInfo.getSSID().replace('"','*');
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
