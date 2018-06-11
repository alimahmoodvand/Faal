package ir.website.faal141;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PopActivityV2 extends Activity {
    private static final String TAG = "PopActivityV2";
    public static WebView mWebView;
    public static Activity activity;
    public static ProgressDialog progress;
    public static Boolean isRun=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Utility.getBool("inapp")) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setTheme(R.style.ThemeV2);
            } else {
                setTheme(R.style.Theme17);
            }
        }
        super.onCreate(savedInstanceState);
        if(OSService.notification.isEmpty()){
            finish();
            return;
        }
        setTitle("");
        activity=this;
        renderUI(false);
        isRun=true;

    }
    @Override
    protected void onDestroy() {
//        if(!Utility.getBool("inapp")){
//            moveTaskToBack(true);
//        }
        super.onDestroy();
        try {
            progress.cancel();
            progress.dismiss();
        }catch (Exception ex){

        }

        try {
            activity=null;
            OSService.notification="";
            finish();
        }catch (Exception ex){

        }

        Utility.setLong("interval", Utility.addMinutes(UtilityV2.interval).getTime());
        Log.d(TAG, "onDestroy: ");
        isRun=false;

    }
    @Override
    public void onBackPressed() {}
    @Override
    protected void onResume() {
        super.onResume();
        isRun=true;
    }
    @Override
    protected void onStop() {
//        if(!Utility.getBool("inapp")){
//            moveTaskToBack(true);
//        }
        super.onStop();
//        isRun=false;
//        if(!Utility.getBool("inapp")){
//            ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                    .getSystemService(Context.ACTIVITY_SERVICE);
//            activityManager.moveTaskToFront(getTaskId(), 0);
//            moveTaskToBack(true);
//
//            isRun=true;
//        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        isRun=false;
        if(!Utility.getBool("inapp")){
            moveTaskToBack(true);
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
            isRun=true;
        }
    }
    public static void renderUI(final Boolean loading){
        Log.d(TAG, "renderUI: "+OSService.notification.length());
        UtilityV2.setLink();


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.setContentView(R.layout.activity_inapp);
                mWebView = (WebView) activity.findViewById(R.id.activity_inapp_webview);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.loadUrl("file:///android_asset/www/indexv2.html");
                mWebView.addJavascriptInterface(new JsInterface(), "MyHandler");
                Log.d(TAG, "run: renderUI"+loading);
                if (loading) {
                    try{
                        progress.cancel();
                        progress.dismiss();
                    }
                    catch (Exception ex){
                    }
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        progress = new ProgressDialog(activity, R.style.AppCompatAlertDialogStyle21);
                    } else {
                        progress = new ProgressDialog(activity);
                    }
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setCancelable(false);
                    progress.setProgressDrawable(activity.getResources().getDrawable(R.drawable.loading));
                    progress.setMessage("لطفا منتظر بمانید . . . ");
                    progress.show();
                }
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        mWebView.loadUrl("javascript:receiveNotification('" + OSService.notification + "')");
                        UtilityV2.setJSStepTwo("");
                        if(loading){
                            mWebView.loadUrl("javascript:hideUI()");
                        }
                    }
                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    }
                });
            }
        });
    }
}