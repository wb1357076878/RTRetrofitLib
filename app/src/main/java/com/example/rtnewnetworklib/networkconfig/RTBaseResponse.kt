package com.example.rtnewnetworklib.networkconfig

/**
 * @ClassName: RTBaseResponse
 * @Description: java类作用描述
 * @Author: WangBo
 * @Date: 2023/6/25 16:02
 */
open class RTBaseResponse {
    var elapsedTime: Long = 0
    var errorCode: Int = 0
    var errorDesc: String = ""
    var logCode: String = ""
    var serverTime: Long = 0
    var success: Boolean = false
}