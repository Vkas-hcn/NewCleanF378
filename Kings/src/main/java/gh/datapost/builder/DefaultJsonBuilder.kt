package gh.datapost.builder

import org.json.JSONObject

/**
 * Date：2025/10/23
 * Describe: 默认Json构建器实现
 * 通过接口回调方式构建Json，增加差异化
 */
internal class DefaultJsonBuilder : JsonBuilderContext {
    
    private val fieldBuilders = mutableListOf<JsonFieldBuilder>()
    
    override fun addFieldBuilder(builder: JsonFieldBuilder) {
        fieldBuilders.add(builder)
    }
    
    override fun build(): JSONObject {
        val json = JSONObject()
        fieldBuilders.forEach { builder ->
            builder.buildField(json)
        }
        return json
    }
}

/**
 * 简单字段构建器
 */
internal class SimpleFieldBuilder(
    private val fieldName: String,
    private val valueProvider: FieldValueProvider
) : JsonFieldBuilder {
    
    override fun buildField(json: JSONObject) {
        val value = valueProvider.provideValue()
        if (value != null) {
            json.put(fieldName, value)
        }
    }
    
    override fun getFieldName(): String = fieldName
}

/**
 * Lambda值提供器
 */
internal class LambdaValueProvider(
    private val provider: () -> Any?
) : FieldValueProvider {
    override fun provideValue(): Any? = provider()
}

/**
 * 嵌套Json字段构建器
 */
internal class NestedJsonFieldBuilder(
    private val fieldName: String,
    private val nestedBuilders: List<JsonFieldBuilder>
) : JsonFieldBuilder {
    
    override fun buildField(json: JSONObject) {
        val nestedJson = JSONObject()
        nestedBuilders.forEach { builder ->
            builder.buildField(nestedJson)
        }
        json.put(fieldName, nestedJson)
    }
    
    override fun getFieldName(): String = fieldName
}

