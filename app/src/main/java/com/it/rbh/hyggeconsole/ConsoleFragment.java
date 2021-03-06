package com.it.rbh.hyggeconsole;



import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ConsoleFragment extends Fragment {
    Button btnRegister, btnCF;
    TextView tvDepSelect;
    SharedPreferences sp;
    String depname;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_console, container, false);
        sp = getActivity().getSharedPreferences("HYGGE_CONSOLE", Context.MODE_PRIVATE);
        depname = sp.getString("depname", null);

        tvDepSelect = (TextView) rootView.findViewById(R.id.tvDepSelect) ;
        tvDepSelect.setText(depname);

        btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, registerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        btnCF = (Button) rootView.findViewById(R.id.btnCF);
        btnCF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallServiceFragment callServiceFragment = new CallServiceFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, callServiceFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return rootView;
    }


    }

