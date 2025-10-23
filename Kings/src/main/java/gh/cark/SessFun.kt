package gh.cark

import gh.datapost.NcPointFun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SessFun {
    fun ssPostFun() {
        GlobalScope.launch {
            NcPointFun.postPointFun(false, "session")
            delay(1000*60*15)
        }
    }
}