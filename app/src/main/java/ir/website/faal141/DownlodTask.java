package ir.website.faal141;

import android.os.AsyncTask;

/**
 * Created by ali on 3/12/18.
 */

public class DownlodTask  extends AsyncTask<String, String, String> {
    private static final String TAG = "DownlodTask";
    public static String path = "";
    public static Boolean verify = false;
    public static Boolean isInstall = false;
    private String filename="";
    DownlodTask(String fname){
        this.filename=fname;
    }
    @Override
    protected String doInBackground(String... uri) {
        isInstall=true;
        //Log.d(TAG, "doInBackground: "+new Date());
        String path=Utility.downloadApp(
                uri[0],
//                String.valueOf(System.currentTimeMillis())
                this.filename.isEmpty()?String.valueOf(System.currentTimeMillis()):this.filename
        );
//        Log.d(TAG, "doInBackground: "+new Date());
//
//        Log.d(TAG, "doInBackground: "+path);
        return path;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this.path=result;
        UtilityV2.install("");
    }
}