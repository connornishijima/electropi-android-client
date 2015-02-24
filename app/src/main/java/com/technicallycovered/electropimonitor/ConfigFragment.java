package com.technicallycovered.electropimonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class ConfigFragment extends Fragment {

    public ConfigFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final EditText devName = (EditText) rootView.findViewById(R.id.deviceName);
        final EditText ipAddress = (EditText) rootView.findViewById(R.id.epiIp);
        final EditText netSSID = (EditText) rootView.findViewById(R.id.netSSID);
        final Context context = getActivity();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        devName.setText(prefs.getString(Constants.DEVICE_NAME,""));
        ipAddress.setText(prefs.getString(Constants.EPiIP, ""));
        netSSID.setText(prefs.getString(Constants.SSID, ""));

        rootView.findViewById(R.id.helpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelp(context);
            }
        });

        rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(Constants.DEVICE_NAME, devName.getText().toString());
                edit.putString(Constants.EPiIP, ipAddress.getText().toString());
                edit.putString(Constants.SSID, netSSID.getText().toString());
                edit.apply();
                rootView.findViewById(R.id.settingsSaved).setVisibility(View.VISIBLE);
                ((MainActivity)getActivity()).goToWebView();
            }
        });

        TextView appDeveloped = (TextView)rootView.findViewById(R.id.appDeveloped);

        appDeveloped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://technicallycovered.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Spannable wordtoSpan = new SpannableString(appDeveloped.getText());
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.highlightColor)), 17, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        appDeveloped.setText(wordtoSpan);
        return rootView;
    }

    public void showHelp(Context context) {
        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("ElectroPi Client Help");
            builder1.setMessage("Server IP:\nThis is the IP Address of your Raspberry Pi\n" +
                    "\n" +
                    "Device Nickname:\n" +
                    "This is a unique nickname that identifies this device to the Pi.\n" +
                    "\n" +
                    "WiFi SSID:\n" +
                    "The EPi Client Service checks your current WiFi connection's SSID against this value to make sure you're on your own network.");
            builder1.setCancelable(true);
            builder1.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
