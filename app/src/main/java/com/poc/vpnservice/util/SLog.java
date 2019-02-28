package com.poc.vpnservice.util;

import android.util.Log;

public class SLog
{
    public static void e(Object tag, Object value) {
        Log.e(tag.toString(), value.toString());
    }
}
