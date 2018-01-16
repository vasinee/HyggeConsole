package com.it.rbh.hyggeconsole;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String hospcodeLogin, depname, depcode;
    ConstraintLayout homeLayout;
    HospitalDepCustomAdapter adapter;
    ListView lvDepartment;
    ArrayList<String> arrayDepName, arrayDepCode;
    HomeAsyncTask homeAsyncTask;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.hygge_head);

        sp = getActivity().getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
        editor = sp.edit();

        hospcodeLogin = sp.getString("hospcode", null);
        Log.d("hospcodeLogin", hospcodeLogin);

        homeLayout = (ConstraintLayout) rootView.findViewById(R.id.homeLayout);
        lvDepartment = (ListView) rootView.findViewById(R.id.lvDepartment);

        arrayDepName = new ArrayList<>();
        arrayDepCode = new ArrayList<>();

        homeAsyncTask = new HomeFragment.HomeAsyncTask();
        homeAsyncTask.execute();

        return  rootView;
    }

    class HomeAsyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {

            String urlDep = "http://hyggemedicalservice.com/hygge_console_app/hospitalDepartment.php?hospcode="+hospcodeLogin;
            String resultServerDep  = getHttpGet(urlDep);
            try {
                final JSONArray jArray = new JSONArray(resultServerDep);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jo = jArray.getJSONObject(i);
                    depcode = jo.getString("department_his");
                    depname= jo.getString("department_name");
                    arrayDepName.add(depname);
                    arrayDepCode.add(depcode);
                }
                Log.d("arrayDepName", arrayDepName.toString());
                Log.d("arrayDepCode", arrayDepCode.toString());
            } catch (JSONException e) {
                Log.e("log_hospitalDepartment", "Error parsing data " + e.toString());
            }
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
            progressDialog = ProgressDialog.show(getActivity(), "", "กำลังโหลดข้อมูล");
            homeLayout.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            homeLayout.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            adapter = new HospitalDepCustomAdapter(getActivity(), arrayDepName, arrayDepCode, transaction);
            lvDepartment.setAdapter(adapter);
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
