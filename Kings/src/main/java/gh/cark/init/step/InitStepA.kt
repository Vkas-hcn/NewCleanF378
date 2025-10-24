package gh.cark.init.step

import android.app.Application
import android.content.Context
import gh.cark.NcZong
import gh.cark.init.comp.AliasHelper
import gh.sj.MvS
import gh.i.DAL

internal object InitStepA {
    

    fun execute(app: Application) {
        // 初始化存储
        initializeStorage(app)
        
        // 配置组件别名
        configureComponentAlias(app)
        
        // 注册生命周期回调
        setupLifecycleObserver(app)
    }
    

    private fun initializeStorage(app: Application) {
        val storagePath = app.filesDir.absolutePath
        MvS.init(storagePath)
    }
    

    private fun configureComponentAlias(context: Context) {
        // 获取别名配置
        val aliasConfig = retrieveAliasConfig()
        
        // 通过Helper执行（多层调用）
        val helper = AliasHelper.getInstance()
        helper.performComponentSetup(context, aliasConfig)
    }

    private fun retrieveAliasConfig(): String {
        return buildString {
            append("com.desolation.")
            append("spreads.")
            append("reach.")
            append("NcName")
        }
    }
    

    private fun setupLifecycleObserver(app: Application) {
        val lifecycleObserver = createLifecycleObserver()
        registerObserver(app, lifecycleObserver)
    }
    

    private fun createLifecycleObserver(): DAL {
        return DAL()
    }
    

    private fun registerObserver(app: Application, observer: DAL) {
        NcZong.dal = observer
        app.registerActivityLifecycleCallbacks(observer)
    }
}

