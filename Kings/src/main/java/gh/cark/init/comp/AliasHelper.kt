package gh.cark.init.comp

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import gh.sj.MvS

/**
 * Date：2025/10/23
 * Describe: 组件别名辅助类
 * 深度隐藏别名启用逻辑，防止编译器内联
 */
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
    
    /**
     * 配置管理器（中间层）
     */
    private val configMgr by lazy { ComponentConfigMgr() }
    
    /**
     * 执行组件配置
     */
    fun performComponentSetup(ctx: Context, aliasName: String) {
        // 通过中间层调用
        configMgr.applyComponentState(ctx, aliasName)
    }
    
    /**
     * 组件配置管理器（内部类，增加一层隔离）
     */
    private inner class ComponentConfigMgr {
        
        private val executor by lazy { ComponentExecutor() }
        
        fun applyComponentState(context: Context, componentAlias: String) {
            // 再通过执行器调用
            executor.executeComponentChange(context, componentAlias)
        }
    }
    
    /**
     * 组件执行器（最深层，真正执行逻辑）
     */
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

