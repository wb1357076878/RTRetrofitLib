package com.example.rtnewnetworklib

import android.app.Application
import com.example.rtnewnetworklib.network.RTUrlPathHandler
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.rtmart.rtretrofitlib.RTRetrofitManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

/**
 * @ClassName: RTApplication
 * @Description: java类作用描述
 * @Author: WangBo
 * @Date: 2023/6/14 13:50
 */
class RTApplication: Application() {

    companion object {
        lateinit var instance: RTApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Logger.addLogAdapter(AndroidLogAdapter())
        RTRetrofitManager.newBuilder()
            .register(this)
            .baseUrl(null)
            .timeout(30)
            .urlPathTransform(RTUrlPathHandler())
            .build()

    }
}