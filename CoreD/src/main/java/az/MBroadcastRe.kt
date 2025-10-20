package az

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ak.impI.Constant

/**
 * Date：2025/7/28
 * Describe:
 *
 */

// todo 重命名
class MBroadcastRe : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val eIntent = intent?.getParcelableExtra(Constant.a) as Intent? //注意:广播接收key(改成你们自己提供的字段)
        if (eIntent != null) {
            try {
                context?.startActivity(eIntent)
            }catch (_:Exception){}
        }
    }
}