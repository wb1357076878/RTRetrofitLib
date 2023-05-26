package com.feiniu.rtnetworklib.network

data class FNBaseResponse<T> (
    var code: Int = 0,
    var msg: String = "",
    var data: T? = null
)
