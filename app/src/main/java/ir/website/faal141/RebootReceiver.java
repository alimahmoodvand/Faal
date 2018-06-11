package ir.website.faal141;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

/**
 * Created by ali on 9/26/17.
 */

public class RebootReceiver extends BroadcastReceiver {

    private static final String TAG = "RebootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, "onReceive: "+new Date());
        Intent serviceintent=new Intent(context,OSService.class);
        context.startService(serviceintent);
    }
}