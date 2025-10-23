package gh.datapost.builder

import org.json.JSONObject

/**
 * Date：2025/10/23
 * Describe: Json字段构建器接口
 * 用于差异化处理Json数据拼接，避免直接硬编码
 */
internal interface JsonFieldBuilder {
    
    /**
     * 构建字段到JSONObject中
     */
    fun buildField(json: JSONObject)
    
    /**
     * 获取字段名称
     */
    fun getFieldName(): String
}

/**
 * 字段值提供器接口
 */
internal interface FieldValueProvider {
    /**
     * 获取字段值
     */
    fun provideValue(): Any?
}

/**
 * Json构建器上下文
 */
internal interface JsonBuilderContext {
    /**
     * 添加字段构建器
     */
    fun addFieldBuilder(builder: JsonFieldBuilder)
    
    /**
     * 执行构建
     */
    fun build(): JSONObject
}

