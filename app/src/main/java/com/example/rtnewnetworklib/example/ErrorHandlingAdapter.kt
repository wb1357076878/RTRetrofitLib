/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: ErrorHandlingAdapter
 * Author: WangBo
 * Date: 2023/6/12 14:32
 * Description: addCallAdapter 使用
 * History:
 */
package com.example.rtnewnetworklib.example

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

interface MyCallback<T> {
    fun success(response: Response<T>)

    fun unauthenticated(response: Response<*>)

    fun clientError(response: Response<*>)

    fun serverError(response: Response<*>)

    fun networkError(e: IOException)

    fun unexpectedError(t: Throwable)
}

interface MyCall<T> {
    fun cancel()

    fun enqueue(callback: MyCallback<T>)

    fun clone(): MyCall<T>
}

class ErrorHandlingCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != MyCall::class.java) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException("MyCall must have generic type (e.g., MyCall<ResponseBody>)")
        }
        val responseType = getParameterUpperBound(0, returnType)
        val callbackExecutor = retrofit.callbackExecutor()
        return ErrorHandlingCallAdapter<Any>(responseType, callbackExecutor)
    }

    private class ErrorHandlingCallAdapter<R> constructor(
        private val responseType: Type,
        private val callbackExecutor: Executor?
    ): CallAdapter<R, MyCall<R>> {

        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<R>): MyCall<R> {
            return MyCallAdapter(call, callbackExecutor)
        }
    }
}

class MyCallAdapter<T>(private val call: Call<T>, private val callbackExecutor: Executor?): MyCall<T> {
    override fun cancel() {
        call.cancel()
    }

    override fun clone(): MyCall<T> {
        return MyCallAdapter(call.clone(), callbackExecutor)
    }

    override fun enqueue(callback: MyCallback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {

                when (response.code()) {
                    in 200 .. 299 -> {
                        callback.success(response)
                    }
                    401 -> {
                        callback.unauthenticated(response)
                    }
                    in 400..499 -> {
                        callback.clientError(response)
                    }
                    in 500..599 -> {
                        callback.serverError(response)
                    }
                    else -> {
                        callback.unexpectedError(RuntimeException("Unexpected response $response"))
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (t is IOException) {
                    callback.networkError(t)
                } else {
                    callback.unexpectedError(t)
                }
            }

        })
    }

}

// 使用
interface HttpBinService {
    @GET("/ip")
    fun getIp(): MyCall<Ip>
}

data class Ip(val origin: String)

fun main(args: Array<String>) {
    val retrofit =
        Retrofit.Builder()
            .baseUrl("http://httpbin.org")
            .addCallAdapterFactory(ErrorHandlingCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service = retrofit.create(HttpBinService::class.java)

    val ip = service.getIp()
    ip.enqueue(object : MyCallback<Ip> {
        override fun success(response: Response<Ip>) {
            println("Successful! ${response.body()?.origin}")
        }

        override fun unauthenticated(response: Response<*>) {
            println("UNAUTHENTICATED")
        }

        override fun clientError(response: Response<*>) {
            println("CLIENT ERROR: code = ${response.code()}, message:${response.message()}")
        }

        override fun serverError(response: Response<*>) {
            println("SERVER ERROR: code = ${response.code()}, message:${response.message()}")
        }

        override fun networkError(e: IOException) {
            println("network error: ${e.message}")
        }

        override fun unexpectedError(t: Throwable) {
            println("unexpected error: ${t.message}")
        }

    })
}