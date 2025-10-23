package gh.sj

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


object MvS {
    
    private var mmkv: MMKV? = null
    

    fun init(rootDir: String? = null) {
        try {
            MMKV.initialize(rootDir ?: MMKV.getRootDir())
            mmkv = MMKV.defaultMMKV()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    fun getMMKV(): MMKV? {
        if (mmkv == null) {
            try {
                mmkv = MMKV.defaultMMKV()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return mmkv
    }
    

    fun int(defaultValue: Int = 0, key: String? = null) = object : ReadWriteProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return getMMKV()?.decodeInt(key ?: property.name, defaultValue) ?: defaultValue
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            getMMKV()?.encode(key ?: property.name, value)
        }
    }
    

    fun long(defaultValue: Long = 0L, key: String? = null) = object : ReadWriteProperty<Any?, Long> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
            return getMMKV()?.decodeLong(key ?: property.name, defaultValue) ?: defaultValue
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
            getMMKV()?.encode(key ?: property.name, value)
        }
    }
    

    fun float(defaultValue: Float = 0f, key: String? = null) = object : ReadWriteProperty<Any?, Float> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Float {
            return getMMKV()?.decodeFloat(key ?: property.name, defaultValue) ?: defaultValue
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
            getMMKV()?.encode(key ?: property.name, value)
        }
    }
    

    fun double(defaultValue: Double = 0.0, key: String? = null) = object : ReadWriteProperty<Any?, Double> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
            return getMMKV()?.decodeDouble(key ?: property.name, defaultValue) ?: defaultValue
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
            getMMKV()?.encode(key ?: property.name, value)
        }
    }


    fun bool(defaultValue: Boolean = false, key: String? = null) = object : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return getMMKV()?.decodeBool(key ?: property.name, defaultValue) ?: defaultValue
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            getMMKV()?.encode(key ?: property.name, value)
        }
    }
    

    fun string(defaultValue: String = "", key: String? = null) = object : ReadWriteProperty<Any?, String> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return getMMKV()?.decodeString(key ?: property.name, defaultValue) ?: defaultValue
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            getMMKV()?.encode(key ?: property.name, value)
        }
    }
    

    fun stringNullable(defaultValue: String? = null, key: String? = null) = object : ReadWriteProperty<Any?, String?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return getMMKV()?.decodeString(key ?: property.name, defaultValue)
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if (value == null) {
                getMMKV()?.removeValueForKey(key ?: property.name)
            } else {
                getMMKV()?.encode(key ?: property.name, value)
            }
        }
    }
    

    fun byteArray(defaultValue: ByteArray? = null, key: String? = null) = object : ReadWriteProperty<Any?, ByteArray?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): ByteArray? {
            return getMMKV()?.decodeBytes(key ?: property.name, defaultValue)
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: ByteArray?) {
            if (value == null) {
                getMMKV()?.removeValueForKey(key ?: property.name)
            } else {
                getMMKV()?.encode(key ?: property.name, value)
            }
        }
    }
    
    /**
     * Parcelable 类型属性委托
     */
    inline fun <reified T : Parcelable> parcelable(defaultValue: T? = null, key: String? = null) = 
        object : ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return try {
                getMMKV()?.decodeParcelable(key ?: property.name, T::class.java, defaultValue)
            } catch (e: Exception) {
                e.printStackTrace()
                defaultValue
            }
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            if (value == null) {
                getMMKV()?.removeValueForKey(key ?: property.name)
            } else {
                try {
                    getMMKV()?.encode(key ?: property.name, value)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    /**
     * Set<String> 类型属性委托
     */
    fun stringSet(defaultValue: Set<String>? = null, key: String? = null) = 
        object : ReadWriteProperty<Any?, Set<String>?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Set<String>? {
            return getMMKV()?.decodeStringSet(key ?: property.name, defaultValue)
        }
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Set<String>?) {
            if (value == null) {
                getMMKV()?.removeValueForKey(key ?: property.name)
            } else {
                getMMKV()?.encode(key ?: property.name, value)
            }
        }
    }
    
    /**
     * 删除指定 key 的值
     */
    fun remove(key: String) {
        getMMKV()?.removeValueForKey(key)
    }
    
    /**
     * 删除多个 key 的值
     */
    fun remove(vararg keys: String) {
        val mmkv = getMMKV() ?: return
        keys.forEach { mmkv.removeValueForKey(it) }
    }
    
    /**
     * 清除所有数据
     */
    fun clearAll() {
        getMMKV()?.clearAll()
    }
    
    /**
     * 检查是否包含某个 key
     */
    fun contains(key: String): Boolean {
        return getMMKV()?.containsKey(key) ?: false
    }
    
    /**
     * 获取所有的 key
     */
    fun allKeys(): Array<String>? {
        return getMMKV()?.allKeys()
    }
    
    /**
     * 同步数据到文件
     */
    fun sync() {
        getMMKV()?.sync()
    }
    
    /**
     * 异步同步数据到文件
     */
    fun async() {
        getMMKV()?.async()
    }
}