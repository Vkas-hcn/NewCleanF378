package a;

import android.app.Application;

import gh.cark.CoreBridge;
import gh.cark.NcZong;

public class A {
    public static void a(String s, String v) {
        CoreBridge.reportEvent(s, v);
    }
    public static void b(Application app) {
        NcZong.INSTANCE.initSp(app);
    }
}
