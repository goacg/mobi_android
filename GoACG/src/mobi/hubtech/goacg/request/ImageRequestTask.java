package mobi.hubtech.goacg.request;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import mobi.hubtech.goacg.global.C;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageRequestTask extends AsyncTask<String, Integer, Bitmap> {
    
    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0]);
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, C.TIME_OUT_CONNECTION);
            HttpConnectionParams.setSoTimeout(httpParams, C.TIME_OUT_READ);
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = response.getEntity().getContent();
                Bitmap b = BitmapFactory.decodeStream(is);
                return b;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void onPostExecute(Bitmap result) {
        
    }
}
