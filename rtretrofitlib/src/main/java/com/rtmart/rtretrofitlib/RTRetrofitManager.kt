package com.rtmart.rtretrofitlib

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Objects
import java.util.concurrent.TimeUnit

/**
 * @ClassName: RTRetrofitManager
 * @Description: 管理retrofit单例和相关配置
 * @Author: WangBo
 * @Date: 2023/6/6 14:28
 */
class RTRetrofitManager internal constructor(builder: Builder) {
    /**
     * Context 提供了访问应用程序资源、启动活动和服务、获取应用程序信息、访问系统服务、发布广播以及创建视图和布局的功能
     *
     * set by call [Builder.register]
     */
    val mContext: Context? = builder.mContext

    /**
     * 管理baseUrl
     *
     * set by call [Builder.baseUrl]
     */
    val baseUrl: HttpUrl = builder.baseUrl

    /**
     * 超时时间：默认30s
     */
    val timeout: Long = builder.timeout

    /**
     * 通过下发接口pathKey转换得到完整的url path
     */
    internal val urlPathTransformer: RTRetrofitUrlPathTransformer = builder.urlPathTransformer

    /**
     * 全局client http请求端
     * 用于配置请求拦截器，请求header等
     */
    private lateinit var httpClient: OkHttpClient

    /**
     * 全局retrofit实例
     */
    private lateinit var retrofit: Retrofit

    /**
     * secondary constructors.
     *
     * @constructor create a retrofit manager class
     */
    constructor() : this(Builder())

    @SuppressLint("StaticFieldLeak")
    companion object {
        private var instance: RTRetrofitManager = RTRetrofitManager()
        internal const val tempHost = "www.example.com"
        internal const val PlaceholderBaseUrl = "http://$tempHost"

        /**
         * Suggested instantiation method
         *
         * 代码示例:
         * ```kotlin
         * RTRetrofitManager.newBuilder()
         *      .register(this)
         *      .baseUrl("")
         *      .build()
         * ```
         * finally must call [Builder.build]
         */
        fun newBuilder(): Builder = Builder(instance)

        /**
         * 获取service请求
         *
         * 代码示例:
         * ```kotlin
         * val service = RTRetrofitManager.getRequestService(ExampleService::class.java)
         * ```
         * than：通过调用ExampleService的interface接口，来发起请求
         */
        fun <T> getRequestService(serviceClass: Class<T>): T {
            Objects.requireNonNull(instance.retrofit, "retrofit == null")
            return instance.retrofit.create(serviceClass)
        }

        /**
         * 获取path
         */
        internal fun urlPathTransformer() = instance.urlPathTransformer

        /**
         * 全局配置httpClient & retrofit instance
         *
         * call by [Builder.build]
         */
        private fun initClient() {
            // 初始化 httpClient
            instance.httpClient =
                OkHttpClient.Builder()
                    .connectTimeout(instance.timeout, TimeUnit.SECONDS)
                    .readTimeout(instance.timeout, TimeUnit.SECONDS)
                    .addInterceptor(RTUrlPathInterceptor())
                    .addInterceptor(RTLoggerInterceptor())
                    .followRedirects(false)
                    .build()
            // 初始化 retrofit
            instance.retrofit =
                Retrofit.Builder()
                    .baseUrl(instance.baseUrl)
                    .client(instance.httpClient)
                    .addCallAdapterFactory(RTRetrofitGranularCallAdapterFactory.create())
                    .addCallAdapterFactory(RTRetrofitFlowCallAdapterFactory.create())
                    .addConverterFactory(
                        RTAnnotatedConverterFactory.Builder()
                            .add(Gson::class.java, GsonConverterFactory.create())
                            .add(Moshi::class.java, MoshiConverterFactory.create()).build()
                    )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
    }

    class Builder() {

        internal var mContext: Context? = null
        internal var baseUrl: HttpUrl = HttpUrl.get(PlaceholderBaseUrl)
        internal var timeout: Long = 30
        internal var urlPathTransformer: RTRetrofitUrlPathTransformer =
            object : RTRetrofitUrlPathTransformer {
                override fun pathForKey(pathKey: String): String {
                    return pathKey
                }
            }

        internal constructor(retrofitManager: RTRetrofitManager) : this() {
            this.mContext = retrofitManager.mContext
            this.baseUrl = retrofitManager.baseUrl
        }

        /**
         * 注册上下文
         */
        fun register(context: Context) = apply {
            mContext = context
        }

        /**
         * 根据url string 设置[RTRetrofitManager.baseUrl]
         *
         * call [baseUrl]
         */
        fun baseUrl(baseUrl: String) = apply {
            baseUrl(HttpUrl.get(baseUrl))
        }

        /**
         * 设置baseUrl, 默认情况下为null，走配置@Path
         *
         * 如何不为null，则严格按照http://host/ 配置url，否则会抛出错误
         */
        fun baseUrl(baseUrl: HttpUrl?) = apply {
            baseUrl?.let {
                // url格式判断
                val pathSegments = baseUrl.pathSegments()
                if ("" != pathSegments[pathSegments.size - 1]) {
                    throw IllegalArgumentException("baseUrl must end in /: $baseUrl")
                }
                this.baseUrl = baseUrl
            }
        }

        /**
         * 设置超时时间
         */
        fun timeout(timeout: Long) = apply {
            this.timeout = timeout
        }

        /**
         * 设置url path 转换器，用于下发接口pathKey => full path
         *
         * default return path Key themself
         */
        fun urlPathTransform(pathTransformer: RTRetrofitUrlPathTransformer) = apply {
            this.urlPathTransformer = pathTransformer
        }

        fun build(): RTRetrofitManager {
            instance = RTRetrofitManager(this)
            initClient()
            return instance
        }
    }
}