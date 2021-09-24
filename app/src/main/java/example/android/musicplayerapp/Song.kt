package example.android.musicplayerapp

import java.io.Serializable

data class Song (val id: Int,
           val path: String,
           val albumText: String,
           val songText: String,
           val artistText: String,
           val durationText: String,
           val yearText: String): Serializable
