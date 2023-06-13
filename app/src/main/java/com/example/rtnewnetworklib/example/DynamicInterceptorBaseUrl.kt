/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: DynamicInterceptorBaseUrl
 * Author: WangBo
 * Date: 2023/6/12 10:29
 * Description:
 * History:
 */
package com.example.rtnewnetworklib.example

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import java.io.IOException

class DynamicInterceptorBaseUrl {

    interface Pop {

        @GET("robots.txt")
        fun robots(): Call<ResponseBody>
    }

    object HostSelectionInterceptor : Interceptor {
        @Volatile
        private var host: String = ""

        fun setHost(host: String) {
            this.host = host
        }

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            if (host.isNotEmpty()) {
                val newUrl = request.url().newBuilder().host(host).build()
                request = request.newBuilder().url(newUrl).build()
            }
            return chain.proceed(request)
        }
    }
}

fun main() {

    val interceptor = DynamicInterceptorBaseUrl.HostSelectionInterceptor
    val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://www.github.com/")
        .callFactory(okHttpClient)
        .build()

    val pop = retrofit.create(DynamicInterceptorBaseUrl.Pop::class.java)
    val response1 = pop.robots().execute()
    println("Response from: ${response1.raw().request().url()}")
    println(response1.body()?.string())

    interceptor.setHost("www.pepsi.com")
    val response2 = pop.robots().execute()
    println("Response from: ${response2.raw().request().url()}")
    println(response2.body()?.string())
}