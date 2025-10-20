package com.two.smooth

import org.junit.Test

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val soName = "libacle.so"
    private val progetName = "T623"
    private val name64 = "quick.txt"
    private val name32 = "zzz.zip"

    // h5
//    private val soName = "libban.so"
//    private val progetName = "T564"
//    private val name64 = "quw93"
//    private val name32 = "qius.txt"

    @Test
    fun addition_isCorrect() {
        val inputFile = "/Users/jxx/Desktop/soencode/$progetName/arm64-v8a/$soName"

        // 加密后文件路径 64
        val encryptedFile = "/Users/jxx/Desktop/soencode/$progetName/$name64"

        encrypt(File(inputFile).inputStream(), File(encryptedFile))

        val inputFile2 = "/Users/jxx/Desktop/soencode/$progetName/armeabi-v7a/$soName"
        // 加密后文件路径
        val encryptedFile2 = "/Users/jxx/Desktop/soencode/$progetName/$name32"
        encrypt(File(inputFile2).inputStream(), File(encryptedFile2))
    }

    private val ALGORITHM = "AES"
    private val SECRET_KEY = "q17s893jsjgk0oqs".toByteArray() // 16, 24, or 32 bytes

    // 加密
    fun encrypt(inputStream: InputStream, outputFile: File) {
        val key = SecretKeySpec(
            SECRET_KEY, ALGORITHM
        )
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputStream = FileOutputStream(outputFile)
        val inputBytes = inputStream.readBytes()
        val outputBytes = cipher.doFinal(inputBytes)
        outputStream.write(outputBytes)
        outputStream.close()
        inputStream.close()
    }

    // 解密
    fun decrypt(inputFile: InputStream, outputFile: File) {
        val key = SecretKeySpec(
            SECRET_KEY, ALGORITHM
        )
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputStream = FileOutputStream(outputFile)
        val inputBytes = inputFile.readBytes()
        val outputBytes = cipher.doFinal(inputBytes)
        outputStream.write(outputBytes)
        outputStream.close()
        inputFile.close()
    }


    private val pathBASE = "/Users/jxx/AndroidStudioProjects/TDemo/TDexDemo/CoreD/"

    @Test
    fun addition_dex() {
        val sourceFilePath = "${pathBASE}makejar/dex/classes.dex" // 源文件路径，可按需修改
        val outputFolderPath = "${pathBASE}output" // 目标文件路径，可按需修改
        val sourceFile = File(sourceFilePath)
        val outputFolder = File(outputFolderPath)
        if (!outputFolder.exists()) {
            outputFolder.mkdirs()
        }

        val local1 = File("$outputFolderPath/local1.txt")
        val file3 = File("$outputFolderPath/origin.txt")
        val string = dexToAesText(sourceFile)

        local1.writeText(string)

        println("文件重写并保存成功")

        // 验证
        file3.writeText(string)
        // aes+iv 加密
        val restoredDex = File(outputFolderPath, "dexMy2.dex")
        val dexBytes = decryptDex(DEX_AES_KEY, file3.readText())
        FileOutputStream(restoredDex).use { it.write(dexBytes) }
    }

    private val DEX_AES_KEY = "v1a3g4s6q7e6ui2s".toByteArray() // 16, 24, or 32 bytes


    // DEX -> AES加密文本
    fun dexToAesText(dexFile: File): String {
        val dexBytes = dexFile.readBytes()
        val encrypted = encrypt(dexBytes)
        return Base64.getEncoder().encodeToString(encrypted)
    }


    // 加密
    fun encrypt(inputBytes: ByteArray): ByteArray {
        val key = SecretKeySpec(DEX_AES_KEY, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outputBytes = cipher.doFinal(inputBytes)
        return outputBytes
    }

    // 解密
    private fun decryptDex(keyAes: ByteArray, inStr: String): ByteArray {
        val inputBytes = Base64.getDecoder().decode(inStr)
        val key = SecretKeySpec(keyAes, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val outputBytes = cipher.doFinal(inputBytes)
        return outputBytes
    }

}