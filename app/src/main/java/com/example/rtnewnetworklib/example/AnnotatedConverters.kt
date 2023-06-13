package com.example.rtnewnetworklib.example

import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.simpleframework.xml.Attribute
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import java.lang.reflect.Type


/**
 * @ClassName: AnnotatedConverters
 * @Description: java类作用描述
 * @Author: WangBo
 * @Date: 2023/6/6 08:55
 */
class AnnotatedConverters {

}

class AnnotatedConverterFactory private constructor(private val factories: Map<Class<out Annotation>, Converter.Factory>) :
    Converter.Factory() {

    class Builder {
        private val factories: MutableMap<Class<out Annotation>, Converter.Factory> =
            LinkedHashMap()

        fun add(cls: Class<out Annotation>, factory: Converter.Factory) = apply {
            if (cls == null) {
                throw NullPointerException("cls == null")
            }
            if (factory == null) {
                throw NullPointerException("factory == null")
            }
            factories[cls] = factory
        }

        fun build(): AnnotatedConverterFactory {
            return AnnotatedConverterFactory(factories)
        }
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            val annotationType = annotation.annotationClass.java
            val factory = factories[annotationType]
            if (factory != null) {
                return factory.responseBodyConverter(type, annotations, retrofit)
            }
        }
        return null
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        for (annotation in parameterAnnotations) {
            val annotationType = annotation.annotationClass.java
            val factory = factories[annotationType]
            if (factory != null) {
                return factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
            }
        }
        return null
    }
}

@Retention(AnnotationRetention.RUNTIME)
annotation class Moshi

@Retention(AnnotationRetention.RUNTIME)
annotation class Gson

@Retention(AnnotationRetention.RUNTIME)
annotation class SimpleXml

data class Library(
    @field:Attribute(name = "name") var name: String = ""
)

interface Service {
    @GET("/")
    @Moshi
    fun exampleMoshi(): Call<Library>

    @GET("/")
    @Gson
    fun exampleGson(): Call<Library>

    @GET("/")
    @SimpleXml
    fun exampleSimpleXml(): Call<Library>

    @GET("/")
    fun exampleDefault(): Call<Library>
}

fun main() {
    println("hello world")
    val server = MockWebServer()
    server.start()
    server.enqueue(MockResponse().setBody("<user name=\"SimpleXML\"/>"))
    server.enqueue(MockResponse().setBody("{\"name\": \"Moshi\"}"))
    server.enqueue(MockResponse().setBody("{\"name\": \"Gson\"}"))
    server.enqueue(MockResponse().setBody("{\"name\": \"Gson\"}"))

    val gsonConverterFactory = GsonConverterFactory.create()
    val moshiConverterFactory = MoshiConverterFactory.create()
    val xmlConverterFactory = SimpleXmlConverterFactory.create()
    val retrofit = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(
            AnnotatedConverterFactory.Builder()
                .add(SimpleXml::class.java, xmlConverterFactory)
                .add(Moshi::class.java, moshiConverterFactory)
                .add(Gson::class.java, gsonConverterFactory)
                .build())
        .addConverterFactory(gsonConverterFactory)
        .build()

    val service = retrofit.create(Service::class.java)

    val library3 = service.exampleSimpleXml().execute().body()
    println("Library 3: ${library3?.name}")
    val library1 = service.exampleMoshi().execute().body()
    println("Library 1: ${library1?.name}")

    val library2 = service.exampleGson().execute().body()
    println("Library 2: ${library2?.name}")


    val library4 = service.exampleDefault().execute().body()
    println("Library 3: ${library4?.name}")

    server.shutdown()
}