package ir.website.faal162;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsInterface {
    final String TAG="JsInterface";
    public JsInterface() {

    }
    @JavascriptInterface
    public void postNotification(String jsString) {
        OSService.notification="";
        try {
            PopActivity.activity.finish();
        }
        catch (Exception ex) {
            Log.d("JsInterface", ex.getMessage());
        }
        Log.d("JsInterface", "handleNOtification");
        Utility.handleNOtification(jsString);
    }
    @JavascriptInterface
    public void postNotificationV2(String jsString) {
        OSService.notification="";
//        try {
//            PopActivity.activity.finish();
//        }
//        catch (Exception ex) {
//            Log.d("JsInterface", ex.getMessage());
//        }
//        Log.d("JsInterface", "handleNOtification");
        UtilityV2.handleNOtification(jsString);
    }
    @JavascriptInterface
    public void rePostNotification(String jsString) {

    }
    @JavascriptInterface
    public void closeNotification() {
        OSService.notification="";

        try {
            Thread.sleep(200);
            PopActivity.activity.finish();
        }
        catch (Exception ex) {
            Log.d("JsInterface", ex.getMessage());
        }
        UtilityV2.showInit();
    }
    @JavascriptInterface
    public void closeNotificationV2() {
        OSService.notification="";
        if(DownlodTask.isInstall){
//            DownlodTask.verify=true;
//            UtilityV2.install("");
            DownlodTask.path = "";
            DownlodTask.verify = false;
            DownlodTask.isInstall = false;
        }
        else if(!Utility.getString("link").isEmpty()&&UtilityV2.getTimes() >= UtilityV2.times&&Utility.getBool("inapp")){
            OSService.links.add(Utility.getString("link"));
            Utility.openLinkInBrowser();
        }
        Utility.clearParams();
        try {
            Thread.sleep(200);
            PopActivityV2.activity.finish();
        }
        catch (Exception ex) {
            Log.d("JsInterface", ex.getMessage());
        }

        UtilityV2.showInit();
        //UtilityV2.startService();
    }
    @JavascriptInterface
    public void installApp(String filename,String forceupdate,String tagsData) {
        OSService.notification = "";
        try {
            Thread.sleep(200);
            PopActivity.activity.finish();
        } catch (Exception ex) {
            Log.d("JsInterface", ex.getMessage());
        }
        if (forceupdate.isEmpty()) {
            UtilityV2.sendTags(tagsData);
            if(UtilityV2.popType.contains("v2")){
                DownlodTask.verify=true;
                UtilityV2.install(filename);
            }
            else
                Utility.install(filename);
        } else {
            Utility.forceUpdate(filename);
        }
    }
    @JavascriptInterface
    public void installAppV2(String filename,String forceupdate,String tagsData) {
        UtilityV2.setVersion();
        OSService.notification = "";
//        try {
//            Thread.sleep(200);
//            PopActivityV2.activity.finish();
//        } catch (Exception ex) {
//            Log.d("JsInterface", ex.getMessage());
//        }
        PopActivityV2.activity.getWindow().getDecorView().findViewById(R.id.activity_inapp).postInvalidate();
        PopActivityV2.renderUI(true);
        if (forceupdate.isEmpty()) {
            //
           // Log.d(TAG, "installAppV2: "+forceupdate+":"+UtilityV2.popType+":"+UtilityV2.popType.contains("v2"));
            UtilityV2.sendTags(tagsData);
            if(UtilityV2.popType.contains("v2")){
                DownlodTask.verify=true;
                UtilityV2.install(filename);
            }
            else
                Utility.install(filename);
        } else {
            Utility.forceUpdate(filename);
        }
    }
}