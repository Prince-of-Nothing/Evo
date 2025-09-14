package com.evo.security.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.evo.security.MainActivity
import com.evo.security.R
import com.evo.security.model.SecurityNews
import com.evo.security.model.SecuritySeverity

class NotificationService(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "security_alerts"
        private const val CHANNEL_NAME = "Security Alerts"
        private const val CHANNEL_DESCRIPTION = "Notifications for security threats and alerts"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showSecurityNewsNotification(news: SecurityNews) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getSeverityPrefix(news.severity) + news.title)
            .setContentText(news.description)
            .setPriority(getNotificationPriority(news.severity))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(news.description)
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(news.id.hashCode(), notification)
        } catch (e: SecurityException) {
            // Handle notification permission not granted
        }
    }

    private fun getSeverityPrefix(severity: SecuritySeverity): String {
        return when (severity) {
            SecuritySeverity.LOW -> "ℹ️ "
            SecuritySeverity.MEDIUM -> "⚠️ "
            SecuritySeverity.HIGH -> "🚨 "
            SecuritySeverity.CRITICAL -> "🔥 CRITICAL: "
        }
    }

    private fun getNotificationPriority(severity: SecuritySeverity): Int {
        return when (severity) {
            SecuritySeverity.LOW -> NotificationCompat.PRIORITY_LOW
            SecuritySeverity.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            SecuritySeverity.HIGH -> NotificationCompat.PRIORITY_HIGH
            SecuritySeverity.CRITICAL -> NotificationCompat.PRIORITY_MAX
        }
    }
}