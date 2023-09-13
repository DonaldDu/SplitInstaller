package com.dhy.splitinstaller;

public class DeviceStatusResponse extends DeviceStatus {
    public int code;
    public String error;

    public boolean isError() {
        return code != 0;
    }
}
