package com.it.rbh.hyggeconsole;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CallServiceCustomAdapter extends BaseAdapter
{
    LayoutInflater mInlfater;
    ArrayList arrayCID, arrayTime, arrayShowQue, arrayID;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;
    FragmentTransaction transaction;

    public CallServiceCustomAdapter(Activity context, ArrayList<String> arrayID, ArrayList<String> arrayShowQue,
                                    ArrayList<String> arrayCID, ArrayList<String> arrayTime, FragmentTransaction transaction)
    {

        this.transaction = transaction;
        this.context = context;
        mInlfater = context.getLayoutInflater();
        this.arrayCID = arrayCID;
        this.arrayShowQue = arrayShowQue;
        this.arrayTime = arrayTime;
        this.arrayID = arrayID;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayID.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }



    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if(rowView == null)
        {
            rowView = mInlfater.inflate(R.layout.list_single_que, null, true);
            holder = new CallServiceCustomAdapter.ViewHolder();
            holder.tvCID = (TextView) rowView.findViewById(R.id.tvCID);
            holder.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
            holder.btnQue = (Button) rowView.findViewById(R.id.btnQue);
            rowView.setTag(holder);
        }
        else
        {
            holder =(ViewHolder) rowView.getTag();
        }
        holder.tvCID.setText((CharSequence) arrayCID.get(position));
        holder.tvTime.setText((CharSequence) arrayTime.get(position));
        holder.btnQue.setText((CharSequence) arrayShowQue.get(position));
        holder.btnQue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(mInlfater.getContext(), " "+arrayID.get(position).toString() , Toast.LENGTH_SHORT).show();
                sp = mInlfater.getContext().getSharedPreferences("PATIENT_ID", Context.MODE_PRIVATE);
                editor = sp.edit();
                editor.putString("id",arrayID.get(position).toString());
                editor.commit();

                SelectStationFragment selectStationFragment = new SelectStationFragment();
                transaction.replace(R.id.fragment_container, selectStationFragment, "selectStationFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rowView;
    }
    static class ViewHolder
    {
        TextView tvCID, tvTime;
        Button btnQue;
    }
}

