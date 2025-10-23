package gh.datapost.builder

import android.os.Build
import gh.cark.NcZong
import gh.sj.NcGoA
import org.json.JSONObject
import java.util.UUID

/**
 * Date：2025/10/23
 * Describe: 顶层Json字段工厂
 * 负责创建顶层Json的各个字段构建器
 */
internal object TopJsonFactory {
    
    /**
     * 创建顶层Json构建器上下文
     */
    fun createTopJsonContext(): JsonBuilderContext {
        val context = DefaultJsonBuilder()
        
        // 添加所有字段构建器
        addBundleIdField(context)
        addClientTimestampField(context)
        addAppVersionField(context)
        addAndroidIdField(context)
        addOperatorField(context)
        addDeviceModelField(context)
        addOsVersionField(context)
        addManufacturerField(context)
        addLogIdField(context)
        addDistinctIdField(context)
        addOsField(context)
        addSystemLanguageField(context)
        addGaidField(context)
        
        return context
    }
    
    /**
     * bundle_id字段
     */
    private fun addBundleIdField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("flora", LambdaValueProvider {
                NcZong.zongApp.packageName
            })
        )
    }
    
    /**
     * client_ts字段
     */
    private fun addClientTimestampField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("visit", LambdaValueProvider {
                System.currentTimeMillis()
            })
        )
    }
    
    /**
     * app_version字段
     */
    private fun addAppVersionField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("creep", LambdaValueProvider {
                NcGoA.showAppVersion()
            })
        )
    }
    
    /**
     * android_id字段
     */
    private fun addAndroidIdField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("stasis", LambdaValueProvider {
                NcZong.aau
            })
        )
    }
    
    /**
     * operator字段（假值）
     */
    private fun addOperatorField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("rove", LambdaValueProvider {
                "rvvs"
            })
        )
    }
    
    /**
     * device_model字段
     */
    private fun addDeviceModelField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("barbour", LambdaValueProvider {
                Build.BRAND
            })
        )
    }
    
    /**
     * os_version字段
     */
    private fun addOsVersionField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("pleural", LambdaValueProvider {
                Build.VERSION.RELEASE
            })
        )
    }
    
    /**
     * manufacturer字段
     */
    private fun addManufacturerField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("joel", LambdaValueProvider {
                Build.MANUFACTURER
            })
        )
    }
    
    /**
     * log_id字段
     */
    private fun addLogIdField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("kiosk", LambdaValueProvider {
                UUID.randomUUID().toString()
            })
        )
    }
    
    /**
     * distinct_id字段
     */
    private fun addDistinctIdField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("berg", LambdaValueProvider {
                NcZong.aau
            })
        )
    }
    
    /**
     * os字段
     */
    private fun addOsField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("ruthless", LambdaValueProvider {
                "absent"
            })
        )
    }
    
    /**
     * system_language字段（假值）
     */
    private fun addSystemLanguageField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("anatomic", LambdaValueProvider {
                "ggty_asd"
            })
        )
    }
    
    /**
     * gaid字段
     */
    private fun addGaidField(context: JsonBuilderContext) {
        context.addFieldBuilder(
            SimpleFieldBuilder("greet", LambdaValueProvider {
                ""
            })
        )
    }
}

