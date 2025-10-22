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

    // finish ac 需要告知内部是否正在进行了ac的finish 里面会进行delay
    // 为了做差异化不要每个包都返回long，需要自己去改动一下这里的逻辑
    fun f(): Long
}
