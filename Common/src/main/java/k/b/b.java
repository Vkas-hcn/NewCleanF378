package k.b;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

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

    }

    // 上报广告事件
    @NonNull
    @Override
    public String e(@NonNull String string) {
        return null;
    }

    @Override
    public void c(@NonNull String string) {

    }

    // 上报install 事件
    @Override
    public void d(@NonNull String ref) {

    }

    // finish 掉所有activity
    @Override
    public long f() {
        return 0;
    }
}
