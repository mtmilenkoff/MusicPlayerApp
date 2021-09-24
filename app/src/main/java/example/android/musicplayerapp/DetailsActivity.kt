package example.android.musicplayerapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

    }

    override fun onResume() {
        super.onResume()

        val song = intent.getSerializableExtra("song") as Song

        val albumText = findViewById<TextView>(R.id.albumTextDetails)
        val artistText = findViewById<TextView>(R.id.artistTextDetails)
        val durationText = findViewById<TextView>(R.id.durationTextDetails)
        val songText = findViewById<TextView>(R.id.songTextDetails)
        val yearText = findViewById<TextView>(R.id.yearTextDetails)

        albumText.text = song.albumText
        songText.text = song.songText
        artistText.text = song.artistText
        durationText.text = song.durationText
        yearText.text = song.yearText

    }
}