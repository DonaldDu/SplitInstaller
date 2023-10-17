package com.donalddu.splitinstaller;

import android.content.pm.ApplicationInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class ApplicationInfoIH implements InvocationHandler {
    private final Object origin;
    private final String hostPackageName;

    public ApplicationInfoIH(Object origin, String hostPackageName) {
        this.origin = origin;
        this.hostPackageName = hostPackageName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object data = method.invoke(origin, args);
        //mPM.getPackageInfo(packageName, flags, mContext.getUserId())
        if (method.getName().equals("getApplicationInfo") && hostPackageName.equals(args[0])) {
            loadSplits((ApplicationInfo) data);
        }
        return data;
    }

    protected abstract void loadSplits(ApplicationInfo info);
}
