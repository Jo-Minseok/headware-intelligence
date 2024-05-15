package com.headmetal.headwareintelligence

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService(){
    private val tag = "FCM MESSAGE"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationManager = NotificationManagerCompat.from(getApplicationContext())
        var builder:NotificationCompat.Builder?=null
        var CHANNEL_ID:String = remoteMessage.messageId.toString()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager.getNotificationChannel(CHANNEL_ID)==null){
                val channel = NotificationChannel(CHANNEL_ID,"channel", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            builder = NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
        }
        else{
            builder = NotificationCompat.Builder(getApplicationContext())
        }
        val title:String = remoteMessage.notification?.title.toString()
        val message:String = remoteMessage.notification?.body.toString()
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setContentTitle(title)
        builder.setContentText(message)
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
        notificationManager.notify(1,notification)

        // 수신한 데이터 처리
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
        // token을 서버로 전송
    }
}
