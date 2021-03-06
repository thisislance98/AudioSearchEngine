package com.adev.abmp3;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragment;
import com.codeimage.music.dowloader.R;

public class AppWallFragment extends SherlockFragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View inflate = inflater.inflate(R.layout.app_wall, container, false);
		WebView webView = (WebView)inflate.findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		final ProgressDialog mProgress = ProgressDialog.show(getActivity(), "Loading", "Please wait for a moment...");
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			public void onPageFinished(WebView view, String url) {
				if(mProgress.isShowing()) {
					mProgress.dismiss();
				}
			}
		});
		webView.loadUrl(getString(R.string.leadbolt_url));
		return inflate;
	}
}
