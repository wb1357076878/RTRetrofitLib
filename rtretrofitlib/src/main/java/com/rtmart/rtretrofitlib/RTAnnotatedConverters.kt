package com.rtmart.rtretrofitlib

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @ClassName: RTAnnotatedConverters
 * @Description: 带注解的转换器
 * @Author: WangBo
 * @Date: 2023/6/8 10:30
 */

@Retention(AnnotationRetention.RUNTIME)
annotation class Moshi

@Retention(AnnotationRetention.RUNTIME)
annotation class Gson

@Retention(AnnotationRetention.RUNTIME)
annotation class SimpleXml

class RTAnnotatedConverterFactory private constructor(private val factories: Map<Class<out Annotation>, Converter.Factory>) :
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

        fun build(): RTAnnotatedConverterFactory {
            return RTAnnotatedConverterFactory(factories)
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