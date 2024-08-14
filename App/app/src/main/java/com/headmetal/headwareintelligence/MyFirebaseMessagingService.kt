package com.headmetal.headwareintelligence

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService(){
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 작업 표시줄 알림으로 왔을 경우 Foreground
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        var builder: NotificationCompat.Builder? = null
        val channelId: String = remoteMessage.messageId.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId,"channel",NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            builder = NotificationCompat.Builder(applicationContext,channelId)
        } else {
            builder = NotificationCompat.Builder(applicationContext)
        }
        val title:String = remoteMessage.notification?.title.toString()
        val message:String = remoteMessage.notification?.body.toString()
        builder.setSmallIcon(R.drawable.helmet).setContentTitle(title).setContentText(message)
        val notification = builder.build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify((System.currentTimeMillis()/1000).toInt(),notification)

        // 수신한 데이터 처리
        remoteMessage.data.isNotEmpty().let{
            Log.d("FCM MESSAGE","data: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let{
            Log.d("FCM MESSAGE", "notification: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM MESSAGE","Token: $token")
        // token을 서버로 전송
    }
}
