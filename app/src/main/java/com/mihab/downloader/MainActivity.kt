package com.mihab.downloader

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mihab.downloader.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var downloadID: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.btnStartDownload.setOnClickListener {
            Toast.makeText(it.context, "Started", Toast.LENGTH_SHORT).show()
            beginDownload()
            binding.btnStartDownload.visibility = View.GONE
        }

    }

    @SuppressLint("Range")
    fun beginDownload() {

        val url = "https://file-examples.com/storage/fe1207564e65327fe9c8723/2017/04/file_example_MP4_1280_10MG.mp4"
        var fileName = url.substring(url.lastIndexOf('/') + 1)
        fileName = fileName.substring(0, 1).uppercase().plus(fileName.substring(1))

        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setTitle(fileName)
            .setDescription("Downloading")

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)

        var isFinishedDownload = false
        var progress: Int

        Log.d("Mihab", "BABu")

        CoroutineScope(Default).launch {
            while (!isFinishedDownload) {
                var abc = binding.progressBar.progress
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_FAILED -> {
                            isFinishedDownload = true
                            break
                        }
                        DownloadManager.STATUS_PAUSED -> break
                        DownloadManager.STATUS_PENDING -> break
                        DownloadManager.STATUS_RUNNING -> {
                            val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (total >= 0) {
                                val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                progress = ((downloaded * 100L) / total).toInt()
                                //Toast.makeText(applicationContext, "progress : $progress", Toast.LENGTH_SHORT).show()
                                /*if (abc < 100) {
                                    abc += 1
                                    binding.progressBar.progress = abc
                                }*/
                                binding.progressBar.progress = progress
                                Log.d("MainActivity","progress : $progress")
                                Log.d("MainActivity","ABC : $abc")

                            }
                            break
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progress =  100
                            isFinishedDownload = true
                            binding.progressBar.progress = progress
                            //Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_SHORT).show()
                            break
                        }
                    }
                }
            }
        }


    }


}