package com.theambitiouscoder.firebasechatapp.data.repositories.notifications

import com.theambitiouscoder.firebasechatapp.data.api.ApiServiceBuilder
import com.theambitiouscoder.firebasechatapp.data.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

class NotificationsRepoImpl : NotificationsRepo {

    override suspend fun postNotification(notification: PushNotification): Response<ResponseBody> {
        return ApiServiceBuilder.api.postNotification(notification)
    }

}