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


        //Bind to the service
        val myIntent = Intent(this@MainActivity, MusicPlayerService::class.java)
        startService(myIntent)

        myIntent.also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()

        val nextButton = findViewById<ImageButton>(R.id.nextButton)
        val prevButton = findViewById<ImageButton>(R.id.prevButton)
        val songText: TextView = findViewById<TextView>(R.id.albumTextMain)
        val albumImage: ImageView = findViewById<ImageView>(R.id.albumImage)
        val playButton: ImageButton = findViewById<ImageButton>(R.id.playButton)


        playButton.setOnClickListener {
            musicService.playSong()
            val title = musicService.getSongPlaying().artistText+" - "+musicService.getSongPlaying().songText
            songText.text = title
            albumImage.setImageBitmap(musicService.getImage())
        }

        nextButton.setOnClickListener{
            musicService.nextSong()
            val title = musicService.getSongPlaying().artistText+" - "+musicService.getSongPlaying().songText
            songText.text = title
            albumImage.setImageBitmap(musicService.getImage())
        }
        prevButton.setOnClickListener{
            musicService.previousSong()
            val title = musicService.getSongPlaying().artistText+" - "+musicService.getSongPlaying().songText
            songText.text = title
            albumImage.setImageBitmap(musicService.getImage())
        }


        albumImage.setOnClickListener {
            val intentToDetails = Intent(this,DetailsActivity::class.java)
            intentToDetails.putExtra("song", musicService.getSongPlaying())
            startActivity(intentToDetails)
        }

    }
}