package com.poc.vpnservice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.poc.vpnservice.R;
import com.poc.vpnservice.adapter.LandingPageViewPagerAdapter;
import com.poc.vpnservice.fragment.LandingPageAppsTabFragment;
import com.poc.vpnservice.fragment.LandingPageServiceTabFragment;
import com.poc.vpnservice.fragment.LandingPageStatusTabFragment;
import com.poc.vpnservice.service.Vpn;

import static com.poc.vpnservice.common.Constants.LOGIN_STATUS_SHARED_PREF;
import static com.poc.vpnservice.common.Constants.MY_SHARED_PREFS_NAME;
import static com.poc.vpnservice.common.Constants.USER_ID_SHARED_PREF;

public class MainActivity extends AppCompatActivity implements LandingPageServiceTabFragment.SignOutInterface, LandingPageStatusTabFragment.SignOutInterface {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LandingPageViewPagerAdapter adapter;
    private FrameLayout fabContainerFrameLayout;
    private static final String[] tabArray = {"STATUS", "SERVICE", "APPS"};//Tab title array
    private boolean isStart;
    private static final int VPN_REQUEST_CODE = 0x0F;
    LandingPageStatusTabFragment landingPageStatusTabFragment;
    LandingPageServiceTabFragment landingPageServiceTabFragment;
    LandingPageAppsTabFragment landingPageAppsTabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager.setOffscreenPageLimit(2);

        landingPageStatusTabFragment = new LandingPageStatusTabFragment();
        landingPageServiceTabFragment = new LandingPageServiceTabFragment();
        landingPageAppsTabFragment = new LandingPageAppsTabFragment();
        adapter = new LandingPageViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(landingPageStatusTabFragment, "STATUS");
        adapter.addFrag(landingPageServiceTabFragment, "SERVICE");
        adapter.addFrag(landingPageAppsTabFragment, "APPS");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE);
        boolean loginStatus = prefs.getBoolean(LOGIN_STATUS_SHARED_PREF, false);

        if (!loginStatus) {
            LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
            tabStrip.setEnabled(false);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setClickable(false);
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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

    public void signInButtonClicked() {
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        tabStrip.setEnabled(true);
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(true);
        }

        startVPN();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            startService(new Intent(MainActivity.this, Vpn.class));

            if(null != landingPageStatusTabFragment && landingPageStatusTabFragment.isAdded() && !landingPageStatusTabFragment.isDetached() && !landingPageStatusTabFragment.isRemoving()) {
                landingPageStatusTabFragment.showAfterSignInLayout();
            }
        }
    }

    public void startVPN() {
        Intent vpnIntent = VpnService.prepare(MainActivity.this);
        if (vpnIntent != null) {
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        } else {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onSignOut() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(LOGIN_STATUS_SHARED_PREF, false);
        editor.putString(USER_ID_SHARED_PREF, "");
        editor.apply();

        viewPager.setCurrentItem(0);
        if(null != landingPageStatusTabFragment) {
            landingPageStatusTabFragment.afterSignOut();
        }

        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        tabStrip.setEnabled(false);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
        }

        sendBroadcast(new Intent(Vpn.BROADCAST_STOP_VPN));
    }

    public void onAllowedAppsListChange() {
        sendBroadcast(new Intent(Vpn.BROADCAST_STOP_VPN));
        startVPN();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
