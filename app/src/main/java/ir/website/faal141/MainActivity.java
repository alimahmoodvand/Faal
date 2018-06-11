package ir.website.faal141;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OSService.context=this;
//        Log.d(TAG, "onStartCommand: "+Utility.getBool("inapp")+";"+PopActivityV2.isRun);
//        Utility.setBool("inapp",true);
//        Utility.clearParams();
//        Utility.clearTimeout();
//        Utility.setLong("removeicon",Utility.addMinutes(UtilityV2.removeicon).getTime());
//        Utility.isFirst=true;
//        Intent intent=new Intent(this,OSService.class);
//        startService(intent);
//        finish();.
//        setTitle("فال روزانه");
//        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getActionBar().setCustomView(R.layout.actionbar);
//        setContentView(R.layout.my_app);
//        mWebView = (WebView) findViewById(R.id.month_webview);
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.loadUrl("http://paydane.ir/apis/month.php");
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//            }
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            }
//        });

    }
}
