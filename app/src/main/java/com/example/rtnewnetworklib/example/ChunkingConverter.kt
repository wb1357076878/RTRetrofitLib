/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: ChunkingConverter
 * Author: WangBo
 * Date: 2023/6/8 17:23
 * Description:
 * History:
 */
package com.example.rtnewnetworklib.example

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.lang.reflect.Type

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Chunked

class ChunkingConverterFactor : Converter.Factory() {

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        var isBody = false
        var isChunked = false
        for (annotation in parameterAnnotations) {
            isBody = isBody || annotation is Body
            isChunked = isChunked || annotation is Chunked
        }

        if (!isBody || !isChunked) {
            return null
        }

        val delegate = retrofit.nextRequestBodyConverter<Any>(
            this, type, parameterAnnotations, methodAnnotations
        )

        return Converter<Any, RequestBody> { value ->
            val realBody = delegate.convert(value)

            object : RequestBody() {
                override fun contentType(): MediaType? {
                    return realBody?.contentType()
                }

                override fun writeTo(sink: BufferedSink) {
                    realBody?.writeTo(sink)
                }
            }
        }
    }
}

data class Repo(
    val owner: String, val name: String
)

interface ApiService {

    @POST("/")
    fun sendNormal(@Body repo: Repo): Call<ResponseBody>

    @POST("/")
    fun sendChunked(@Chunked @Body repo: Repo): Call<ResponseBody>
}

fun main(args: Array<String>) {
    println("hello chunk convertor")
    val server = MockWebServer()
    server.enqueue(MockResponse())
    server.enqueue(MockResponse())
    server.start()

    val retrofit =
        Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(ChunkingConverterFactor())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val apiService = retrofit.create(ApiService::class.java)

    val repo = Repo("feiniu", "network")

    apiService.sendNormal(repo).execute()
    val normalRequest = server.takeRequest()
    println("Normal @Body Transfer-Encoding: ${normalRequest.headers.get("Transfer-Encoding")}")

    apiService.sendChunked(repo).execute()
    val chunkedRequest = server.takeRequest()
    println("@Chunked @Body Transfer-Encoding: ${chunkedRequest.headers.get("Transfer-Encoding")}")

    server.shutdown()
}