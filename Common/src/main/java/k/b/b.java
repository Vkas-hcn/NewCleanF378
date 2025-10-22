package k.b;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import zj.go.NcZong;
import zj.go.datapost.NcPointFun;
import zj.go.datapost.PostHelp;

/**
 * Date：2025/10/14
 * Describe:
 * todo 需要把Keep删掉加入到consumer-rules中
 * 具体的实现可以有几种不一定要keep实现类，接口类com.ak.c必须要keep住
 * 同时也可以把接口类的东西拆分开分别用几个keep 类来实现
 */


@Keep
public class b implements com.ak.c {
    // 上报普通埋点事件
    @Override
    public void a(@NonNull String string, @NonNull String value) {
        PostHelp.INSTANCE.postPointShow(string,value);
    }

    // 上报广告事件
    @Override
    public void c(@NonNull String string) {
        PostHelp.INSTANCE.postAdShow(string);
    }

    // finish 掉所有activity
    @Override
    public long f() {
        NcZong.dal.finishAllActivities();
        return 0;
    }
}
