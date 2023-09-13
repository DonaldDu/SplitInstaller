package com.dhy.splitinstaller;

import android.os.Build;

public class DeviceStatus {
    volatile public static DeviceStatus it = new DeviceStatus();
    public Boolean assetOK;
    public Boolean soOK;
    public Boolean pluginPage;
    public String BRAND = Build.BRAND;
    public String MODEL = Build.MODEL;

    public boolean allReady() {
        return soOK != null && assetOK != null && pluginPage != null;
    }
}
