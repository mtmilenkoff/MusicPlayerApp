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
import androidx.core.app.NotificationCompat

class MusicPlayerService : Service() {

    lateinit var songPlayer: MediaPlayer
    val metadataRetrieve = MediaMetadataRetriever()
    val songs = ArrayList<Song>()
    var songPlaying = 0
    var isPlaying = false
    private val musicBinder = MyBinder()


    inner class MyBinder : Binder() {
        fun getService() : MusicPlayerService{ return this@MusicPlayerService }
    }

    override fun onBind(p0: Intent?): IBinder { return musicBinder }

    override fun onUnbind(intent: Intent?): Boolean { return false }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){
            "ACTION_PLAY" -> playSong()
            "ACTION_NEXT" -> nextSong()
            "ACTION_PREV" -> previousSong()
        }
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()

        //ADD THE SONGS TO THE LIST AND START THE MEDIA PLAYER WITH THE FIRST SONG
        songs.add(createSong(R.raw.song1, ("android.resource://"+getPackageName()+"/"+R.raw.song1)))
        songs.add(createSong(R.raw.song2, ("android.resource://"+getPackageName()+"/"+R.raw.song2)))
        songs.add(createSong(R.raw.song3, ("android.resource://"+getPackageName()+"/"+R.raw.song3)))
        songPlayer = MediaPlayer.create(this@MusicPlayerService, songs[0].id)


        val notiManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openIntent = Intent(this, MainActivity::class.java)
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
            .addAction(R.drawable.previous, "", previousPendingIntent)
            .addAction(R.drawable.play, "", playPendingIntent)
            .addAction(R.drawable.next, "", nextPendingIntent)
            .setContentTitle("Music Player")
            .setContentText("Song")
            .build()
        notiManager.notify(0,notification)
    }



    private fun changeSong(songPlaying: Int){
        songPlayer.stop()
        songPlayer.reset()
        songPlayer.setDataSource(this@MusicPlayerService, Uri.parse(songs[songPlaying].path))
        songPlayer.prepare()
        songPlayer.start()
        songPlayer.setOnCompletionListener { nextSong() }
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