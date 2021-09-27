package example.android.musicplayerapp

import android.content.*

class NotificationReceiver: BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        val playIntent = Intent(context, MusicPlayerService::class.java)
        playIntent.action = "ACTION_PLAY"
        val nextIntent = Intent(context, MusicPlayerService::class.java)
        nextIntent.action = "ACTION_NEXT"
        val previousIntent = Intent(context, MusicPlayerService::class.java)
        previousIntent.action = "ACTION_PREV"

        when(intent?.action){
            "ACTION_PLAY" -> context?.startService(playIntent)
            "ACTION_NEXT" -> context?.startService(nextIntent)
            "ACTION_PREV" -> context?.startService(previousIntent)
        }
    }
}