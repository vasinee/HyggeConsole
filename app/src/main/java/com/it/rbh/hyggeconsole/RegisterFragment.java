package com.it.rbh.hyggeconsole;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class RegisterFragment extends Fragment {
    EditText inputCID;
    Button btnAdd;
    String cid, depcode, hospcode;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    TextView tvNoti;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        sp = getActivity().getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
        editor = sp.edit();
        depcode = sp.getString("depcode", null);
        hospcode = sp.getString("hospcode", null);
        tvNoti = (TextView)rootView.findViewById(R.id.tvNoti);


        inputCID = (EditText) rootView.findViewById(R.id.inputCID);


        btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cid = inputCID.getText().toString();
                if (cid.length() != 13){
                    tvNoti.setVisibility(View.VISIBLE);
                }else{
                    Log.v("RegisterFragment", hospcode+","+depcode+","+cid);
                    RegisterFragment.RegistersyncTask registersyncTask;
                    registersyncTask = new RegisterFragment.RegistersyncTask();
                    registersyncTask.execute();
                }
            }
        });

        return rootView;
    }

    class RegistersyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {
            try{
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("hospcode",hospcode));
                nameValuePairs.add(new BasicNameValuePair("depcode",depcode));
                nameValuePairs.add(new BasicNameValuePair("cid",cid));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://hyggemedicalservice.com/hygge_console_app/registerFragment.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                httpclient.execute(httppost);
            }catch(Exception e){
                Log.d("log_err", "Error in http connection " + e.toString());
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

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

        }
    }
}
