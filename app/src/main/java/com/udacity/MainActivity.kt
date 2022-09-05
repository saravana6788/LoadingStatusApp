package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var selectedUrl:String

    var downloadStatus = "fail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel()
        radio_group.setOnCheckedChangeListener { _, checkedId ->
            selectedUrl = when(checkedId){
                R.id.glide_download -> DOWNLOADURLS.DOWNLOAD_GLIDE.url
                R.id.udacity_download -> DOWNLOADURLS.DOWNLOAD_UDACITY.url
                else -> DOWNLOADURLS.DOWNLOAD_RETROFIT.url
            }
        }

        custom_button.setOnClickListener {

            try {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    download()
                else
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionInfo.PROTECTION_DANGEROUS
                    )
            }catch (exception:Exception){
                Log.e("MainActivity","Exception occurred while downloading")
            }

        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(downloadID == id){
                downloadStatus = "Success"
                custom_button.buttonState = ButtonState.Completed
                createNotification()
            }
        }
    }

    private fun download() {
        if(this::selectedUrl.isInitialized) {
            custom_button.buttonState = ButtonState.Loading
            val request =
                DownloadManager.Request(Uri.parse(selectedUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/repository.zip")

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        downloadStatus = "Success"
                        Toast.makeText(this, "Download Successful!!", Toast.LENGTH_LONG).show()
                    }
                    DownloadManager.STATUS_FAILED -> {
                        downloadStatus = "fail"
                        Toast.makeText(this, "Download Failed", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Log.e(
                            "Main Activity",
                            cursor.getColumnIndex(DownloadManager.COLUMN_REASON).toString()
                        )
                    }
                }
            }
        }else{
            Toast.makeText(this,"Select the library to download!", Toast.LENGTH_LONG).show()
        }


    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }

    enum class DOWNLOADURLS(val url:String) {
        DOWNLOAD_GLIDE("https://github.com/bumptech/glide"),
        DOWNLOAD_UDACITY("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"),
        DOWNLOAD_RETROFIT("https://github.com/square/retrofit")
    }


    fun createNotification(){
        val notificationManager  = ContextCompat.getSystemService(this,
        NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(this,selectedUrl,downloadStatus)
    }


    fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID,"Downloader",NotificationManager.IMPORTANCE_LOW)
        notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

}
