package com.headmetal.headwareintelligence

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService(){
    private val tag = "FCM MESSAGE"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 알림 데이터 처리
        remoteMessage.data.isNotEmpty().let{
            Log.d(tag,"data: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let{
            Log.d(tag, "notification: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag,"Token: $token")
    }
}
