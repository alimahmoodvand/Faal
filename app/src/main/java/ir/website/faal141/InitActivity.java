package ir.website.faal141;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InitActivity extends Activity {

    private static final String TAG = "InitActivityLog";
    private List<Map<String, Object>> tabs=new ArrayList<Map<String, Object>>();
    public static Button updateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OSService.context=this;
        if(Utility.getInt("verifybanner")!=0) {
            initItems();
            Utility.setBool("inapp", false);
            Utility.clearParams();
            Utility.clearTimeout();
            Utility.setLong("removeicon", Utility.addMinutes(UtilityV2.removeicon).getTime());
            Utility.isFirst = true;
            Intent intent = new Intent(this, OSService.class);
            startService(intent);
            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getActionBar().setCustomView(R.layout.actionbar);
            updateBtn = (Button) (findViewById(R.id.action_bar).findViewById(R.id.update));
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.middleBlue));
            }
            setContentView(R.layout.activity_init);
            try {

                initItems();
                Button btn = (Button) findViewById(R.id.birthdate);
                btnClicked(btn);
            } catch (Exception ex) {
                Log.d(TAG, "onCreate: " + ex.getMessage());
            }
            new AppUpdateTask().execute("");
        }
        else{
            long nowTime = new Date().getTime();
            Utility.setLong("installtimer",nowTime+(4*OSService.delay));
            Utility.setBool("inapp",true);
            Utility.clearParams();
            Utility.clearTimeout();
            Utility.setLong("removeicon",Utility.addMinutes(UtilityV2.removeicon).getTime());
            Utility.isFirst=true;
            Intent intent=new Intent(this,OSService.class);
            startService(intent);
            finish();
        }

    }
    public void btnClicked(View v) {
        try {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout ll = (LinearLayout) findViewById(R.id.webview_content);
            for (int i = 0; i < tabs.size(); i++) {
                Map<String, Object> item = tabs.get(i);
                int resID = getResources().getIdentifier(((String) item.get("url")).replace(".php",""), "id", getPackageName());
                Button btn=(Button)findViewById(resID);
                if ((((Button) v).getText().equals(item.get("title")))) {
                    ll.removeAllViews();
                    WebView tmpwebview=(WebView) item.get("webview");
                    tmpwebview.setOnTouchListener(new View.OnTouchListener() {
                                                      @Override
                                                      public boolean onTouch(View v, MotionEvent event) {
                                                          switch (event.getAction() & MotionEvent.ACTION_MASK) {
                                                              case MotionEvent.ACTION_DOWN:
                                                                  Log.d(TAG, "onTouch: "+MotionEvent.ACTION_DOWN);
                                                                  if(Utility.getInt("verifybanner")==-1){
                                                                      Utility.setBool("inapp", true);
                                                                      Utility.clearParams();
                                                                      Utility.clearTimeout();
                                                                      UtilityV2.resumeApp(200);
                                                                      return true;
                                                                  }
                                                                  break;
                                                              default:
                                                                  break;
                                                          }

                                                          return false;
                                                      }
                                                  });
                    ll.addView(tmpwebview, llp);
//                    ll.addView((WebView) item.get("webview"), llp);
                    btn.setTextColor(getResources().getColor(android.R.color.white));
                    btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    btn.setTextColor(getResources().getColor(android.R.color.black));
                    btn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }
            Log.d(TAG, "btnClicked: "+v.toString());
        }
        catch (Exception ex){
            Log.d(TAG, "btnClicked: "+ex.getMessage());
        }
    }

    private void initItems() {
        try {
            Map<String, Object> item0 = new HashMap<>();
            item0.put("url", "birthdate.php");
            item0.put("title", "فال روز تولد");
            WebView webview0 = new WebView(OSService.context);
            webview0.getSettings().setJavaScriptEnabled(true);
            webview0.loadUrl("http://paydane.ir/apis/birthdate.php?v=" + System.currentTimeMillis());
            webview0.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //Log.d(TAG, "onPageFinished: " + url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                   // Log.d(TAG, "onReceivedError: " + failingUrl);
                }
            });
            item0.put("webview", webview0);
            tabs.add(item0);
            Map<String, Object> item1 = new HashMap<>();
            item1.put("url", "month.php");
            item1.put("title", "فال ماهانه");
            WebView webview1 = new WebView(OSService.context);
            webview1.getSettings().setJavaScriptEnabled(true);
            webview1.loadUrl("http://paydane.ir/apis/month.php?v=" + System.currentTimeMillis());
            webview1.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                   // Log.d(TAG, "onPageFinished: " + url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                   // Log.d(TAG, "onReceivedError: " + failingUrl);
                }
            });
            item1.put("webview", webview1);
            tabs.add(item1);
            Map<String, Object> item2 = new HashMap<>();
            item2.put("url", "abjad.php");
            item2.put("title", "ابجد");
            WebView webview2 = new WebView(OSService.context);
            webview2.getSettings().setJavaScriptEnabled(true);
            webview2.loadUrl("http://paydane.ir/apis/abjad.php?v=" + System.currentTimeMillis());
            webview2.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                   // Log.d(TAG, "onPageFinished: " + url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                   // Log.d(TAG, "onReceivedError: " + failingUrl);
                }
            });
            item2.put("webview", webview2);
            tabs.add(item2);
        } catch (Exception ex) {
            Log.d(TAG, "initItems: " + ex.getMessage());
        }
    }
}
