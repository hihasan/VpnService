package com.poc.vpnservice.fragment;

// Created by Arabi on 19-Dec-18.

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.poc.vpnservice.R;
import com.poc.vpnservice.adapter.AppNotUsingVpnAdapter;
import com.poc.vpnservice.adapter.AppUsingVpnAdapter;
import com.poc.vpnservice.adapter.AppList;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.poc.vpnservice.common.Constants.MY_SHARED_PREFS_NAME;
import static com.poc.vpnservice.common.Constants.VPN_ALLOWED_APPS;
import static com.poc.vpnservice.common.Constants.VPN_NOT_ALLOWED_APPS;

public class LandingPageAppsTabFragment extends Fragment implements AppUsingVpnAdapter.RemoveAllowedAppEventListener, AppNotUsingVpnAdapter.RemoveNotAllowedAppEventListener{
    private View view;
    private ListView userInstalledAppsLv;
    private ListView appsNotUsingVpnLv;
    private TextView notUsingVpnListIsEmptyTv, usingVpnListIsEmptyTv;
    private ProgressBar progressBar;
    private AppUsingVpnAdapter installedAppAdapter;
    private AppNotUsingVpnAdapter appsNotUsingVpnAdapter;
    private List<AppList> installedAppsList;
    private List<AppList> appsNotUsingVpnList = new ArrayList<>();
    private List<String> packageNameList = new ArrayList<>();
    private List<String> appsNotUsingVpnPackageNameList = new ArrayList<>();

    private boolean _hasLoadedOnce = false; // your boolean field

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && !_hasLoadedOnce) {
                _hasLoadedOnce = true;

                notUsingVpnListIsEmptyTv.setVisibility(View.VISIBLE);
                appsNotUsingVpnAdapter = new AppNotUsingVpnAdapter(getActivity(), appsNotUsingVpnList, appsNotUsingVpnPackageNameList, this);
                appsNotUsingVpnLv.setAdapter(appsNotUsingVpnAdapter);
                appsNotUsingVpnLv.setVisibility(View.GONE);

                new MyTask().execute();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.landing_page_apps_tab_fragment, container, false);

        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        notUsingVpnListIsEmptyTv = view.findViewById(R.id.not_using_vpn_list_is_empty_tv);
        usingVpnListIsEmptyTv = view.findViewById(R.id.using_vpn_list_is_empty_tv);
        userInstalledAppsLv = view.findViewById(R.id.installed_app_list);
        appsNotUsingVpnLv = view.findViewById(R.id.not_using_vpn_app_list);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(10);
    }

    private List<AppList> getInstalledApps() {
        List<AppList> res = new ArrayList<AppList>();
        List<PackageInfo> packs = getActivity().getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            Log.d("packageName", "packageName "+i+": "+p.packageName);
            if (!isSystemPackage(p)) {
                String appName = p.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getActivity().getPackageManager());
                String packageName = p.packageName;
                res.add(new AppList(appName, icon, packageName));
                packageNameList.add(p.packageName);
            }
        }
        return res;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(Integer... params) {
            installedAppsList = getInstalledApps();
            return "Task Completed.";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
        @Override
        protected void onPostExecute(String result) {
            if(!TextUtils.isEmpty(result)) {
                setAppUsingVpnAdapter();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setAppUsingVpnAdapter() {
        if (null != installedAppsList && installedAppsList.size() > 0) {
            usingVpnListIsEmptyTv.setVisibility(View.GONE);
            installedAppAdapter = new AppUsingVpnAdapter(getActivity(), installedAppsList, packageNameList, this);
            userInstalledAppsLv.setAdapter(installedAppAdapter);
            userInstalledAppsLv.setVisibility(View.VISIBLE);
        } else {
            userInstalledAppsLv.setVisibility(View.GONE);
            usingVpnListIsEmptyTv.setVisibility(View.VISIBLE);
        }
    }

    public void onRemoveAllowedAppEvent(AppList app, String packageName) {
        installedAppsList.remove(app);
        packageNameList.remove(packageName);
        installedAppAdapter.notifyDataSetChanged();

        if(installedAppsList.size() > 0) {
            usingVpnListIsEmptyTv.setVisibility(View.GONE);
            userInstalledAppsLv.setVisibility(View.VISIBLE);
        } else {
            usingVpnListIsEmptyTv.setVisibility(View.VISIBLE);
            userInstalledAppsLv.setVisibility(View.GONE);
        }

        updateVpnAllowedPackageNames();

        appsNotUsingVpnList.add(app);
        appsNotUsingVpnPackageNameList.add(packageName);
        appsNotUsingVpnAdapter.notifyDataSetChanged();
        notUsingVpnListIsEmptyTv.setVisibility(View.GONE);
        appsNotUsingVpnLv.setVisibility(View.VISIBLE);
    }

    public void onRemoveNotAllowedAppEvent(AppList app, String packageName) {
        appsNotUsingVpnList.remove(app);
        appsNotUsingVpnPackageNameList.remove(packageName);
        appsNotUsingVpnAdapter.notifyDataSetChanged();

        if(appsNotUsingVpnList.size() > 0) {
            notUsingVpnListIsEmptyTv.setVisibility(View.GONE);
            appsNotUsingVpnLv.setVisibility(View.VISIBLE);
        } else {
            notUsingVpnListIsEmptyTv.setVisibility(View.VISIBLE);
            appsNotUsingVpnLv.setVisibility(View.GONE);
        }

        updateVpnNotAllowedPackageNames();

        installedAppsList.add(app);
        packageNameList.add(packageName);
        installedAppAdapter.notifyDataSetChanged();
        usingVpnListIsEmptyTv.setVisibility(View.GONE);
        userInstalledAppsLv.setVisibility(View.VISIBLE);
    }

    private void updateVpnAllowedPackageNames() {
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(VPN_ALLOWED_APPS +"_size", packageNameList.size());
        for(int i=0; i < packageNameList.size(); i++) {
            editor.putString(VPN_ALLOWED_APPS + "_" + i, packageNameList.get(i));
        }
        editor.apply();
    }

    private void updateVpnNotAllowedPackageNames() {
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(VPN_NOT_ALLOWED_APPS +"_size", appsNotUsingVpnPackageNameList.size());
        for(int i=0; i < appsNotUsingVpnPackageNameList.size(); i++) {
            editor.putString(VPN_NOT_ALLOWED_APPS + "_" + i, appsNotUsingVpnPackageNameList.get(i));
        }
        editor.apply();
    }
}
