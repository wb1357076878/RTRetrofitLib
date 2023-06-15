/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: RTRetrofitInterceptors
 * Author: WangBo
 * Date: 2023/6/8 14:32
 * Description: 拦截器管理类
 * History:
 */
package com.rtmart.rtretrofitlib

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class RTUrlPathInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        return response
    }
}

class RTLoggerInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startNanos = System.nanoTime()
        val response = chain.proceed(request)
        val elapsedNanos = System.nanoTime() - startNanos

        val invocation = request.tag(Invocation::class.java)
        if (invocation != null) {
            Logger.d(
                "request url: ${response.request().url()}\n" +
                        "invocation By ${invocation.method().declaringClass.simpleName}.${invocation.method().name}\n" +
                        "arguments: ${invocation.arguments()} ${response.code()}\n" +
                        "Response Total Time = ${elapsedNanos / 1e6} ms"
            )
        }

        return response
    }
}