package com.it.rbh.hyggeconsole;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HospitalDepCustomAdapter extends BaseAdapter {
    LayoutInflater mInlfater;
    ArrayList arrayDepName, arrayDepCode, arrayPrefix;
    Activity context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    FragmentTransaction transaction;


        public HospitalDepCustomAdapter(Activity context, ArrayList<String> arrayDepName,
                                        ArrayList<String> arrayDepCode, ArrayList<String> arrayPrefix, FragmentTransaction transaction)
        {
            this.context = context;
            mInlfater = context.getLayoutInflater();
            this.arrayDepName = arrayDepName;
            this.arrayDepCode = arrayDepCode;
            this.arrayPrefix = arrayPrefix;
            this.transaction = transaction;

        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayDepCode.size();
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
            if(rowView == null) {
                rowView = mInlfater.inflate(R.layout.list_single_department, null, true);
                holder = new HospitalDepCustomAdapter.ViewHolder();
                holder.depName = (TextView) rowView.findViewById(R.id.tvDepname);
                holder.rowDep = (ConstraintLayout) rowView.findViewById(R.id.rowDep);
                rowView.setTag(holder);
            }else {
                holder = (ViewHolder) rowView.getTag();
            }
            holder.depName.setText((CharSequence) arrayDepName.get(position));
            holder.rowDep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mInlfater.getContext(), " "+arrayDepCode.get(position).toString() , Toast.LENGTH_SHORT).show();
                    String depcode = arrayDepCode.get(position).toString();
                    String depname = arrayDepName.get(position).toString();
                    String prefix = arrayPrefix.get(position).toString();

                    sp = mInlfater.getContext().getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString("depcode", depcode);
                    editor.putString("depname", depname);
                    editor.putString("prefix", prefix);
                    editor.commit();

                    ConsoleFragment consoleFragment = new ConsoleFragment();
                    transaction.replace(R.id.fragment_container, consoleFragment, "serviceFragment");
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            return rowView;
        }
    static class ViewHolder
    {
        TextView depName;
        ConstraintLayout rowDep;
    }

}
