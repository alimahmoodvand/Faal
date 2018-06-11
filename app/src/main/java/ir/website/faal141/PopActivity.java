package ir.website.faal141;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PopActivity extends Activity {
    private WebView mWebView;
    public static  Activity activity=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PopActivity.activity=this;
        setTitle("");
        Log.d("appstatus", OSService.notification + "onCreate............................................................");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/www/index.html");
        mWebView.addJavascriptInterface(new JsInterface(), "MyHandler");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:receiveNotification('" + OSService.notification + "')");
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });
        Log.d("appstatus", "onFinish............................................................");
    }
    @Override
    protected void onDestroy() {
        moveTaskToBack(true);

        super.onDestroy();
      //  Utility.resumePopActivity(OSService.notification,20000);
        Log.d("appstatus", OSService.notification + "onDestroy............................................................");
    }
    @Override
    public void onBackPressed() {}
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("appstatus", OSService.notification + "onResume............................................................");
    }
    @Override
    protected void onStop() {
        moveTaskToBack(true);
        super.onStop();
        Utility.resumePopActivity(OSService.notification,20000);

        Log.d("appstatus", OSService.notification + "onStop............................................................");
    }
    @Override
    protected void onPause() {
        moveTaskToBack(true);


        super.onPause();

        // Utility.resumePopActivity(OSService.notification,20000);
        Log.d("appstatus", OSService.notification + "onPause............................................................");
    }
}