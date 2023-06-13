/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: InvocationMetrics
 * Author: WangBo
 * Date: 2023/6/12 17:47
 * Description: example for request metrics
 * History:
 */
package com.example.rtnewnetworklib.example

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url

class InvocationMetrics {

    interface Browse {

        @GET("/robots.txt")
        fun robots(): Call<ResponseBody>

        @GET("/favicon.ico")
        fun favicon(): Call<ResponseBody>

        @GET("/")
        fun home(): Call<ResponseBody>

        @GET
        fun page(@Url path: String): Call<ResponseBody>
    }

    class InvocationLogger : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val startNanos = System.nanoTime()
            val response = chain.proceed(request)
            val elapsedNanos = System.nanoTime() - startNanos

            val invocation = request.tag(Invocation::class.java)
            if (invocation != null) {
                println("${invocation.method().declaringClass.simpleName}.${invocation.method().name} ${invocation.arguments()} ${response.code()} (${elapsedNanos / 1e6} ms)")
            }

            return response
        }
    }
}

fun main() {
    val invocationLogger = InvocationMetrics.InvocationLogger()
    val okHttpClient = OkHttpClient.Builder().addInterceptor(invocationLogger).build()

    val retrofit =
        Retrofit.Builder()
            .baseUrl("https://square.com/")
            .callFactory(okHttpClient)
            .build()
    val browse = retrofit.create(InvocationMetrics.Browse::class.java)

    browse.robots().execute()
    browse.favicon().execute()
    browse.home().execute()
    browse.page("https://square.com/sitemap.xml").execute()
    browse.page("http://10.200.48.214:8000/test/home/data.json").execute()
}