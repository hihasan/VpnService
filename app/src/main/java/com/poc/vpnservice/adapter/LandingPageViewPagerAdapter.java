package com.poc.vpnservice.adapter;

// Created by Arabi on 19-Dec-18.

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.poc.vpnservice.fragment.LandingPageAppsTabFragment;
import com.poc.vpnservice.fragment.LandingPageServiceTabFragment;
import com.poc.vpnservice.fragment.LandingPageStatusTabFragment;

public class LandingPageViewPagerAdapter extends FragmentPagerAdapter {

    public LandingPageViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new LandingPageStatusTabFragment();
        }
        else if (position == 1)
        {
            fragment = new LandingPageServiceTabFragment();
        }
        else if (position == 2)
        {
            fragment = new LandingPageAppsTabFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "STATUS";
        }
        else if (position == 1)
        {
            title = "SERVICE";
        }
        else if (position == 2)
        {
            title = "APPS";
        }
        return title;
    }
}
