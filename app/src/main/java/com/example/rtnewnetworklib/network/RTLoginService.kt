package com.example.rtnewnetworklib.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface RTLoginService {

    @POST
    fun getLogin(@Url path: String, @Body para: RTLoginData) : Call<ResponseBody>
}