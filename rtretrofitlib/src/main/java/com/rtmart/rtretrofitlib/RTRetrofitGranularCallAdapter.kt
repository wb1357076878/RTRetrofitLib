/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: RTRetrofitRefineCallAdapter
 * Author: WangBo
 * Date: 2023/6/13 08:15
 * Description: 精细化返回自定义MyCall<T>的适配器
 * History:
 */
package com.rtmart.rtretrofitlib

import okhttp3.ResponseBody
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

/**
 * A callback which offers granular callbacks for various conditions.
 */
interface RTCallback<T> {
    /** Called for [200, 300) responses. */
    fun success(response: Response<T>)

    /** Called for 401 responses. */
    fun unauthenticated(response: Response<*>)

    /** Called for [400, 500) responses, except 401. */
    fun clientError(response: Response<*>)

    /** Called for [500, 600) response. */
    fun serverError(response: Response<*>)

    /** Called for network errors while making the call. */
    fun networkError(e: IOException)

    /** Called for unexpected errors while making the call. */
    fun unexpectedError(t: Throwable)
}

/**
 * 用来取代retrofit 原生call<T>
 *
 * 颗粒度更高的callback view by [RTCallback]
 */
interface RTCall<T> {
    /**
     * Cancel this call. An attempt will be made to cancel in-flight calls, and if the call has not
     * yet been executed it never will be.
     */
    fun cancel()
    /**
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     */
    fun enqueue(callback: RTCallback<T>)
    /**
     * Create a new, identical call to this one which can be enqueued or executed even if this call
     * has already been.
     */
    fun clone(): RTCall<T>
}

/**
 * create A granular call adapter factory
 *
 * 构造方式：
 * ```kotlin
 * RTRetrofitGranularCallAdapterFactory.create()
 * ````
 */
internal class RTRetrofitGranularCallAdapterFactory private constructor(): CallAdapter.Factory() {

    companion object {
        fun create() = RTRetrofitGranularCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != RTCall::class.java) {
            return null
        }
        // 判断是否是范型类型
        if (returnType !is ParameterizedType) {
            throw IllegalStateException("MyCall must have generic type (e.g., MyCall<ResponseBody>)")
        }
        val responseType = getParameterUpperBound(0, returnType)
        val callbackExecutor = retrofit.callbackExecutor()
        return RTRetrofitGranularCallAdapter<Any>(responseType, callbackExecutor)
    }

    private class RTRetrofitGranularCallAdapter<R> constructor(
        private val responseType: Type,
        private val callbackExecutor: Executor?
    ) : CallAdapter<R, RTCall<R>> {

        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<R>): RTCall<R> {
            return RTGranularCallAdapter(call, callbackExecutor)
        }
    }
}

private class RTGranularCallAdapter<T>(
    private val call: Call<T>,
    private val callbackExecutor: Executor?
) : RTCall<T> {
    override fun cancel() {
        call.cancel()
    }

    override fun clone(): RTCall<T> {
        return RTGranularCallAdapter(call.clone(), callbackExecutor)
    }

    override fun enqueue(callback: RTCallback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {

                when (response.code()) {
                    in 200..299 -> {
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