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

    lateinit var albumImage: ImageView
    lateinit var songText: TextView



    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicPlayerService.MyBinder
            musicService = binder.getService()
            serviceBounded = true
            updateUI()
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

        prevButton.setOnClickListener{
            musicService.previousSong()
            updateUI()
        }
        playButton.setOnClickListener {
            musicService.playSong()
            updateUI()
        }
        nextButton.setOnClickListener{
            musicService.nextSong()
            updateUI()
        }


        albumImage.setOnClickListener {
            val intentToDetails = Intent(this,DetailsActivity::class.java)
            intentToDetails.putExtra("song", musicService.getSongPlaying())
            startActivity(intentToDetails)
        }
    }

    fun updateUI(){
        val title = musicService.getSongPlaying().artistText+" - "+musicService.getSongPlaying().songText
        songText.text = title
        val image = musicService.getImage()
        if(image == null){
            albumImage.setImageResource(R.drawable.no_album)
        }else{
            albumImage.setImageBitmap(image)
        }
    }
}