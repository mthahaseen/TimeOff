package com.thahaseen.timeoff;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    DatabaseHandler db = new DatabaseHandler(this);
    ConnectionDetector cd = new ConnectionDetector(this);
    EditText edName;
    EditText edDept;
    EditText edLocation;
    EditText edReason;
    EditText edRemarks;
    EditText edTimeLate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        hideKeyboard();
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Button btnAddTimeOff = (Button) findViewById(R.id.btnAddTimeOff);
        edName = (EditText) findViewById(R.id.txtName);
        edDept = (EditText) findViewById(R.id.txtDept);
        edLocation = (EditText) findViewById(R.id.txtLocation);
        edReason = (EditText) findViewById(R.id.txtReason);
        edRemarks = (EditText) findViewById(R.id.txtRemarks);
        edTimeLate = (EditText) findViewById(R.id.txtTimeLate);
        btnAddTimeOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(validateTimeOffDetails()){
                    if(cd.isConnectingToInternet()){
                        //add the time off details to remote db
                        addTimeOff();
                    } else{
                        //Start service to listen for internet connection availability
                        if(!isMyServiceRunning(InternetService.class)){
                            addUnSyncDataToDB();
                            startService(new Intent(MainActivity.this,InternetService.class));
                            clearAllValues();
                            hideKeyboard();
                        }else {
                            addUnSyncDataToDB();
                            clearAllValues();
                            hideKeyboard();
                        }
                    }
                }
            }
        });
    }
    private void addUnSyncDataToDB(){
        TimeOffItem item = new TimeOffItem();
        item.setUsername(preferences.getString("username",""));
        item.setName(edName.getText().toString().trim());
        item.setDept(edDept.getText().toString().trim());
        item.setLocation(edLocation.getText().toString().trim());
        item.setReason(edReason.getText().toString().trim());
        item.setRemarks(edRemarks.getText().toString().trim());
        item.setTimelate(Integer.parseInt(edTimeLate.getText().toString().trim()));
        item.setDatasync("N");
        db.addTimeOff(item);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public Boolean validateTimeOffDetails(){
        if (edName.getText().toString().trim().length() == 0 || edName.getText().toString().trim().equals("")) {
            Toast.makeText(MainActivity.this, "Enter your full name", Toast.LENGTH_SHORT).show();
            edName.requestFocus();
            return false;
        } else if (edDept.getText().toString().length() == 0 || edDept.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Enter Department", Toast.LENGTH_SHORT).show();
            edDept.requestFocus();
            return false;
        } else if (edLocation.getText().toString().length() == 0 || edLocation.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Enter Location", Toast.LENGTH_SHORT).show();
            edLocation.requestFocus();
            return false;
        } else if (edReason.getText().toString().length() == 0 || edReason.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Enter reason for time off", Toast.LENGTH_SHORT).show();
            edReason.requestFocus();
            return false;
        } else if (edTimeLate.getText().toString().length() == 0 || edTimeLate.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Enter time off in minutes", Toast.LENGTH_SHORT).show();
            edTimeLate.requestFocus();
            return false;
        }
        return true;
    }
    public void addTimeOff(){
        final ProgressDialog pDialog;
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Adding your time off. Please wait..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        String URL_FEED = null;
        try {
           URL_FEED = "http://restoranapp.com/tradly/addtimeoff.php?tag=addtimeoff&" + "&username="+preferences.getString("username","")+
                   "&name="+ URLEncoder.encode(edName.getText().toString().trim(), "UTF-8") + "&dept="+ URLEncoder.encode(edDept.getText().toString().trim(), "UTF-8") +
                   "&location="+ URLEncoder.encode(edLocation.getText().toString().trim(), "UTF-8")+ "&remarks="+ URLEncoder.encode(edRemarks.getText().toString().trim(), "UTF-8") +
                   "&reason="+ URLEncoder.encode(edReason.getText().toString().trim(), "UTF-8") + "&timelate="+ edTimeLate.getText().toString().trim();
        }catch (UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        Log.i("URL TIME OFF", "URL_TIME_OFF = " + URL_FEED);
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                URL_FEED, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                if (response != null) {
                    try {
                        if (response.getInt("success") != 0) {
                            TimeOffItem item = new TimeOffItem();
                            item.setUsername(preferences.getString("username",""));
                            item.setName(edName.getText().toString().trim());
                            item.setDept(edDept.getText().toString().trim());
                            item.setLocation(edLocation.getText().toString().trim());
                            item.setReason(edReason.getText().toString().trim());
                            item.setRemarks(edRemarks.getText().toString().trim());
                            item.setTimelate(Integer.parseInt(edTimeLate.getText().toString().trim()));
                            item.setDatasync("Y");
                            db.addTimeOff(item);
                            pDialog.dismiss();
                            clearAllValues();
                            hideKeyboard();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Oops, something went wrong. Kindly try adding again!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();}
                }else {
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void clearAllValues(){
        edName.setText("");
        edDept.setText("");
        edLocation.setText("");
        edReason.setText("");
        edRemarks.setText("");
        edTimeLate.setText("");
        edName.requestFocus();
    }
}
