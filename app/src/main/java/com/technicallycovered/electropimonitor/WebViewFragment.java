package com.technicallycovered.electropimonitor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewFragment extends Fragment {

    WebView rootView;
    private Bundle webViewBundle;

    public WebViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (WebView) inflater.inflate(R.layout.fragment_webview, container, false);
        WebSettings settings = rootView.getSettings();
        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(true);
        rootView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String location = prefs.getString(Constants.EPiIP, "");
        if (location.length() == 0)
            Toast.makeText(getActivity(),"Set EPi Server IP first", Toast.LENGTH_SHORT).show();
        else
        {
            if (webViewBundle == null) {
                rootView.loadUrl(String.format("http://%s/index.php", location));
            } else {
                rootView.restoreState(webViewBundle);
            }
        }
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        webViewBundle = new Bundle();
        rootView.saveState(webViewBundle);
    }
}
