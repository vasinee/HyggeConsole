package com.it.rbh.hyggeconsole;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class SelectStationFragment extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String id, station, selectQue;
    Button station1, station2,station3, station4, station5;
    SelectStationFragment.SelectStationsyncTask selectStationsyncTask;
    TextView tvCallQue;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_selectstation, container, false);

        selectStationsyncTask = new SelectStationFragment.SelectStationsyncTask();
        sp = getActivity().getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
        editor = sp.edit();
        id = sp.getString("id", null);
        selectQue = sp.getString("selectQue", null);
        Log.v("id/selectQue", id+"/"+selectQue);

        tvCallQue = (TextView)rootView.findViewById(R.id.tvCallQue);
        tvCallQue.setText(selectQue);

        station1 = (Button)rootView.findViewById(R.id.station1);
        station1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                station = "STAT01";
                selectStationsyncTask.execute();
            }
        });

        station2 = (Button)rootView.findViewById(R.id.station2);
        station2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                station = "STAT02";
                selectStationsyncTask.execute();
            }
        });

        station3 = (Button)rootView.findViewById(R.id.station3);
        station3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                station = "STAT03";
                selectStationsyncTask.execute();
            }
        });

        station4 = (Button)rootView.findViewById(R.id.station4);
        station4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                station = "STAT04";
                selectStationsyncTask.execute();
            }
        });

        station5 = (Button)rootView.findViewById(R.id.station5);
        station5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                station = "STAT05";
                selectStationsyncTask.execute();
            }
        });

        return rootView;
    }
    class SelectStationsyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {
            try{
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id",id));
                nameValuePairs.add(new BasicNameValuePair("station",station));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://hyggemedicalservice.com/hygge_console_app/updateSelectStation.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpclient.execute(httppost);
            }catch(Exception e){
                Log.d("selectStation", "Error in http connection " + e.toString());
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
            getFragmentManager().popBackStackImmediate();
        }
    }

}
