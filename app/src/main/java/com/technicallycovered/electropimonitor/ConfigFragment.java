package com.technicallycovered.electropimonitor;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final EditText devName = (EditText) rootView.findViewById(R.id.deviceName);
        final EditText ipAddress = (EditText) rootView.findViewById(R.id.epiIp);
        final EditText netSSID = (EditText) rootView.findViewById(R.id.netSSID);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        devName.setText(prefs.getString(Constants.DEVICE_NAME,""));
        ipAddress.setText(prefs.getString(Constants.EPiIP, ""));
        netSSID.setText(prefs.getString(Constants.SSID, ""));

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
}
