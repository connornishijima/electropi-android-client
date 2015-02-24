package com.technicallycovered.electropimonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

public class WebViewFragment extends Fragment {

    FrameLayout rootView;
    private Bundle webViewBundle;
    final Context context = getActivity();

    public WebViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (FrameLayout) inflater.inflate(R.layout.fragment_webview, container, false);
        WebView wView = (WebView) rootView.findViewById(R.id.mainWeb);

        WebSettings settings = wView.getSettings();
        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(true);
        wView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                splash();
            }

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
                wView.loadUrl(String.format("http://%s/index.php", location));
            } else {
                wView.restoreState(webViewBundle);
            }
        }
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        webViewBundle = new Bundle();
        //wView.saveState(webViewBundle);
    }

    public void splash() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //show webview
                rootView.findViewById(R.id.mainWeb).setVisibility(View.VISIBLE);
                //hide loading image
                rootView.findViewById(R.id.imgLoad).setVisibility(View.GONE);
            }
        }, 1200);
    }
}
