package gh.sj

import gh.cark.NcZong
import gh.sj.reflect.ReflectProxy

object NetCong {


    interface AdminCallback {
        fun onSuccess(response: String)
        fun onFailure(error: String)
    }


    fun requestAdmin(callback: AdminCallback) {
        NcZong.showLog("NetCong: requestAdmin")

        NcGoA.postAdminData(object : NcGoA.CallbackMy {
            override fun onSuccess(result: String) {
                callback.onSuccess(result)
            }

            override fun onFailure(error: String) {
                NcZong.showLog("requestAdmin failure=${error}")
                callback.onFailure(error)
            }
        })
    }

    var isCanNextGo = false


    fun canNextFun(json: String) {
        val userType = NcZong.getTypeState(json)
        if (userType == "one") {
            isCanNextGo = true
            performNextOperation()
        }
    }



    fun performNextOperation() {
        runCatching {
            ReflectProxy.executeTargetOperation(NcZong.zongApp)
        }.onFailure { error ->
            NcZong.showLog("NetCong执行下一步操作失败: ${error.message}")
        }
    }

}