package com.example.rtnewnetworklib.network

open class RTBaseParameter {
    /** 用户token，跟edp账号相对应*/
    var token: String = ""

    /** 设备id*/
    var deviceId: String = ""

    /** 操作系统类型：1：安卓 2：苹果*/
    val osType: String = "1"

    /** 操作系统版本*/
    var osVersionNo: String = "0.0.0"

    /** App版本*/
    var appVersionNo: String = "0.0.0"

    /** App分辨率尺寸*/
    var viewSize: String = "0*0"

    /** ip地址*/
    var ipAddr: String = "0.0.0"
    /** 用户cid*/
    /** 仓库编号 */
    var warehouseNo: String = ""

    /** storeId*/
    var storeId: String = ""

    init {
        appVersionNo = "9.9.9"
        deviceId = "-1129544574"
        ipAddr = "10.0.2.15"
        osVersionNo = "10"
        storeId = "1001"
        token = "41763987-cdd4-43b9-af45-1cfbdbca3340"
        viewSize = "1440*2560"
        warehouseNo = "10"
    }
}
