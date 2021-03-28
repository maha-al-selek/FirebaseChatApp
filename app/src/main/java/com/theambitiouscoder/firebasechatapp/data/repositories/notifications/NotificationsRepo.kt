package com.theambitiouscoder.firebasechatapp.data.repositories.notifications

import com.theambitiouscoder.firebasechatapp.data.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

interface NotificationsRepo {

    suspend fun postNotification(notification: PushNotification): Response<ResponseBody>

}