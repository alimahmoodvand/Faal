package ir.website.faal141;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ali on 9/26/17.
 */

public class ExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private static final String TAG = "ExceptionHandler";
    private Context myContext;

    public ExceptionHandler(Context context) {
        myContext = context;
    }
    public void uncaughtException(Thread thread, Throwable exception) {

        Log.d(TAG, "uncaughtException: "+exception.getMessage());
        try {
            if(myContext==null&&OSService.context!=null){
                myContext=OSService.context;
            }
            Thread.sleep(2000);
            Intent service = new Intent(myContext, OSService.class);
            myContext.startService(service);
        } catch (InterruptedException e) {
            uncaughtException(thread,exception);
        }
    }
}