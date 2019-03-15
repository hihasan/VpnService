package com.poc.vpnservice.fragment;

// Created by Arabi on 19-Dec-18.

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.poc.vpnservice.R;
import com.poc.vpnservice.util.SLog;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.poc.vpnservice.common.Constants.LOGIN_STATUS_SHARED_PREF;
import static com.poc.vpnservice.common.Constants.LOGIN_TIME_IN_MILLIS;
import static com.poc.vpnservice.common.Constants.MY_SHARED_PREFS_NAME;
import static com.poc.vpnservice.common.Constants.PIN;
import static com.poc.vpnservice.common.Constants.PIN_LENGTH;
import static com.poc.vpnservice.common.Constants.USER_ID;
import static com.poc.vpnservice.common.Constants.USER_ID_SHARED_PREF;

public class LandingPageStatusTabFragment extends Fragment implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    private TextView RX,TX,onlineTimeTv;
    private View view;
    private AppCompatSpinner accounts_spinner;
    private List<String> accounts_list;
    private String userId;
    private Button readyButton, signInButton, signOutButton;
    private ViewGroup chooseAccountLayout, insertPasswordLayout, afterSignInLayout;
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    private String inputPin;
    private long loginTime;

    //Tx, RX Value
    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;

    private CharSequence charSequence = new CharSequence() {
        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int index) {
            return 0;
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return null;
        }
    };

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
        Intent intent = VpnService.prepare(getActivity());
        if (intent != null) {
            //intent
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, RESULT_OK, null);
        }

        chooseAccountLayout = view.findViewById(R.id.choose_account_layout);
        insertPasswordLayout = view.findViewById(R.id.insert_password_layout);
        afterSignInLayout = view.findViewById(R.id.status_tab_layout_after_sign_in);

        accounts_spinner = view.findViewById(R.id.sp_accounts);
        accounts_list = new ArrayList<>();
        accounts_list.add("freevpnaccess.com");
        accounts_list.add("arabi@abc.com");
        accounts_list.add("nadim@d.com");
        accounts_list.add("john@xyz.com");

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accounts_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accounts_spinner.setAdapter(adapter);
        accounts_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userId = accounts_list.get(i);
                //Toast.makeText(getActivity(), "Selected Account: " + userId, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        readyButton = view.findViewById(R.id.ready_button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId.equals(USER_ID)) {
                    readyButtonClicked();
                } else {
                    Toast.makeText(getActivity(), "Error: Incorrect User ID!", Toast.LENGTH_LONG).show();
                }
            }
        });

        mPinFirstDigitEditText = view.findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = view.findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = view.findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = view.findViewById(R.id.pin_forth_edittext);
        mPinHiddenEditText = view.findViewById(R.id.pin_hidden_edittext);
        setPINListeners();

        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinFirstDigitEditText.setText("");
                mPinSecondDigitEditText.setText("");
                mPinThirdDigitEditText.setText("");
                mPinForthDigitEditText.setText("");
                mPinHiddenEditText.setText("");

                if(inputPin.length() == PIN_LENGTH && inputPin.equals(PIN)) {
                    long loginTimeInMillis = System.currentTimeMillis();

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(LOGIN_STATUS_SHARED_PREF, true);
                    editor.putString(USER_ID_SHARED_PREF, userId);
                    editor.putLong(LOGIN_TIME_IN_MILLIS, loginTimeInMillis);
                    editor.apply();

                    signInButtonClicked();
                } else {
                    Toast.makeText(getActivity(), "Error: Incorrect pin!", Toast.LENGTH_SHORT).show();
                    mPinFirstDigitEditText.requestFocus();
                }
            }
        });

        signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutButtonClicked();
            }
        });

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE);
        boolean loginStatus = prefs.getBoolean(LOGIN_STATUS_SHARED_PREF, false);
        if(loginStatus) {
            chooseAccountLayout.setVisibility(GONE);
            insertPasswordLayout.setVisibility(GONE);
            afterSignInLayout.setVisibility(VISIBLE);
            signInButtonClicked();
        }
    }

    private void readyButtonClicked() {
        chooseAccountLayout.setVisibility(GONE);
        insertPasswordLayout.setVisibility(VISIBLE);
    }

//    @Override
//    public void onActivityResult(int request, int result, Intent data) {
//
//        if (result == RESULT_OK) {
//            SLog.e("vpnServer", "===============");
//            ToyVpnService.startService(getActivity());
//            return;
//        }
//        SLog.e("vpnServer", "=============");
//    }

    private void signInButtonClicked() {
        //Code for rx tx
        RX = view.findViewById(R.id.rx);
        TX = view.findViewById(R.id.tx);
        onlineTimeTv = view.findViewById(R.id.online_time_value_tv);
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE);
        loginTime = prefs.getLong(LOGIN_TIME_IN_MILLIS, 0L);

        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();

        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
//            AlertDialog.Builder alert = new AlertDialog.Builder(this);
//            alert.setTitle("Uh Oh!");
//            alert.setMessage("Your device does not support traffic stat monitoring.");
//            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }

        insertPasswordLayout.setVisibility(GONE);
        afterSignInLayout.setVisibility(VISIBLE);
    }

    private void signOutButtonClicked() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(LOGIN_STATUS_SHARED_PREF, false);
        editor.putString(USER_ID_SHARED_PREF, null);
        editor.apply();

        afterSignInLayout.setVisibility(GONE);
        chooseAccountLayout.setVisibility(VISIBLE);
    }

    //rx tx function
    private final Runnable mRunnable = new Runnable() {
        public void run() {

            long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
            RX.setText(Long.toString(rxBytes)+" bytes");
            long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
            TX.setText(Long.toString(txBytes)+" bytes");

            long currentTime = System.currentTimeMillis();
            long difference = currentTime - loginTime;
            String onlineTime = "";
            int hours = (int) difference/(60*60*1000);
            if (hours > 0) {
                onlineTime = onlineTime + hours + "hr ";
            }
            int minutes = (int) (difference%(60*60*1000))/(60*1000);
            if (minutes > 0) {
                onlineTime = onlineTime + minutes + "min ";
            }
            int seconds = (int) ((difference%(60*60*1000))%(60*1000))/1000;
            if (seconds > 0) {
                onlineTime = onlineTime + seconds + "sec";
            }
            onlineTimeTv.setText(onlineTime);

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        if(null != getActivity()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);

            if(null != imm) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        if(null != getActivity()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
            if(null != imm) {
                imm.showSoftInput(editText, 0);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        charSequence = s;

        //setDefaultPinBackground(mPinFirstDigitEditText);
        //setDefaultPinBackground(mPinSecondDigitEditText);
        //setDefaultPinBackground(mPinThirdDigitEditText);
        //setDefaultPinBackground(mPinForthDigitEditText);
        //setDefaultPinBackground(mPinFifthDigitEditText);

        if (s.length() == 0) {
            //setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            //setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 2) {
            //setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 3) {
            //setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 4) {
            //setFocusedPinBackground(mPinFifthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");

            inputPin = mPinFirstDigitEditText.getText().toString() + mPinSecondDigitEditText.getText().toString() + mPinThirdDigitEditText.getText().toString() + mPinForthDigitEditText.getText().toString();
            hideSoftKeyboard(mPinForthDigitEditText);
        }
    }

    /**
     * Sets default PIN background.
     *
     * @param editText edit text to change
     */
    private void setDefaultPinBackground(EditText editText) {
        if (null != getActivity()) {
            //setViewBackground(editText, getActivity().getResources().getDrawable(R.drawable.selected_pin_edittext_bg, null));
            editText.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary, null));
        }
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    private void setFocusedPinBackground(EditText editText) {
        if (null != getActivity()) {
            //setViewBackground(editText, getActivity().getResources().getDrawable(R.drawable.default_pin_edittext_bg, null));
            editText.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark, null));
        }
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
    }

    /**
     * Sets background of the view.
     * This method varies in implementation depending on Android SDK version.
     *
     * @param view       View to which set background
     * @param background Background to set to view
     */
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }
}
