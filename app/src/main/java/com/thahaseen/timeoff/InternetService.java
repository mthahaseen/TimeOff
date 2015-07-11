package com.thahaseen.timeoff;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class InternetService extends Service {

    private int interval;
    private Timer mTimer = null;
    DatabaseHandler db = new DatabaseHandler(this);
    ConnectionDetector cd = new ConnectionDetector(this);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        interval = 6;

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, interval * 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    class CheckForConnection extends TimerTask{
        @Override
        public void run() {
            if(cd.isConnectingToInternet()){
                getUnSyncedDataToAdd();
                mTimer.cancel();
                stopSelf();
            }
        }
    }

    private void getUnSyncedDataToAdd(){
        List<TimeOffItem> items = db.getUnSyncedData();
        if(items.size() != 0) {
            for (TimeOffItem item : items) {
                addTimeOff(item);
            }
        }else{
            mTimer.cancel();
            stopSelf();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void addTimeOff(final TimeOffItem item){
        String URL_FEED = null;
        try {
            URL_FEED = "http://restoranapp.com/tradly/addtimeoff.php?tag=addtimeoff&" + "&username="+ item.getUsername()+
                    "&name="+ URLEncoder.encode(item.getName(), "UTF-8") + "&dept="+ URLEncoder.encode(item.getDept(), "UTF-8") +
                    "&location="+ URLEncoder.encode(item.getLocation(), "UTF-8")+ "&remarks="+ URLEncoder.encode(item.getRemarks(), "UTF-8") +
                    "&reason="+ URLEncoder.encode(item.getReason(), "UTF-8") + "&timelate="+ item.getTimelate();
        }catch (UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        Log.i("URL TIME OFF", "URL_TIME_OFF = " + URL_FEED);
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                URL_FEED, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getInt("success") != 0) {
                            db.updateDataSyncDone(item.getId());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();}
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }
}