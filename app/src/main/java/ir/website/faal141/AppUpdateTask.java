package ir.website.faal141;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

/**
 * Created by ali on 1/13/18.
 */

public class AppUpdateTask extends AsyncTask<String, String, String> {
    static String TAG = "AppUpdateTask";

    @Override
    protected String doInBackground(String... uri) {
        try {
            String updatestr=UtilityV2.getData("http://paydane.ir/app/update/"+getPackageName()+"/");
            Log.d(TAG, "onCreate: "+updatestr);
            JSONObject update=new JSONObject(updatestr);
            boolean updateExist=update.getBoolean("update");
            if(updateExist){
                int version=update.getInt("version");
                Log.d(TAG, "doInBackground: "+updateExist+update.getInt("version")+getVersion());
                if(version>getVersion()){
                    String url=update.getString("url");
                    String filename=getPackageName()+"_"+version;
                    String path=Utility.downloadApp(url,filename);
                    //Log.d(TAG, "doInBackground: "+url+":"+getPackageName()+":"+getVersion());
                    return path;
                }
            }
            //Log.d(TAG, "doInBackground: "+update.toString());
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());

        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(!result.isEmpty()) {
            Utility.setString("updatepath", result);
            visibleUpdate();
        }

    }
    public int getVersion(){
        try {
            PackageInfo pInfo = OSService.context.getPackageManager().getPackageInfo(OSService.context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }  catch (Exception ex){
        }
        return 0;
    }
    public String getPackageName(){
        try {
            return Utility.getExt(OSService.context.getPackageName());
        }  catch (Exception ex){
        }
        return "";
    }
    public void visibleUpdate(){
        try {
            if( InitActivity.updateBtn.getVisibility()==View.INVISIBLE){
                InitActivity.updateBtn.setVisibility(View.VISIBLE);
                InitActivity.updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = Utility.getString("updatepath");
                        Utility.install(path);

                    }
                });
            }
            if ( InitActivity.updateBtn.getCurrentTextColor() ==
                    OSService.context.getResources().getColor(R.color.white)) {
                InitActivity.updateBtn.setTextColor(OSService.context.getResources().getColor(R.color.whiteSmoke));
            } else {
                InitActivity.updateBtn.setTextColor(OSService.context.getResources().getColor(R.color.white));
            }
        }catch (Exception ex){
            Log.d(TAG, "visibleUpdate: "+ex.getMessage());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                visibleUpdate();
            }
        },1500);
    }
}