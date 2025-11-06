package nnctwo.tw;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import rgtf.uut.de;



public class Stns extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView webView, int i10) {
        super.onProgressChanged(webView, i10);
        Log.e("zzzz", "onProgressChanged1: "+webView.getUrl());
        if (i10 == 100) {
            Log.e("zzzz", "onProgressChanged2: "+webView.getUrl());

            de.nbtw(i10);
        }
    }
}
