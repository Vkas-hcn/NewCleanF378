package com.ak.impI;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.tencent.mmkv.MMKV;

/**
 * Date：2025/9/25
 * Describe:
 * com.ak.impI.Core
 */
public class Core {

    public static String ver = "1.0.1"; //appVersion
    public static long insAppTime = 0L; //installAppTime
    private static final MMKV mmkv = MMKV.defaultMMKV();
    public static com.ak.c e;
    public static boolean isPostLog = true;
    public static String mustPostLog = "";
    private static String mLog = "cf_fail-pop_fail-advertise_limit-config_G";
    public static Application mApp;

    // todo  入口 记得做差异化
    public static void a(Object context, com.ak.c c) {
        e = c;
        mApp = (Application) context;
        pE("test_d_load");
        inIf(mApp);
        //admin url
        // admin 的url，可以外面传进来，也可以直接在里面写死，
        // 但是上架的时候需要注意打包的dex已经去除了test字符串
        new AdminCheck(c.e("admin_url")).cr(mApp);
    }

    public static void pE(String string, String value) {
        if (isPostLog || mustPostLog.contains(string) || mLog.contains(string)) {
            e.a(string, value);
        }
    }

    public static void pE(String string) {
        pE(string, "");
    }

    public static void postAd(String string) {
        e.c(string);
    }


    public static String getStr(String key) {
        return mmkv.decodeString(key, "");
    }

    public static void saveC(String ke, String con) {
        mmkv.encode(ke, con);
    }

    public static int getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public static void saveInt(String key, int i) {
        mmkv.encode(key, i);
    }

    private static void inIf(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            ver = pi.versionName;
            insAppTime = pi.firstInstallTime;
        } catch (Exception ignored) {
        }
    }
}
