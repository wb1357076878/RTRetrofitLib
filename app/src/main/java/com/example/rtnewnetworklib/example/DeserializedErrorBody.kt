/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: DeserializedErrorBody
 * Author: WangBo
 * Date: 2023/6/12 10:00
 * Description: 反序列化error body
 * History:
 */
package com.example.rtnewnetworklib.example

import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class DeserializedErrorBody {

    class User {}

    interface Service {
        @GET("/user")
        fun getUser(): Call<User>
    }

    data class ErrorBody(val msg: String)

}

fun main(args: Array<String>) {
    val server = MockWebServer()
    server.start()
    server.enqueue(
        MockResponse()
            .setResponseCode(404)
            .setBody("{\"msg\":\"Unable to locate resource\"}")
    )

    val retrofit =
        Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service = retrofit.create(DeserializedErrorBody.Service::class.java)

    val response = service.getUser().execute()

    val errorConverter: Converter<ResponseBody, DeserializedErrorBody.ErrorBody> =
        retrofit.responseBodyConverter(DeserializedErrorBody.ErrorBody::class.java, arrayOf())

    val errorBody = errorConverter.convert(response.errorBody())

    println("Error: ${errorBody?.msg}")

    server.shutdown()
}


