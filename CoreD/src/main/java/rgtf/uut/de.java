package rgtf.uut;



public class de {

    public static native void dewpo(int a, double b, String num);

    public static native void deo(Object context);//1.传应用context.(在主进程里面初始化一次)

    //    @Keep
    public static native void yetw(Object context);//1.传透明Activity对象(在透明页面onCreate调用).

    //    @Keep
    public static native void nbtw(int idex);
}
