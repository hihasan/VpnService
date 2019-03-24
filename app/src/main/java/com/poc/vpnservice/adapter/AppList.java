package com.poc.vpnservice.adapter;

import android.graphics.drawable.Drawable;

public class AppList {
    private String name;
    Drawable icon;
    private String packageName;

    public AppList(String name, Drawable icon, String packageName) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageName;
    }
}
