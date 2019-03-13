package com.poc.vpnservice.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.VpnService;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poc.vpnservice.R;
import com.poc.vpnservice.adapter.LandingPageViewPagerAdapter;
import com.poc.vpnservice.server.ToyVpnService;
import com.poc.vpnservice.util.SLog;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LandingPageViewPagerAdapter adapter;
    private FrameLayout fabContainerFrameLayout;
    private static final String[] tabArray = {"STATUS", "SERVICE", "APPS"};//Tab title array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        LinearLayout l =(LinearLayout) findViewById(R.id.clickable);
//        l.setOnClickListener(this);

        viewPager = findViewById(R.id.viewPager);
        adapter = new LandingPageViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabLayout);
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

        fabContainerFrameLayout = findViewById(R.id.fabContainerFrameLayout);
        fabContainerFrameLayout.setOnClickListener((View.OnClickListener) this);
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

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.fabContainerFrameLayout:
//                Intent intent = VpnService.prepare(MainActivity.this);
//                if (intent != null) {
//                    //启动intent
//                    startActivityForResult(intent, 0);
//                } else {
//                    onActivityResult(0, RESULT_OK, null);
//                }
//        }
//    }
//
//    protected void onActivityResult(int request, int result, Intent data) {
//        //同意本app启动vpn服务
//        if (result == RESULT_OK) {
//            SLog.e("启动vpnServer", "===============");
//            ToyVpnService.startService(this);
//            return;
//        }
//        SLog.e("不能启动vpnServer", "=============");
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


}
