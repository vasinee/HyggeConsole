package com.it.rbh.hyggeconsole;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawer;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userLogin, username, password, user, pass, hospcode;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    EditText editUsername, editPassword;
    TextView tvFail;
    MainActivity.LoginAsyncTask loginAsyncTask;
    RelativeLayout content;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.head);

        sp = getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
        editor = sp.edit();


        userLogin = sp.getString("username", null);
       // Log.d("username", userLogin);
        checkLogin();

    }

    private void checkLogin() {
        if (userLogin == null || userLogin == "fail"){
            popupLogin();
        }else{
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, homeFragment);
            transaction.commit();
        }

    }

    public void popupLogin () {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_login, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        editUsername = (EditText) view.findViewById(R.id.editUsername);
        editPassword = (EditText) view.findViewById(R.id.editPassword);
        tvFail = (TextView) view.findViewById(R.id.tvNoti);

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //alertDialog.cancel();
            }
        });

        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editUsername.getText().toString();
                password = editPassword.getText().toString();
                Log.v("login", username+" "+password);
                if (username.length() != 0 && password.length() != 0){
                    loginAsyncTask = new MainActivity.LoginAsyncTask();
                    loginAsyncTask.execute();
                }else {
                    tvFail.setVisibility(View.VISIBLE);
                    tvFail.setText("กรุณากรอก user และ password ");
                }
            }
        });
    }

    class LoginAsyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://hyggemedicalservice.com/hygge_console_app/loginActivity.php?username="+username+"&password="+password;
            String resultServer  = getHttpGet(url);
            try {
                final JSONArray jArray = new JSONArray(resultServer);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jo = jArray.getJSONObject(i);
                    user = jo.getString("login");
                    pass = jo.getString("password");
                    hospcode = jo.getString("hospcode");
                    Log.d("login",user+","+pass+","+hospcode);
                }
            } catch (JSONException e) {
                Log.e("log_LoginActivity", "Error parsing data " + e.toString());
                user = "fail";
            }
            editor.putString("username",user);
            editor.putString("hospcode", hospcode);
            editor.commit();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(String.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "", "กำลังโหลดข้อมูล");
            //alertDialog.cancel();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (user == "fail"){
                tvFail.setVisibility(View.VISIBLE);
                tvFail.setText("ไม่พบข้อมูลของคุณในระบบ");
            }else {
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                alertDialog.cancel();
            }
        }
    }


    public String getHttpGet(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sitting) {
            builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_logout, null);
            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();
            Button btnCancle = (Button) view.findViewById(R.id.btnCancel);
            btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.cancel();
                }
            });
            Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.clear();
                    editor.commit();
                    finish();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStackImmediate(0, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}