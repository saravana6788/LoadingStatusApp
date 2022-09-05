package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if(intent.hasExtra("fileName")){
            repository_name.text = intent.getStringExtra("fileName")
        }

        if(intent.hasExtra("downloadStatus")){
            repository_download_status.text = intent.getStringExtra("downloadStatus")
        }

        back_to_home_button.setOnClickListener {
            val mainIntent = Intent(applicationContext,MainActivity::class.java)
            startActivity(mainIntent)
        }

    }

}
