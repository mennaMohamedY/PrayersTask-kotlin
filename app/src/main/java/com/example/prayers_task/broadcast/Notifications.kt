package com.example.prayers_task.broadcast


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.prayers_task.MainActivity
import com.example.prayers_task.R

class Notifications {
    @RequiresApi(Build.VERSION_CODES.O)
    fun makeNotification(context: Context,uniqueCode:Int){
        val channelID="CHANNEL_ID_PRAYERS_${uniqueCode}"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel: NotificationChannel = NotificationChannel(
                channelID,"my channel",NotificationManager.IMPORTANCE_DEFAULT)

            val manager: NotificationManager? =
                ContextCompat.getSystemService(context, NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
        var notfBuilder: NotificationCompat.Builder= NotificationCompat.Builder(context,channelID)
        notfBuilder.setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("تذكير بموعد الصلاه")
            .setContentText("حات الان موعد الصلاه")
            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var notificationCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)

        val intent= Intent(context,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingint= PendingIntent.getActivity(context,uniqueCode
            ,intent, PendingIntent.FLAG_MUTABLE)

        notfBuilder.setContentIntent(pendingint)

        var notificationManag: NotificationManager? =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        var notificationChannel: NotificationChannel = notificationManag!!.getNotificationChannel(channelID)
        if(notificationChannel == null){
            val importance= NotificationManager.IMPORTANCE_HIGH
            notificationChannel= NotificationChannel(
                channelID,
                "time to Pray",importance)
        }
        notificationManag?.createNotificationChannel(notificationChannel)
        notificationManag?.notify(2,notfBuilder.build())

    }
}