package azshow;


import android.os.Handler;
import android.os.Message;

import azshow.sl.lo;


/**
 * Date：2025/7/28
 * Describe:
 */
// todo 重命名
public class NcH extends Handler {
    @Override
    public void handleMessage(Message message) {
        lo.lod(message.what);
    }
}
