package az;


import android.os.Handler;
import android.os.Message;


/**
 * Date：2025/7/28
 * Describe:
 */
// todo 重命名
public class MHandler extends Handler {
    @Override
    public void handleMessage(Message message) {
        az.b.a.d(message.what);
    }
}
