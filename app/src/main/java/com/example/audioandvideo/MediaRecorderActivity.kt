package com.example.audioandvideo

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.app.ActivityCompat
import java.io.File
import java.lang.Exception

class MediaRecorderActivity : AppCompatActivity() {
    private val rootPath = Environment.getExternalStorageDirectory()
    private val mediaRecorder: MediaRecorder = MediaRecorder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_recorder)

    }

    fun openRecorder(view: View) {
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC) //麦克风采集音频 音频源
            mediaRecorder.setAudioSamplingRate(44100)//设置采样率
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)//输出文件的格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)//录制的音频编码器
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
            val file = File("$rootPath/media_recorder.mp4")
            if (!file.exists()) {
                file.createNewFile()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(file)
            }else{
                mediaRecorder.setOutputFile("$rootPath/media_recorder.mp4")
            }
            mediaRecorder.prepare()
            mediaRecorder.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun closerRecorder(view: View) {
        mediaRecorder.stop()
        mediaRecorder.release()
    }
}