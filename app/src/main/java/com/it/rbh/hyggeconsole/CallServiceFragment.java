package com.it.rbh.hyggeconsole;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CallServiceFragment extends Fragment {
    ConstraintLayout conCallService;
    ListView lvQue;
    CallServiceCustomAdapter adapter;
    CallServiceFragment.CallServiceAsyncTask callServiceAsyncTask;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String hospcode, depcode, id, showQue, cid, timestamp;
    ArrayList<String> arrayID, arrayShowQue, arrayTime, arrayCID;
    PopupWindow menuPopup;
    View menuView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_callservice, container, false);

        sp = getActivity().getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
        editor = sp.edit();
        hospcode = sp.getString("hospcode", null);
        depcode = sp.getString("depcode", null);

        arrayCID = new ArrayList<>();
        arrayID = new ArrayList<>();
        arrayTime = new ArrayList<>();
        arrayShowQue = new ArrayList<>();

        conCallService = (ConstraintLayout)rootView.findViewById(R.id.conCallService);
        lvQue = (ListView)rootView.findViewById(R.id.lvQue);

        callServiceAsyncTask = new CallServiceFragment.CallServiceAsyncTask();
        callServiceAsyncTask.execute();

        return rootView;
    }

    class CallServiceAsyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {

            String urlDep = "http://hyggemedicalservice.com/hygge_console_app/queDepartment.php?hospcode="+hospcode+"&depcode="+depcode;
            String resultServerDep  = getHttpGet(urlDep);
            try {
                final JSONArray jArray = new JSONArray(resultServerDep);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jo = jArray.getJSONObject(i);
                    id = jo.getString("id");
                    showQue = jo.getString("showQue");
                    cid = jo.getString("cid");
                    timestamp = jo.getString("timestamp");
                    arrayID.add(id);
                    arrayShowQue.add(showQue);
                    arrayCID.add(cid);
                    arrayTime.add(timestamp);

                }
                //Log.d("arrayID", arrayID.toString());
                //Log.d("arrayShowQue", arrayShowQue.toString());
            } catch (JSONException e) {
                Log.e("log_queDepartment", "Error parsing data " + e.toString());
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
            conCallService.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (arrayShowQue.size() > 0){
                conCallService.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                adapter = new CallServiceCustomAdapter(getActivity(), arrayID, arrayShowQue, arrayCID,arrayTime, transaction);
                lvQue.setAdapter(adapter);
            }else {
                conCallService.setVisibility(View.GONE);
                LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                menuView = layoutInflater.inflate(R.layout.popup, null);
                menuPopup = new PopupWindow(menuView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                menuPopup.showAtLocation(menuView, Gravity.CENTER, 0, 0);

                Button btnClose = (Button) menuView.findViewById(R.id.btn_close);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        menuPopup.dismiss();
                        getActivity().getSupportFragmentManager().popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });
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

    public void onPause() {
        super.onPause();
        if (menuPopup != null && menuPopup.isShowing()) {
            menuPopup.dismiss();
        }
    }

}
