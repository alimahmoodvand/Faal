package ir.website.faal141;


import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends Activity implements ActionBar.TabListener {
    private static final String TAG = "HomeActivityLog";
    private static Activity activity;
    private WebView mWebView;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    public static SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static int counter=0;
    private static List<Map<String, Object>> tabs = new ArrayList<Map<String, Object>>();
    public static  String[] urls = new String[] {"birthdate.php","month.php","abjad.php"};
    public static  WebView[] WebView = new WebView[urls.length];
    public static View root[]=new View[urls.length];
    public static boolean loaded[]=new boolean[]{false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //start service for popup
        OSService.context=this;
        activity=this;
        initItems();
        Utility.setBool("inapp",false);
        Utility.clearParams();
        Utility.clearTimeout();
        Utility.setLong("removeicon",Utility.addMinutes(UtilityV2.removeicon).getTime());
        Utility.isFirst=true;
        Intent intent=new Intent(this,OSService.class);
        startService(intent);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.actionbar);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.middleBlue));
        }
        OSService.context=this;
//        setContentView(R.layout.activity_home);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
//        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        mViewPager.setOffscreenPageLimit(tabs.size());

        for (int i =0; i <  tabs.size(); i++) {
            Map<String, Object> item=tabs.get(i);
            Log.d(TAG, "onCreate: "+item.get("title"));
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(String.valueOf(item.get("title")))
                            .setTabListener(this));
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
                    Log.d(TAG, "onPageFinished: " + url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.d(TAG, "onReceivedError: " + failingUrl);
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
                    Log.d(TAG, "onPageFinished: " + url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.d(TAG, "onReceivedError: " + failingUrl);
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
                    Log.d(TAG, "onPageFinished: " + url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.d(TAG, "onReceivedError: " + failingUrl);
                }
            });
            item2.put("webview", webview2);
            tabs.add(item2);
        }catch (Exception ex){
            Log.d(TAG, "initItems: "+ex.getMessage());
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Map<String, Object> item=tabs.get(tab.getPosition());

        mViewPager.setCurrentItem(tab.getPosition());
        Log.d(TAG, "onTabSelected: "+tab.getPosition()+item.get("url")+tab.getText());
        //((WebView)item.get("webview")).loadUrl("http://paydane.ir/apis/"+item.get("url")+"?v=" + System.currentTimeMillis());
        //webview0.loadUrl("http://paydane.ir/apis/birthdate.php?v=" + System.currentTimeMillis());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            Log.d(TAG, "newInstance: "+sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d(TAG, "newInstance: "+counter+" - - http://paydane.ir/apis/" + urls[counter]+"?v="+System.currentTimeMillis());
            View rootView=new View(OSService.context);
//            if(counter==0)
//                rootView = inflater.inflate(R.layout.fragment_home0, container, false);
//            else
//                rootView = inflater.inflate(R.layout.fragment_home1, container, false);
            if(counter<urls.length) {
//                WebView webview =new WebView(OSService.context);
//                //       (WebView)rootView.findViewById(R.id.app_webview) ;
//                webview.getSettings().setJavaScriptEnabled(true);
//                webview.loadUrl("http://paydane.ir/apis/" + urls[counter]+"?v="+System.currentTimeMillis());
//                webview.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//                        //HomeActivity.checkVIew(view,url);
//                        loaded[counter]=true;
//                        Log.d(TAG, "onPageFinished: "+url);
//                    }
//                    @Override
//                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                        //HomeActivity.checkVIew(view,failingUrl);.
//                        loaded[counter]=true;
//                        Log.d(TAG, "onReceivedError: "+failingUrl);
//                    }
//                });
//                WebView[counter]=webview;
//                root[counter]=rootView;
//                int count=0;
//                while(!loaded[counter]){
//                    Log.d(TAG, "onCreateView: "+count++);
//                }
                Map<String, Object> item=tabs.get(counter);
                try {
                    //((ViewGroup)rootView).addView((View) item.get("webview"));
                }catch (Exception ex){
                    Log.d(TAG, "onCreateView: "+ex.getMessage());
                }
            counter++;

        }
            return rootView;
        }
    }
    private static void checkVIew(WebView view, String url) {
        for(int i=0;i<WebView.length;i++ ) {
            if(WebView[i]==view){
                Log.d(TAG, "checkVIew: "+i+":"+url);
                ((ViewGroup)root[i]).addView(view);
            }
        }
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "فال روز تولد";
                case 1:
                    return "فال ماهانه";
                case 2:
                    return "ابجد";
            }
            return null;
        }
    }
}
