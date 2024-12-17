package com.example.testchatfragment

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.testchatfragment.models.SongModel

object MyExoplayer {
    private var exoPlayer: ExoPlayer? = null
    private var currentSong: SongModel? = null

    @JvmStatic
    fun getCurrentSong(): SongModel? {
        return currentSong
    }


        fun getInstance(): ExoPlayer? {
        return exoPlayer
    }

    // Tambahkan parameter context
    fun startPlay(context: Context, song: SongModel) {
        // Bangun ExoPlayer hanya jika null
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }
        if (currentSong != song){ //to make no restart
            //it's new
            currentSong = song
            currentSong?.url?.let { url -> //if not null play
                val mediaItem = MediaItem.fromUri(url) //asignmediaitem
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare() //start prepering
                exoPlayer?.play()
            }
        }

    }
}
