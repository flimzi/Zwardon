package com.example.jetpacktest.services
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.asLiveData
import com.example.jetpacktest.FullScreenActivity
import com.example.jetpacktest.R
import com.example.jetpacktest.repositories.AuthRepository
import com.example.jetpacktest.repositories.AuthRepository.Companion.authStore
import com.example.jetpacktest.routes.Api
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred

class MessageService : FirebaseMessagingService() {
    companion object {
        const val CHANNEL_ID = "fcm"
    }

    override fun onCreate() {
        super.onCreate()

        applicationContext.authStore.data.mapNotNull { it[AuthRepository.tokenKey] }.asLiveData().observeForever { accessToken ->
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseMessaging.getInstance().token.asDeferred().await()?.let {
                    Api.Fcm.token(accessToken, it)
                }
            }
        }

        createNotificationChannel()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val a = 3
        super.onMessageReceived(message)

        val bundle = Bundle()
        for ((key, value) in message.data)
            bundle.putString(key,value)

        val activityIntent = Intent(this, FullScreenActivity::class.java).putExtras(bundle)
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Zwardon")
            .setContentText("application is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)

        getSystemService(NotificationManager::class.java).notify(1, notification.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("service", "token refresh")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return

        val serviceChannel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
    }
}

// this is creating a custom notification on a custom channel and binding an intent, also binding information from data to notification title, body
// the default notification handler and channel suffice for the general case where the activity is programatically ran from onmessagereceived
// however this may still come in handy later. another solution would be to do this https://stackoverflow.com/a/37560009 if still possible
// but i think the manual configuration of notification is still better because it allows for more configuration

//    private fun showNotification(messageBody: String?, actionDetails: String?) {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Create an intent for the notification action (this will probably not open the application on itself)
//        val intent = Intent(this, FullScreenActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            putExtra("ACTION_DETAILS", actionDetails)
//        }
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        // Build the notification
//        val notification = NotificationCompat.Builder(this, "your_channel_id")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Action Required")
//            .setContentText(messageBody)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setDefaults(NotificationCompat.DEFAULT_ALL) // This includes sound and vibration
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        // Show the notification
//        notificationManager.notify(1, notification)
//    }