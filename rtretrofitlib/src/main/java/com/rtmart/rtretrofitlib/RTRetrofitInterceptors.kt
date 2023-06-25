/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: RTRetrofitInterceptors
 * Author: WangBo
 * Date: 2023/6/8 14:32
 * Description: 拦截器管理类
 * History:
 */
package com.rtmart.rtretrofitlib

import android.util.Log
import com.orhanobut.logger.Logger
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import retrofit2.Invocation

internal class RTUrlPathInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url()
        val path = request.url().encodedPath()
        val buffer = Buffer()
        request.body()?.writeTo(buffer)
        val requestBodyContent = buffer.readUtf8()
        // 打印 RequestBody 的内容
        Logger.d("RequestBody Content:\n$requestBodyContent")

        if (url.host() == RTRetrofitManager.tempHost) {
            val pathHandler = RTRetrofitManager.urlPathTransformer()
            val newUrlString = pathHandler.pathForKey(path.drop(1))
            val urlBuilder = HttpUrl.parse(newUrlString)?.newBuilder()
            val params = pathHandler.queryParameters(newUrlString, requestBodyContent)
            for ((key, value) in params) {
                urlBuilder?.addQueryParameter(key, value)
            }
            val finalUrl = urlBuilder?.build()
            finalUrl?.let {
                request = request.newBuilder().url(it).build()
            }
        }

        val response = chain.proceed(request)

        return response
    }
}

internal class RTLoggerInterceptor : Interceptor {

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