/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: RTRetrofitFlowCallAdapterFactory
 * Author: WangBo
 * Date: 2023/6/19 11:26
 * Description: retrofit return call adapter factory class
 * History:
 */
package com.rtmart.rtretrofitlib

import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A [CallAdapter.Factory] which create a flow return type.
 */
internal class RTRetrofitFlowCallAdapterFactory private constructor() : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException("Flow return type must have generic type (e.g., Flow<ResponseBody>)")
        }

        val responseType = getParameterUpperBound(0, returnType)
        val rawFlowType = getRawType(responseType)

        return if (rawFlowType == Response::class.java) {
            check(responseType is ParameterizedType) { "Response must be parameterized as Response<Foo> or Response<out Foo>" }
            RTRetrofitResponseCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else {
            RTRetrofitResponseBodyCallAdapter<Any>(responseType)
        }
    }

    companion object {
        fun create() = RTRetrofitFlowCallAdapterFactory()
    }
}