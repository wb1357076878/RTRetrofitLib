package com.rtmart.rtretrofitlib

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * A flow call return type adapter create by [RTRetrofitFlowCallAdapterFactory]
 *
 * @ClassName: RTRetrofitFlowCallAdapter
 * @Description:
 * @Author: WangBo
 * @Date: 2023/6/19 14:14
 */
internal class RTRetrofitResponseCallAdapter<T>(private val responseType: Type) :
    CallAdapter<T, Flow<Response<T>>> {

    override fun responseType() = responseType


    override fun adapt(call: Call<T>): Flow<Response<T>> {
        return flow {
            emit(suspendCancellableCoroutine { continuation ->
                call.enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        continuation.resume(response)
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
                continuation.invokeOnCancellation { call.cancel() }
            })
        }
    }
}

internal class RTRetrofitResponseBodyCallAdapter<T>(private val responseType: Type) :
    CallAdapter<T, Flow<T>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<T>): Flow<T> {
        return flow {
            emit(suspendCancellableCoroutine { continuation ->
                call.enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        try {
                            continuation.resume(response.body()!!)
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
                continuation.invokeOnCancellation { call.cancel() }
            })
        }
    }
}