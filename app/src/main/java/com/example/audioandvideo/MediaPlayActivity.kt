package com.example.audioandvideo

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_media_play.*
import java.io.File
import java.sql.Time
import java.util.*
import kotlin.concurrent.timerTask

class MediaPlayActivity : AppCompatActivity() {
    private var filePath="${Environment.getExternalStorageDirectory().absolutePath.trim()}/media_recorder.mp4"
    private var mMediaPlay:MediaPlayer? = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_play)
        initPaly()
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mMediaPlay?.seekTo(seekBar?.progress!!)
            }

        })
    }

    private fun initPaly() {
        mMediaPlay?.reset()
    }

    /**
     * 播放
     */
    fun start(view: View) {
        val file =
            File(filePath)
        if (file.exists()&&file.length()>0){
            mMediaPlay?.setDataSource(filePath)//设置播放源地址
            mMediaPlay?.setAudioStreamType(AudioManager.STREAM_MUSIC)//设置音频流类型
//            mMediaPlay?.prepareAsync()//异步方式加载音频文件
//            mMediaPlay?.setOnPreparedListener(object :MediaPlayer.OnPreparedListener{
//                override fun onPrepared(mp: MediaPlayer?) {//加载完毕，开始播放
//                    mMediaPlay?.start()
//                }
//
//            })
//            mMediaPlay?.setOnCompletionListener {//播放完成
//
//            }
//            mMediaPlay?.setOnErrorListener { mp, what, extra -> //出错
//
//                false
//            }
            mMediaPlay?.prepare()
            mMediaPlay?.start()


            val timerTask = timerTask {
                val duration = mMediaPlay?.duration//总长度
                val currentPosition = mMediaPlay?.currentPosition
                seekBar.max=duration!!
                seekBar.progress=currentPosition!!
            }

            Timer().schedule(timerTask,0,1000)
        }
    }



    /**
     * 释放MediaPlay
     */
    private fun releaseMediaPlay(){
        if (mMediaPlay!=null&&mMediaPlay!!.isPlaying)
        mMediaPlay?.stop()
        mMediaPlay?.release()
        mMediaPlay=null
    }

    /**
     * 关闭
     */
    fun stop(view: View) {
        releaseMediaPlay()
    }

    /**
     * 暂停
     */
    fun pause(view: View) {
        mMediaPlay?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlay()
    }
}