package com.bosolar.musicdemo

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bosolar.musicdemo.services.NotificationActionService

object CreateNotification {
    const val CHANNEL_ID = "channel1"
    const val ACTION_PREVIOUS = "actionPrevious"
    const val ACTION_PLAY = "actionPlay"
    const val ACTION_NEXT = "actionNext"
    private var notification: Notification? = null
    @JvmStatic
    fun createNotification(
        context: Context,
        track: Track,
        playButton: Int,
        pos: Int,
        size: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManagerCompat =
                NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSessionCompat(context, "tag")
            val icon =
                BitmapFactory.decodeResource(context.resources, track.image)
            val pendingIntentPrevious: PendingIntent?
            val drwPrevious: Int
            if (pos == 0) {
                pendingIntentPrevious = null
                drwPrevious = 0
            } else {
                val intentPrevious = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_PREVIOUS)
                pendingIntentPrevious = PendingIntent.getBroadcast(
                    context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
                )
                drwPrevious = R.drawable.ic_skip_previous_black_24dp
            }
            val intentPlay = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val pendingIntentNext: PendingIntent?
            val drwNext: Int
            if (pos == size) {
                pendingIntentNext = null
                drwNext = 0
            } else {
                val intentNext = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_NEXT)
                pendingIntentNext = PendingIntent.getBroadcast(
                    context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT
                )
                drwNext = R.drawable.ic_skip_next_black_24dp
            }

            //create notification
            notification =
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setContentTitle(track.title)
                    .setContentText(track.artist)
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true) //show notification for only first time
                    .setShowWhen(false)
                    .addAction(drwPrevious, "Previous", pendingIntentPrevious)
                    .addAction(playButton, "Play", pendingIntentPlay)
                    .addAction(drwNext, "Next", pendingIntentNext)
                    .setStyle(
                        androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.sessionToken)
                    )
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()
            notificationManagerCompat.notify(1, notification!!)
        }
    }
}