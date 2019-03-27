package com.misoca.radioplayer

import android.app.Service
import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory


class RadioService : Service(), Player.EventListener {

    private val player by lazy { ExoPlayerFactory.newSimpleInstance(applicationContext) }
    private val binder = RadioBinder()
    private var playerStateChangedListener : PlayerStateChangedListener? = null

    companion object {
        const val UA = "ua"
    }

    interface PlayerStateChangedListener {
        fun onPlayerStateChanged(playbackState: Int)
    }

    inner class RadioBinder : Binder() {
        fun playStart(url: String) {
            val uri = Uri.parse(url)
            val dataSourceFactory = DefaultDataSourceFactory(this@RadioService, UA)
            val audioSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            player.prepare(audioSource)
        }
        fun stop() { player.stop() }
        fun isPlaying() = player.playbackState == PlaybackState.STATE_PLAYING
        fun setListener(listener: PlayerStateChangedListener) {
            playerStateChangedListener = listener
        }
    }

    override fun onCreate() {
        super.onCreate()
        player.playWhenReady = true
        player.addListener(this)
    }

    override fun onDestroy() {
        player.stop()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        playerStateChangedListener?.onPlayerStateChanged(playbackState)
    }
}
