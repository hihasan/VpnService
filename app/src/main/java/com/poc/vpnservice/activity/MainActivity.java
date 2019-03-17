package com.poc.vpnservice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.poc.vpnservice.R;
import com.poc.vpnservice.adapter.LandingPageViewPagerAdapter;
import com.poc.vpnservice.fragment.LandingPageServiceTabFragment;
import com.poc.vpnservice.service.Vpn;

public class MainActivity extends AppCompatActivity implements LandingPageServiceTabFragment.SignOutInterface {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LandingPageViewPagerAdapter adapter;
    private FrameLayout fabContainerFrameLayout;
    private static final String[] tabArray = {"STATUS", "SERVICE", "APPS"};//Tab title array
    private boolean isStart;
    private static final int VPN_REQUEST_CODE = 0x0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        if(!isStart) {
            //StartService(new Intent(MainActivity.this, Vpn.class));
            startVPN();
        }
        else{
            sendBroadcast(new Intent(Vpn.BROADCAST_STOP_VPN));
        }

//        LinearLayout l =(LinearLayout) findViewById(R.id.clickable);
//        l.setOnClickListener(this);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager.setOffscreenPageLimit(2);

        adapter = new LandingPageViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);//setting tab over viewpager

        //Implementing tab selected listener over tablayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                switch (tab.getPosition()) {
                    case 0:
                        Log.e("TAG", "TAB1");
                        break;
                    case 1:
                        Log.e("TAG", "TAB2");
                        break;
                    case 2:
                        Log.e("TAG", "TAB3");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setUpCustomTabs();

//        fabContainerFrameLayout = findViewById(R.id.fabContainerFrameLayout);
//        fabContainerFrameLayout.setOnClickListener((View.OnClickListener) this);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            stopService(new Intent(MainActivity.this, Vpn.class));
        }
    };


    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (Vpn.BROADCAST_VPN_STATE.equals(intent.getAction()))
            {
                if (intent.getBooleanExtra("running", false))
                {
                    isStart = true;

                }
                else
                {
                    isStart =false;

                    handler.postDelayed(runnable,200);
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            startService(new Intent(MainActivity.this, Vpn.class));
        }
    }

    private void startVPN()
    {
        Intent vpnIntent = VpnService.prepare(MainActivity.this);
        if (vpnIntent != null)
        {
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        }
        else
        {
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
        }
    }

    //setting custom layout over tab
    private void setUpCustomTabs() {
        for (int i = 0; i < tabArray.length; i++) {
            TextView customTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);//get custom view
            customTab.setText(tabArray[i]);//set text over view
            TabLayout.Tab tab = tabLayout.getTabAt(i);//get tab via position
            if (tab != null) {
                tab.setCustomView(customTab);//set custom view
            }
        }
    }

    //Need to set it


//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.fabContainerFrameLayout:
//                Intent intent = Vpn.prepare(MainActivity.this);
//                if (intent != null) {
//                    //intent
//                    startActivityForResult(intent, 0);
//                } else {
//                    onActivityResult(0, RESULT_OK, null);
//                }
//        }
//    }
//
//    protected void onActivityResult(int request, int result, Intent data) {
//
//        if (result == RESULT_OK) {
//            SLog.e("vpnServer", "===============");
//            ToyVpnService.startService(this);
//            return;
//        }
//        SLog.e("vpnServer", "=============");
//    }

    //End here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSignOut(){
        adapter = new LandingPageViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);//setting tab over viewpager

        //Implementing tab selected listener over tablayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                switch (tab.getPosition()) {
                    case 0:
                        Log.e("TAG", "TAB1");
                        break;
                    case 1:
                        Log.e("TAG", "TAB2");
                        break;
                    case 2:
                        Log.e("TAG", "TAB3");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setUpCustomTabs();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        unregisterReceiver(vpnStateReceiver);
    }
}
