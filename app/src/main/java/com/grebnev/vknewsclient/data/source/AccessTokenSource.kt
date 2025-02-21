package com.grebnev.vknewsclient.data.source

import com.vk.id.VKID

class AccessTokenSource {
    fun getAccessToken(): String {
        val token = VKID.instance.accessToken?.token ?: throw IllegalStateException("Token is null")
        return token
    }
}