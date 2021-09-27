package example.android.musicplayerapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.io.*
import android.content.BroadcastReceiver




class MainActivity : AppCompatActivity() {

    lateinit var musicService: MusicPlayerService
    private var serviceBounded: Boolean = false

    lateinit var albumImage: ImageView
    lateinit var songText: TextView



    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicPlayerService.MyBinder
            musicService = binder.getService()
            serviceBounded = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            serviceBounded = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val myIntent = Intent(this@MainActivity, MusicPlayerService::class.java)
        //Start service
        startService(myIntent)
        //Bound service
        myIntent.also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }


    override fun onResume() {
        super.onResume()

        val playButton: ImageButton = findViewById<ImageButton>(R.id.playButton)
        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        val prevButton = findViewById<ImageButton>(R.id.prevButton)
        songText = findViewById<TextView>(R.id.albumTextMain)
        albumImage= findViewById<ImageView>(R.id.albumImage)


        playButton.setOnClickListener {
            musicService.playSong()
            val title = musicService.getSongPlaying().artistText+" - "+musicService.getSongPlaying().songText
            songText.text = title
            val image = musicService.getImage()
            if(image == null){
                albumImage.setImageResource(R.drawable.no_album)
            }else{
                albumImage.setImageBitmap(image)
            }
        }

        nextButton.setOnClickListener{
            musicService.nextSong()
            val title = musicService.getSongPlaying().artistText+" - "+musicService.getSongPlaying().songText
            songText.text = title
            val image = musicService.getImage()
            if(image == null){
                albumImage.setImageResource(R.drawable.no_album)
            }else{
                albumImage.setImageBitmap(image)
            }
        }

        prevButton.setOnClickListener{
            musicService.previousSong()
            val title = musicService.getSongPlaying().artistText+" - "+musicService.getSongPlaying().songText
            songText.text = title
            val image = musicService.getImage()
            if(image == null){
                albumImage.setImageResource(R.drawable.no_album)
            }else{
                albumImage.setImageBitmap(image)
            }
        }

        albumImage.setOnClickListener {
            val intentToDetails = Intent(this,DetailsActivity::class.java)
            intentToDetails.putExtra("song", musicService.getSongPlaying())
            startActivity(intentToDetails)
        }
    }

    override fun onPause() {
        super.onPause()


        val notiManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

         val openIntent = Intent(this, this::class.java)
         val openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, 0)

        val playIntent = Intent(this, NotificationReceiver::class.java).also{ it.action = "ACTION_PLAY" }
        val playPendingIntent = PendingIntent.getBroadcast(this,0,playIntent,0)

        val nextIntent = Intent(this, NotificationReceiver::class.java).also{ it.action = "ACTION_NEXT" }
        val nextPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent,0)

        val previousIntent = Intent(this, NotificationReceiver::class.java).also{ it.action = "ACTION_PREV" }
        val previousPendingIntent = PendingIntent.getBroadcast(this,0,previousIntent,0)

        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "musicplayerid")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.download)
            .setContentIntent(openPendingIntent)
            .addAction(R.drawable.play, "", playPendingIntent)
            .addAction(R.drawable.previous, "", previousPendingIntent)
            .addAction(R.drawable.next, "", nextPendingIntent)
            .setContentTitle("Music Player")
            .setContentText("Song")
            .build()

        notiManager.notify(0,notification)

    }


    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "player"
            val descriptionText = "player description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("musicplayerid", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }




}