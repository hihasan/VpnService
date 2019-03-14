package com.poc.vpnservice.fragment;

// Created by Arabi on 19-Dec-18.

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.poc.vpnservice.R;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.poc.vpnservice.common.Constants.LOGIN_STATUS_SHARED_PREF;
import static com.poc.vpnservice.common.Constants.MY_SHARED_PREFS_NAME;
import static com.poc.vpnservice.common.Constants.SERVER_END_POINT;
import static com.poc.vpnservice.common.Constants.USER_ID_SHARED_PREF;

public class LandingPageServiceTabFragment extends Fragment {
    private View view;
    private TextView userIdTv, serverEndPointTv;
    private Button signOutButton;

    private boolean _hasLoadedOnce = false; // your boolean field

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && !_hasLoadedOnce) {
                _hasLoadedOnce = true;

                SharedPreferences prefs = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE);
                String userId = prefs.getString(USER_ID_SHARED_PREF, null);
                if (!TextUtils.isEmpty(userId)) {
                    userIdTv.setText(userId);
                } else {
                    Toast.makeText(getActivity(), "Warning: Not logged in!", Toast.LENGTH_SHORT).show();
                }


                serverEndPointTv.setText(SERVER_END_POINT);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.landing_page_service_tab_fragment, container, false);

        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        userIdTv = view.findViewById(R.id.user_id);
        serverEndPointTv = view.findViewById(R.id.server_end_point);
        signOutButton = view.findViewById(R.id.sign_out_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutButtonClicked();
            }
        });
    }

    private void signOutButtonClicked() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(LOGIN_STATUS_SHARED_PREF, false);
        editor.putString(USER_ID_SHARED_PREF, null);
        editor.apply();

        SignOutInterface signOutInterface = (SignOutInterface) getActivity();
        signOutInterface.onSignOut();
    }

    public interface SignOutInterface{
        public void onSignOut();
    }
}
