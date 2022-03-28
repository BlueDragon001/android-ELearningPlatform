package com.hcorp.tuition.videoplayer

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.hcorp.tuition.R
import com.hcorp.tuition.videoplayer.VideoCache.Singleton


class VideoPlayer : AppCompatActivity() {
    private lateinit var uri: Uri
    lateinit var player: ExoPlayer
    private lateinit var playButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    lateinit var leftView: View
    lateinit var rightView: View
    private lateinit var progressBar: DefaultTimeBar
    private lateinit var mButton: LinearLayout
    private lateinit var playerLayout: ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_video_player)
        mediaPlayer()
        buttonInitiator()
        playerUI()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, View(this)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun buttonInitiator() {
        playButton = findViewById(R.id.exo_play)
        nextButton = findViewById(R.id.exo_next)
        prevButton = findViewById(R.id.exo_prev)
        leftView = findViewById(R.id.viewLeft)
        rightView = findViewById(R.id.viewRight)
        progressBar = findViewById(R.id.exo_progress)
        mButton = findViewById(R.id.bottomControl)
        playerLayout = findViewById(R.id.playerControllerLayout)
    }

    private fun playerUI() {
        playButton.setBackgroundResource(R.drawable.play_button)
        playButton.setOnClickListener {
            if (!player.isPlaying) {
                player.play()
                playButton.setBackgroundResource(R.drawable.pause_button)
            } else {
                player.pause()
                playButton.setBackgroundResource(R.drawable.play_button)

            }

        }
        mButton.visibility = progressBar.visibility
        if (playerLayout.visibility != View.VISIBLE) {
            playerLayout.visibility = View.VISIBLE
        }
        leftView.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                player.seekBack()
                leftView.setBackgroundResource(R.drawable.black_rewind)
                Handler(Looper.getMainLooper()).postDelayed({
                    leftView.setBackgroundResource(0)
                }, 300)
            }
        })
        leftView.background = null
        rightView.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                player.seekForward()
                rightView.setBackgroundResource(R.drawable.black_forward)
                Handler(Looper.getMainLooper()).postDelayed({
                    rightView.setBackgroundResource(0)
                }, 300)
            }
        })
        rightView.background = null
    }

    private fun mediaPlayer() {
        player = ExoPlayer.Builder(this).build()
        uri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        val upstreamFactory = DefaultDataSource.Factory(this)
        val cacheFactory = CacheDataSource.Factory().apply {
            setCache(Singleton.getInstance(this@VideoPlayer))
            setUpstreamDataSourceFactory(upstreamFactory)
            setFlags(
                CacheDataSource.FLAG_BLOCK_ON_CACHE or
                        CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
            )


        }
        val playerView: StyledPlayerView = findViewById(R.id.player_view)
        playerView.player = player
        val mediaSourceFactory = ProgressiveMediaSource.Factory(cacheFactory)
        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)
        player.setMediaSource(mediaSource, true)
        player.prepare()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        player.stop()
        player.release()
        cacheDir.delete()
    }


    abstract class DoubleClickListener : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(v: View?)

        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }


}