package ir.website.faal141;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by ali on 1/13/18.
 */

public class InAppTask extends AsyncTask<String, String, String> {
    static String TAG = "InAppTask";

    @Override
    protected String doInBackground(String... uri) {
        Log.d(TAG, "doInBackground: " + PopActivityV2.isRun + ":" + UtilityV2.getTimes() + ":" + UtilityV2.times + ":" + Utility.getBool("inapp"));
        String responseString = "";
        Utility.clearParams();
        try {
            PopActivityV2.progress.cancel();
            PopActivityV2.progress.dismiss();
        } catch (Exception ex) {
            Log.d(TAG, "doInBackground: "+ex.getMessage());
        }
        try {
            if (PopActivityV2.isRun)
                return responseString;
            if (UtilityV2.getTimes() >= UtilityV2.times || !Utility.getBool("inapp")) {
                Utility.setBool("inapp", false);
                try {
                    PopActivityV2.activity.finish();
                } catch (Exception ex) {
                }
            } else {
                try {
                    PopActivityV2.activity.finish();
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    Log.d(TAG, "doInBackground: "+ex.getMessage());
                }
                String operator = UtilityV2.getSmsGateway();
                OSService.notification = UtilityV2.getData("http://paydane.ir/onesignal/files/faal-" + operator + ".txt");
                if(!OSService.notification.isEmpty()
                        &&UtilityV2.isActive()
                ) {
                    UtilityV2.setVersion();
                    UtilityV2.checkInstal();
                    UtilityV2.setMessageFix();

                    try {
                        Intent serviceIntent = new Intent(OSService.context, PopActivityV2.class);
                        serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        UtilityV2.setTimes();
                        Utility.setInt("verifybanner",-1);
                        OSService.context.startActivity(serviceIntent);
                        return responseString;

                    } catch (Exception e) {
                        Log.d(TAG, "doInBackground: " + e.getMessage());
                    }
                }
                UtilityV2.setTimes();

                Utility.setInt("verifybanner",1);
                UtilityV2.showInit();
            }
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());

        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}