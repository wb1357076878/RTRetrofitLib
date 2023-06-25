package com.example.rtnewnetworklib.login.service

import com.example.rtnewnetworklib.login.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

const val kLogin = "login"

interface RTLoginService {

    @POST
    fun getLogin(@Url path: String, @Body para: RTLoginParameter) : Call<ResponseBody>

    @POST(kLogin)
    fun getLogin(@Body para: RTLoginParameter) : Flow<RTLoginResponse>
}