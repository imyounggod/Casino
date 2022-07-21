package com.example.casino.data.impl.server.odds_api.interceptors

import com.example.casino.data.impl.server.odds_api.ApiConstants
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        try {
            var request = chain.request()
            val url = request.url.newBuilder()
                .addQueryParameter(ApiConstants.ODDS_HEADER_KEY, ApiConstants.ODDS_API_KEY)
                .build()
            request = request.newBuilder().url(url).build()
            return chain.proceed(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return chain.proceed(originalRequest)
    }
}
