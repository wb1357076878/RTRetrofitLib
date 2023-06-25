package com.example.rtnewnetworklib.networkconfig

import com.rtmart.rtretrofitlib.RTRetrofitUrlPathTransformer
import com.rtmart.rtretrofitlib.md5

/**
 * @ClassName: RTUrlPathHandler
 * @Description: java类作用描述
 * @Author: WangBo
 * @Date: 2023/6/19 17:35
 */
class RTUrlPathHandler: RTRetrofitUrlPathTransformer {

    companion object {
        // mock api map<key,path>
        private val map = mapOf(Pair("login", "http://rtmart-mars-pdaapi-x.beta1.fn/app/login/employeeLogin"))
    }

    override fun pathForKey(pathKey: String): String {
        val path = map[pathKey]
        path?.let {
            return it
        }
        return pathKey
    }

    override fun queryParameters(url: String, requestBodyString: String): Map<String, String> {
        val paramMap:MutableMap<String,String> = mutableMapOf()
        paramMap["signature"] = (requestBodyString+"123456789").md5()
        paramMap["idempotent"] = (url+requestBodyString+System.currentTimeMillis()).md5()
        return paramMap
    }
}