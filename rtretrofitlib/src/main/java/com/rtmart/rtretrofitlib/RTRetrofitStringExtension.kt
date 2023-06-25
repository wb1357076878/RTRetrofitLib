/**
 * Copyright (C), 2015-2023, 飞牛集达有限公司
 * FileName: RTRetrofitStringExtension
 * Author: WangBo
 * Date: 2023/6/19 18:11
 * Description: string 拓展函数
 * History:
 */
package com.rtmart.rtretrofitlib

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.md5(): String {
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val digest: ByteArray = instance.digest(this.toByteArray())
        val sb = StringBuffer()
        for (b in digest) {
            val i: Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}