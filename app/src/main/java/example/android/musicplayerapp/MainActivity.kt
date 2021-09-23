package example.android.musicplayerapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.*

class MainActivity : AppCompatActivity() {

    lateinit var musicService: MusicPlayerService
    private var serviceBounded: Boolean = false

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

        val songName: TextView = findViewById<TextView>(R.id.albumTextMain)
        val albumArt: ImageView = findViewById<ImageView>(R.id.albumImage)


        //We bind to the service
        var myIntent = Intent(this@MainActivity, MusicPlayerService::class.java)
        startService(myIntent)

        myIntent.also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()

        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        val prevButton = findViewById<ImageButton>(R.id.prevButton)
        val songName: TextView = findViewById<TextView>(R.id.albumTextMain)
        val albumArt: ImageView = findViewById<ImageView>(R.id.albumImage)
        val playButton: ImageButton = findViewById<ImageButton>(R.id.playButton)
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)


        playButton.setOnClickListener {
            songName.text = musicService.getData().first
            albumArt.setImageBitmap(musicService.getData().second)
            musicService.playSong()
        }

        nextButton.setOnClickListener{
            musicService.nextSong()
            songName.text = musicService.getData().first
            albumArt.setImageBitmap(musicService.getData().second)

        }
        prevButton.setOnClickListener{
            musicService.previousSong()
            songName.text = musicService.getData().first
            albumArt.setImageBitmap(musicService.getData().second)
        }

        if(serviceBounded){
            songName.text = musicService.getData().first
            albumArt.setImageBitmap(musicService.getData().second)
        }
    }
}