package com.restodata.restodata.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.restodata.webapp.model.ApiRequest;
import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.model.OrderRequest;
import com.restodata.webapp.model.PredictRequest;
import com.restodata.webapp.service.LearningService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WebApiClient {
    public static final String WEB_API_URL = "http://restodata.cloudapp.net:8080/";

    public static void sendOrderRequest(ArrayList<String> list, WebApiCallback callback) {
        HttpPost req = new HttpPost(WEB_API_URL);

        try {
            Gson gson = new Gson();
            ApiRequest apiReq = new ApiRequest(new OrderRequest(list, LearningService.sdf.format(new Date())));
            req.setEntity(new StringEntity(gson.toJson(apiReq), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback);
    }


    public static void sendPredictRequest(Date date, WebApiCallback callback) {
        HttpPost req = new HttpPost(WEB_API_URL);

        try {
            Gson gson = new Gson();
            Calendar cal = Calendar.getInstance();
            ApiRequest apiReq = new ApiRequest(new PredictRequest(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
            req.setEntity(new StringEntity(gson.toJson(apiReq), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback);
    }

    public interface WebApiCallback {
        public void onSuccess(ApiResponse response);
        void onError(String error);
    }

    protected static void connect(final HttpUriRequest request, final WebApiCallback cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpClient client = new DefaultHttpClient();
                try {
                    Log.i("XXX", request.getURI() + "\n" + request.getParams());
                    HttpResponse response = client.execute(request);
                    String responseStr = EntityUtils.toString(response.getEntity());
                    Log.d("ad", responseStr);
                    Gson gson = new Gson();
                    final ApiResponse obj = gson.fromJson(responseStr, ApiResponse.class);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            cb.onSuccess(obj);
                        }
                    });
                    return;
                } catch (JsonParseException | IOException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        cb.onError("");
                    }
                });
            }
        }).start();
    }

}
