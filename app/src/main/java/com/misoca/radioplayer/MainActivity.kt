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
            binder?.playStart("<URL>")
        }
        btnStop.setOnClickListener {
            binder?.stop()
        }
        bindService(Intent(this, RadioService::class.java), conn, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(conn)
        super.onDestroy()
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
