package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


const val REQUEST_CODE = 1001

    fun NotificationManager.sendNotification(applicationContext: Context,fileName:String,downloadStatus:String){
        val intent = Intent(applicationContext,DetailActivity::class.java)
        intent.putExtra("fileName",fileName)
        intent.putExtra("downloadStatus",downloadStatus)
        val pendingIntent = PendingIntent.getActivity(applicationContext, REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(applicationContext, "channelId")
            .setContentTitle("Repository Download Status")
            .setContentText("$fileName Downloaded Successfully!!")
            .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_baseline_cloud_download_24,"View Details",pendingIntent)

        notify(1,builder.build())

    }
