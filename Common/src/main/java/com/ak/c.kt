package com.ak

/**
 * Date：2025/8/22
 * Describe:
 *
 */
// todo  修改这个类的名字和路径
//  这个类需要keep 住，添加的该模块混淆规则中
// 同时这个类的具体实现为了做差异化不一定都由这个接口来实现也可以进行拆分
interface c {
    // 埋点上报
    fun a(string: String, value: String)

    // 内部会传广告组装好的数据，
    // 只需要与外部的TBA的公共数据进行组装即可
    fun c(string: String)

    // ref post 记得做上报成功后不在上报的逻辑
    fun d(ref: String)

    // finish ac 需要告知内部是否正在进行了ac的finish 里面会进行delay
    // 为了做差异化不要每个包都返回long，需要自己去改动一下这里的逻辑
    fun f(): Long

    //根据tab获取外面的字符串 可以外面传进来，也可以直接在里面写死，
    //
    fun e(string: String): String
}
