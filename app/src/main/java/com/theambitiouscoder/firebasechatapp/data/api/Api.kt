package com.theambitiouscoder.firebasechatapp.data.api

import com.theambitiouscoder.firebasechatapp.data.models.PushNotification
import com.theambitiouscoder.firebasechatapp.ui.utils.Constants.CONTENT_TYPE
import com.theambitiouscoder.firebasechatapp.ui.utils.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {

    @Headers("Authorization: key=$SERVER_KEY", "Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

}