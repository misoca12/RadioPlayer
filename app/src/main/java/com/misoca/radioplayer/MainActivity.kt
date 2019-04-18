package com.misoca.radioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.session.PlaybackState
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RadioService.PlayerStateChangedListener {

    private var binder : RadioService.RadioBinder? = null

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            binder = iBinder as RadioService.RadioBinder
            binder?.setListener(this@MainActivity)
            setPlayStatus(binder?.isPlaying())
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            binder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlay.setOnClickListener {
            val intent = Intent(this, RadioService::class.java).apply {
                action = RadioService.ACTION_PLAY_RADIO
                putExtra(RadioService.EXTRA_RADIO_URL, "https://nhkradioikr1-i.akamaihd.net/hls/live/512098/1-r1/1-r1-01.m3u8")
            }
            startService(intent)
        }
        btnStop.setOnClickListener {
            val intent = Intent(this, RadioService::class.java).apply {
                action = RadioService.ACTION_STOP_RADIO
            }
            startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, RadioService::class.java), conn, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        unbindService(conn)
        super.onPause()
    }

    private fun setPlayStatus(play: Boolean?) {
        play ?: return
        btnPlay.isEnabled = !play
        btnStop.isEnabled = play
    }

    override fun onPlayerStateChanged(playbackState: Int) {
        setPlayStatus(playbackState == PlaybackState.STATE_PLAYING)
    }
}
