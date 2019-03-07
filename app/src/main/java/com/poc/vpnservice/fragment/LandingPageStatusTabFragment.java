package com.poc.vpnservice.fragment;

// Created by Arabi on 19-Dec-18.

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.poc.vpnservice.R;

import java.util.ArrayList;
import java.util.List;

public class LandingPageStatusTabFragment extends Fragment {
    private View view;
    private AppCompatSpinner accounts_spinner;
    private List<String> accounts_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.landing_page_status_tab_fragment, container, false);
        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        accounts_spinner = view.findViewById(R.id.sp_accounts);
        accounts_list = new ArrayList<>();
        accounts_list.add("arabi@abc.com");
        accounts_list.add("nadim@d.com");
        accounts_list.add("john@xyz.com");

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accounts_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts_spinner.setAdapter(adapter);
        accounts_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String emailId = accounts_list.get(i);
                //Toast.makeText(getActivity(), "Selected Account: " + emailId, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
