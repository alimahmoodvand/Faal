package ir.website.faal141;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by ali on 9/26/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            OSService.context=context;
            if (intent.getAction().equals(SMS_RECEIVED)) {
                Bundle bundle = intent.getExtras();
//            Log.d("SmsReceivera", intent.getAction());
//            if (bundle!=null)
                {
                    String myshortcode = Utility.getString("myshortcode");
                    Object[] pdusObj;
                    try {
                        pdusObj = (Object[]) bundle.get("pdus");
                    } catch (Exception ex) {
                        pdusObj = new Object[0];
                    }
                    if (myshortcode != "") {
                        for (int i = 0; i < pdusObj.length; i++) {
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();
                            if (senderNum.contains(myshortcode)) {
                                UtilityV2.checkSMS(message);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex){
            Log.d(TAG, "onReceive: "+ex.getMessage());
        }
    }
}