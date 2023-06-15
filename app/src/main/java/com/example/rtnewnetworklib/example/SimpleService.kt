/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: SimpleService
 * Author: WangBo
 * Date: 2023/6/13 15:28
 * Description:
 * History:
 */
package com.example.rtnewnetworklib.example

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.logging.Logger

class SimpleService {
    companion object {
        const val API_URL = "https://api.github.com"
    }

    data class Contributor(val login: String, val contributions: Int)

    interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        fun contributors(
            @Path("owner") owner: String,
            @Path("repo") repo: String
        ): Call<List<Contributor>>
    }
}

fun main(args: Array<String>) {
    val logger = Logger.getGlobal()
    logger.info("hello logger")

    val retrofit = Retrofit.Builder()
        .baseUrl(SimpleService.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gitHub = retrofit.create(SimpleService.GitHub::class.java)

    val call = gitHub.contributors("wb1357076878", "RTRetrofitLib")
    val request = call.request()
    logger.info("request url = ${request.url()}")
    val contributors = call.execute().body()
    contributors?.let {
        it.forEach { contributor ->
            logger.info("${contributor.login} (${contributor.contributions})")
        }
    }
}