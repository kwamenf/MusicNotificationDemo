package com.bosolar.musicdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bosolar.musicdemo.CreateNotification.createNotification
import com.bosolar.musicdemo.services.OnClearFromRecentService
import java.util.*

class MainActivity : AppCompatActivity(), Playable {
    private lateinit var play: ImageButton
    private lateinit var title: TextView
    private var notificationManager: NotificationManager? = null
    private var tracks: MutableList<Track> = ArrayList()
    private var position = 0
    var isPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        play = findViewById(R.id.play)
        title = findViewById(R.id.title)
        populateTracks()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
            startService(Intent(baseContext, OnClearFromRecentService::class.java))
        }
        play.setOnClickListener {
            if (isPlaying) {
                onTrackPause()
            } else {
                onTrackPlay()
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CreateNotification.CHANNEL_ID,
                "KOD Dev", NotificationManager.IMPORTANCE_LOW
            )
            notificationManager =
                getSystemService(NotificationManager::class.java)
            if (notificationManager != null) {
                notificationManager!!.createNotificationChannel(channel)
            }
        }
    }

    //populate list with tracks
    private fun populateTracks() {
        tracks.add(Track("Track 1", "Artist 1", R.drawable.t1))
        tracks.add(Track("Track 2", "Artist 2", R.drawable.t2))
        tracks.add(Track("Track 3", "Artist 3", R.drawable.t3))
        tracks.add(Track("Track 4", "Artist 4", R.drawable.t4))
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            when (intent.extras!!.getString("actionName")) {
                CreateNotification.ACTION_PREVIOUS -> onTrackPrevious()
                CreateNotification.ACTION_PLAY -> if (isPlaying) {
                    onTrackPause()
                } else {
                    onTrackPlay()
                }
                CreateNotification.ACTION_NEXT -> onTrackNext()
            }
        }
    }

    override fun onTrackPrevious() {
        position--
        createNotification(
            this@MainActivity, tracks[position],
            R.drawable.ic_pause_black_24dp, position, tracks.size - 1
        )
        title.text = tracks[position].title
    }

    override fun onTrackPlay() {
        createNotification(
            this@MainActivity, tracks[position],
            R.drawable.ic_pause_black_24dp, position, tracks.size - 1
        )
        play.setImageResource(R.drawable.ic_pause_black_24dp)
        title.text = tracks[position].title
        isPlaying = true
    }

    override fun onTrackPause() {
        createNotification(
            this@MainActivity, tracks[position],
            R.drawable.ic_play_arrow_black_24dp, position, tracks.size - 1
        )
        play.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        title.text = tracks[position].title
        isPlaying = false
    }

    override fun onTrackNext() {
        position++
        createNotification(
            this@MainActivity, tracks[position],
            R.drawable.ic_pause_black_24dp, position, tracks.size - 1
        )
        title.text = tracks[position].title
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager!!.cancelAll()
        }
        unregisterReceiver(broadcastReceiver)
    }
}