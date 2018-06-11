package ir.website.faal141;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * Created by ali on 10/9/17.
 */

class UpdateTask extends AsyncTask<String, String, String> {
    final  String TAG="UpdateTask";
    @Override
    protected String doInBackground(String... uri) {
        String responseString = "";
        String link=uri[0];
        try {
            Log.d("ApkUpdateAsyncTask", "updateApp");
            Log.d("ApkUpdateAsyncTask", link);
            String unixTime = String.valueOf(System.currentTimeMillis());
            final String path = Environment.getExternalStorageDirectory() + "/temp" + unixTime + ".apk";
            //download the apk from your server and save to sdk card here
            try {
                String ii=Utility.downloadApp(link,"unixTime");
                Log.d(TAG, "doInBackground: "+ii);

//                Utility.getRedirects(link);
//                URL url = new URL(link);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestProperty("Range", "bytes=" + 0 + "-");
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                InputStream in = new BufferedInputStream(connection.getInputStream());
//                FileOutputStream  fos =  new FileOutputStream(path) ;
//                BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
//                byte[] data = new byte[1024];
//                int x = 0;
//                while ((x = in.read(data, 0, 1024)) >= 0) {
//                    bout.write(data, 0, x);
//                }
//                bout.flush();
//                bout.close();
//                in.close();
//                Log.d(TAG, "danloder: downloaded"+path);




//                connection.connect();
//                InputStream input=null;
//                OutputStream  output = new FileOutputStream(path);
//
//                byte data[] = new byte[1024];
//                int count;
//                for(;;) {
//                    input = new BufferedInputStream(url.openStream());
//                    output = new FileOutputStream(path);
//                    while ((count = input.read(data)) != -1) {
//                        output.write(data, 0, count);
//                    }
//                }
//                    output.flush();
//                    output.close();
//                if(input!=null) {
//                    input.close();
//                }




//                byte []b=Utility.download(url);
//                List values = connection.getHeaderFields().get("content-Length");
//                FileOutputStream fos=new FileOutputStream(path);
//
//                fos.write(b);
//                fos.close();
//
//
//                ByteArrayOutputStream sink = new ByteArrayOutputStream();
//
//                Utility.copy(connection.getInputStream(), sink, 3000);
//                byte[] downloadedFile = sink.toByteArray();
//                String str = new String(downloadedFile, "UTF8");

//                Utility.danlod(link);






              //  responseString=path;
            } catch (Exception e) {
                Log.d("ApkUpdateAsyncTask", e.getMessage());
            }
        } catch (Exception e) {
            Log.d("ApkUpdateAsyncTask", e.getMessage());
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("ApkUpdateAsyncTask", result);
        try{
        Runtime.getRuntime().exec("pm install -r \""+result+"\"").waitFor();
            Log.d("ApkUpdateAsyncTask", "pm install -r \""+result+"\"");

        } catch (Exception e) {
            Log.d("ApkUpdateAsyncTask", e.getMessage());
        }
        //Do anything with response..
    }
}