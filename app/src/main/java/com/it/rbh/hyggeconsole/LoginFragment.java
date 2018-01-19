package com.it.rbh.hyggeconsole;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginFragment extends FragmentActivity {


    SharedPreferences sp;
    EditText editUsername, editPassword;
    LoginAsyncTask loginAsyncTask;
    String username, password, user, pass, hospcode;
    SharedPreferences.Editor editor;
    TextView tvFail;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        sp = getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
        editor = sp.edit();

        editUsername = (EditText) findViewById(R.id.editUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);
        tvFail = (TextView) findViewById(R.id.tvNoti);

        TextView Cancel = (TextView) findViewById(R.id.Cancel);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //alertDialog.cancel();
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editUsername.getText().toString();
                password = editPassword.getText().toString();
                Log.v("login", username+" "+password);
                if (username.length() != 0 && password.length() != 0){
                    loginAsyncTask = new LoginAsyncTask();
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
            //progressDialog = ProgressDialog.show(MainActivity.this, "", "กำลังโหลดข้อมูล");
            //alertDialog.cancel();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // progressDialog.dismiss();
            if (user == "fail"){
                tvFail.setVisibility(View.VISIBLE);
                tvFail.setText("ไม่พบข้อมูลของคุณในระบบ");
            }else {
                Intent myIntent = new Intent(LoginFragment.this, MainActivity.class);
                startActivity(myIntent);
                finish();
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

}
