package com.feiniu.rtnetworklib.network

import android.icu.text.IDNA
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

public interface ApiService {

    @GET("test/home/data.json")
    fun getJsonData(): Call<FNBaseResponse<FNHandleInfo>>
}