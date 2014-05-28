package ceab.movlab.tigerapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class WebViewApi7{

public static void api7settings(WebView myWebView, Context context) {

	myWebView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
	myWebView.getSettings().setAppCachePath(
			context.getCacheDir().getAbsolutePath());
	myWebView.getSettings().setAppCacheEnabled(true);

	return;
}

}