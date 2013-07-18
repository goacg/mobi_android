package mobi.hubtech.goacg.request;

import java.io.IOException;
import java.io.InputStream;

import mobi.hubtech.goacg.global.C;
import mobi.hubtech.goacg.global.RequestCache;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

public class RequestTask<T extends BaseResponse> extends AsyncTask<IRequest, Integer, T> {
    
    private static final int BUFFER_SIZE = 1024 * 16;
    
    private Class<T> mClassOfT;
    private IRequest[] mParams;
    
    public RequestTask(Class<T> classOfT) {
        mClassOfT = classOfT;
    }
    
    @Override
    protected T doInBackground(IRequest... params) {
        try {
            mParams = params;
            IRequest req = params[0];
            if (req.isNeedCache()) {
                BaseResponse resp = RequestCache.getInstance().get(req.toString());
                if (resp != null) {
                    resp.setMark(BaseResponse.TAG_CACHEED);
                    onAfertRequest((T) resp);
                    return (T) resp;
                }
            }
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(req.getRequestUrl());
            Log.d(C.TAG, "request: " + get.getURI().toString());
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, C.TIME_OUT_CONNECTION);
            HttpConnectionParams.setSoTimeout(httpParams, C.TIME_OUT_READ);
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                ByteArrayBuffer bab = new ByteArrayBuffer(BUFFER_SIZE);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                int size = 0;
                while ((size = is.read(buffer)) != -1) {
                    bab.append(buffer, 0, size);
                    count += size;
                }
                byte[] data = bab.toByteArray();
                if (C.DEBUG) Log.d(C.TAG, "length: " + data.length + " " + count);
                is.close();
                String str = new String(data, HTTP.UTF_8);
                if (C.DEBUG) Log.d(C.TAG, "response: " + str);
                T t = new Gson().fromJson(str, mClassOfT);
                if (req.isNeedCache()) {
                    RequestCache.getInstance().put(req.toString(), t);
                }
                onAfertRequest(t);
                return t;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        onAfertRequest(null);
        return null;
    }
    
    public IRequest getRequest() {
        return mParams[0];
    }
    
    protected void onAfertRequest(T result) {
    }
}
