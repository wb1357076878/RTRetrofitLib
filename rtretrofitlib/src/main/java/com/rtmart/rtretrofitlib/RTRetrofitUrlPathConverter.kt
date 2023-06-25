package com.rtmart.rtretrofitlib

interface RTRetrofitUrlPathTransformer {
    /**
     * 下发接口pathKey 转成实际url path
     */
    fun pathForKey(pathKey: String): String

    /**
     * 某些原因，当我们需要给整个url path后面追加根据request body生成的特定查询参数
     *
     * example：
     * urlPath?signature=78291deaf8b5adc3f52fd91942b56beb&idempotent=f9e073523b340ad2359f544989c2bd58
     */
    fun queryParameters(url: String, requestBodyString: String): Map<String, String> = mapOf()
}

