package example.android.musicplayerapp

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

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

    override fun onCreate() {
        super.onCreate()

        //ADD THE SONGS TO THE LIST AND START THE MEDIA PLAYER WITH THE FIRST SONG)
        songs.add(createSong(R.raw.song1, ("android.resource://"+getPackageName()+"/"+R.raw.song1)))
        songs.add(createSong(R.raw.song2, ("android.resource://"+getPackageName()+"/"+R.raw.song2)))
        songPlayer = MediaPlayer.create(this@MusicPlayerService, songs[0].id)
        songPlayer.setOnCompletionListener { nextSong() }
    }


    private fun changeSong(songPlaying: Int){
        songPlayer.stop()
        songPlayer.reset()
        songPlayer = MediaPlayer.create(this@MusicPlayerService, songs[songPlaying].id)
        songPlayer.start()
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

    fun getImage(): Bitmap{
        metadataRetrieve.setDataSource(this@MusicPlayerService, Uri.parse(songs[songPlaying].path))
        val imageOnBits = metadataRetrieve.embeddedPicture
        return BitmapFactory.decodeByteArray(imageOnBits, 0, imageOnBits!!.size)
    }

}