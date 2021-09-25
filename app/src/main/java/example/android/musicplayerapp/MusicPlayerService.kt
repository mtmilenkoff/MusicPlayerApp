package example.android.musicplayerapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MusicPlayerService : Service() {


    val metadataRetrieve = MediaMetadataRetriever()

    lateinit var songPlayer: MediaPlayer
    val songs = ArrayList<Song>()
    var songPlaying = 0
    var isPlaying = false


    private val musicBinder = MyBinder()


    inner class MyBinder : Binder() {
        fun getService() : MusicPlayerService{ return this@MusicPlayerService }
    }

    override fun onBind(p0: Intent?): IBinder {
        return musicBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()

        //ADD THE SONGS TO THE LIST AND START THE MEDIA PLAYER WITH THE FIRST SONG)
        songs.add(createSong(R.raw.song1, ("android.resource://"+getPackageName()+"/"+R.raw.song1)))
        songs.add(createSong(R.raw.song2, ("android.resource://"+getPackageName()+"/"+R.raw.song2)))
        songs.add(createSong(R.raw.song3, ("android.resource://"+getPackageName()+"/"+R.raw.song3)))
        songPlayer = MediaPlayer.create(this@MusicPlayerService, songs[0].id)

    }


    private fun changeSong(songPlaying: Int){
        songPlayer.stop()
        songPlayer.reset()
        songPlayer.setDataSource(this@MusicPlayerService, Uri.parse(songs[songPlaying].path))
        songPlayer.prepare()
        songPlayer.start()
        songPlayer.isLooping = true
        isPlaying=true
    }

    fun playSong(){
        isPlaying = if(isPlaying){
            songPlayer.pause()
            false
        }else{
            songPlayer.start()
            true
        }
    }

    fun nextSong(){
        if(songPlaying+1>=songs.size){
            songPlaying = 0
        }else{
            songPlaying++
        }
        changeSong(songPlaying)
    }

    fun previousSong(){
        if(songPlaying-1<0){
            songPlaying = songs.size-1
        }else{
            songPlaying--
        }
        changeSong(songPlaying)
    }

    fun getSongPlaying(): Song{
        return songs[songPlaying]
    }

    private fun createSong(id: Int,path: String): Song{
        metadataRetrieve.setDataSource(this@MusicPlayerService, Uri.parse(path))
        val albumName = metadataRetrieve.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Not found"
        val songName = metadataRetrieve.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Not found"
        val artistText = metadataRetrieve.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Not found"
        val durationText = metadataRetrieve.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "Not found"
        val yearText = metadataRetrieve.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: "Not found"

        return Song(id, path, albumName, songName, artistText, durationText, yearText)
    }

    fun getImage(): Bitmap?{
        metadataRetrieve.setDataSource(this@MusicPlayerService, Uri.parse(songs[songPlaying].path))
        val imageOnBits = metadataRetrieve.embeddedPicture
        if(imageOnBits != null){
            return BitmapFactory.decodeByteArray(imageOnBits, 0, imageOnBits.size)
        }
        return null
    }

}