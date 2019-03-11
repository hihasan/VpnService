package com.poc.vpnservice.dummy;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.poc.vpnservice.R;

public class Main extends AppCompatActivity
{
    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tx_rx_demo);

        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();

        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }

    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            TextView RX = (TextView) findViewById(R.id.RX);
            TextView TX = (TextView) findViewById(R.id.TX);
            long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
            RX.setText(Long.toString(rxBytes));
            long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
            TX.setText(Long.toString(txBytes));
            mHandler.postDelayed(mRunnable, 1000);
        }
    };
}
