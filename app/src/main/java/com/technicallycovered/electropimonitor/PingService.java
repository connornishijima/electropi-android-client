package com.technicallycovered.electropimonitor;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class PingService extends IntentService {

    private static boolean isLooping = false;

    public PingService() {
        super("PingService");
        setGlobalSecValue(10000);
        wifiWait(10000,false);
        }


    private int mGlobalSecValue;
    public int getGlobalSecValue() {
        return mGlobalSecValue;
    }
    public void setGlobalSecValue(int num) {
        mGlobalSecValue = num;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isLooping) {
            setGlobalSecValue(1);
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
                    if (isWiFiConnected()) {

                        postWait(0);
                        int milliseconds = (getGlobalSecValue() * 1000);
                        keepGoing(milliseconds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    int milliseconds = (getGlobalSecValue() * 1000);
                    keepGoing(milliseconds);
                }
            }
        }, time_ms);
    }

    public void wifiWait(final int time_ms, boolean noWifi)
    {
        if(noWifi == true){
            if (!isWiFiConnected()) {
                //stopSelf();
            }
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run()
            {
                if (isWiFiConnected()) {
                    Log.i(Constants.LOG_TAG, "Wifi is up!");

                    keepGoing(1000);
                }
                else {
                    Log.i(Constants.LOG_TAG, "STILL NO WIFI, WHAT THE FUCK.");
                    wifiWait(30000, true);
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
                                Log.d(Constants.LOG_TAG, "Job has Wifi");
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
                                    try {
                                        HttpClient client = new DefaultHttpClient();
                                        String getURL = "http://%s/checkIn.php?interval";
                                        HttpGet get = new HttpGet(String.format(getURL, epiip));
                                        int seconds = Integer.parseInt(EntityUtils.toString(client.execute(get).getEntity()));
                                        setGlobalSecValue(seconds);
                                        Log.d(Constants.LOG_TAG, "Got new interval ("+String.valueOf(seconds)+") from EPi");
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
            return true;
        }
        else{
            return false;
        }
    }
}
