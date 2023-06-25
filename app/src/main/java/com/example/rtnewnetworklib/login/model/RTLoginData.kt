package com.example.rtnewnetworklib.login.model

import com.example.rtnewnetworklib.networkconfig.*

/**
 * @ClassName: RTLoginData
 * @Description: java类作用描述
 * @Author: WangBo
 * @Date: 2023/6/14 16:18
 */

class RTLoginParameter: RTBaseParameter() {
    var data = RTLoginData()
}

class RTLoginData {
    var employeeNo = "15721096991"
    var passWord = "123456"
    var storeId = "1001"
    var warehouseNo = "10"
}

class RTLoginResponse: RTBaseResponse() {
    var data = RTLoginSuccessData()
}

class RTLoginSuccessData {
    var employeeName: String = ""
    var employeeNo = ""
    var mobile = ""
    var sectionName = ""
    var sectionNo = ""
    var sectionNoList = ""
}