package gh.cark.init.comp

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import gh.sj.MvS


internal class AliasHelper private constructor() {
    
    companion object {
        @Volatile
        private var instance: AliasHelper? = null
        
        fun getInstance(): AliasHelper {
            return instance ?: synchronized(this) {
                instance ?: AliasHelper().also { instance = it }
            }
        }
    }
    

    private val configMgr by lazy { ComponentConfigMgr() }
    

    fun performComponentSetup(ctx: Context, aliasName: String) {
        configMgr.applyComponentState(ctx, aliasName)
    }
    

    private inner class ComponentConfigMgr {
        
        private val executor by lazy { ComponentExecutor() }
        
        fun applyComponentState(context: Context, componentAlias: String) {
            executor.executeComponentChange(context, componentAlias)
        }
    }
    

    private inner class ComponentExecutor {
        
        fun executeComponentChange(ctx: Context, alias: String) {
            runCatching {
                val pkgMgr = retrievePackageManager(ctx)
                val component = buildComponent(ctx, alias)
                applyEnabledState(pkgMgr, component)
            }.onFailure {
                // 静默失败
                it.printStackTrace()
            }
        }
        
        private fun retrievePackageManager(context: Context): PackageManager {
            return context.packageManager
        }
        
        private fun buildComponent(context: Context, aliasName: String): ComponentName {
            return ComponentName(context, aliasName)
        }
        var isComponentEnabled by MvS.bool()
        private fun applyEnabledState(manager: PackageManager, component: ComponentName) {
            if (isComponentEnabled) {
                return
            }
            manager.setComponentEnabledSetting(
                component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            isComponentEnabled = true
        }
    }
}

