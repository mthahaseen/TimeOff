package com.thahaseen.timeoff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        if(!(preferences.getString("username","").equals(""))){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            Button btnLogin = (Button) findViewById(R.id.btnLogin);
            final TextView txtUsername = (TextView) findViewById(R.id.txtUsername);
            final TextView txtPassword = (TextView) findViewById(R.id.txtPassword);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (txtUsername.getText().toString().trim().length() == 0 || txtUsername.getText().toString().trim().equals("")) {
                        Toast.makeText(LoginActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    } else if (txtPassword.getText().toString().length() == 0 || txtPassword.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    } else {
                        if (txtUsername.getText().toString().trim().equals("user123") && txtPassword.getText().toString().trim().equals("password")) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", txtUsername.getText().toString().trim());
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Username and Password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
