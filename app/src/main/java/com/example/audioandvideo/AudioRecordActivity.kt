package com.example.audioandvideo

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.RuntimeException


class AudioRecordActivity : AppCompatActivity(),Runnable {
    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private val mAudioSource = MediaRecorder.AudioSource.MIC
    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private val mSampleRateInHz = 44100
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private val mChannelConfig: Int = AudioFormat.CHANNEL_CONFIGURATION_MONO //单声道
    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private val mAudioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private val mBufferSizeInBytes = 0
    //保存录音文件目录路径
    private val mDirPath=Environment.getExternalStorageDirectory().absolutePath+"/audioRecord"
    //文件名称
    private val mFilePath="audiorecord.pcm"
    //缓冲区大小
    private var minBufferSize:Int = 0

    //AudioRecorder实例
    private lateinit var mAudioRecord:AudioRecord
    //目录文件
    private lateinit var mFileRoot:File
    //录音状态
    private var isRecording=false
    private var mThread:Thread?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)
        initData()
    }

    private fun initData(){
        minBufferSize =
            AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat)
        mAudioRecord=AudioRecord(mAudioSource,mSampleRateInHz,mChannelConfig,mAudioFormat,minBufferSize)
        mFileRoot=File(mDirPath)
        if (!mFileRoot.exists()){
            mFileRoot.mkdirs()
        }
    }

    fun openRecorder(view: View) {
        //判断当前设备是否支持这个缓冲区的大小
        if (AudioRecord.ERROR_BAD_VALUE==minBufferSize||AudioRecord.ERROR==minBufferSize){
            throw RuntimeException("BufferSize Exception")
        }else{
            destroyThread()
            isRecording=true
            if (mThread==null){
                mThread=Thread(this)
                mThread?.start()
            }
        }
    }
    fun closerRecorder(view: View) {
        isRecording=false
        if (mAudioRecord!=null){
            if (mAudioRecord.state==AudioRecord.STATE_INITIALIZED){
                mAudioRecord.stop()
            }
            if (mAudioRecord!=null){
                mAudioRecord.release()
            }
        }
    }

    /**
     * 销毁线程
     */
    private fun destroyThread(){
        isRecording=false
        if (mThread!=null&&Thread.State.RUNNABLE==mThread?.state){
            Thread.sleep(500)
            mThread?.interrupt()
        }
        mThread=null
    }

    override fun run() {
        isRecording=true

        val file = File(mFileRoot, mFilePath)
        if (file.exists()){
            file.delete()
        }
        try {
            file.createNewFile()
        }catch (e:NoSuchFileException){
            e.printStackTrace()
        }

        try {
            val dataOutputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(file)))
            val byte = ByteArray(minBufferSize)
            if (mAudioRecord.state==AudioRecord.STATE_UNINITIALIZED){//若未创建实例
                initData()
            }
            mAudioRecord.startRecording()//开始录音
            while (isRecording&&mAudioRecord.state==AudioRecord.RECORDSTATE_RECORDING){//正处于录音状态
                val readBuff = mAudioRecord.read(byte, 0, mBufferSizeInBytes)
                for (i in 0 until readBuff){
                    dataOutputStream.write(byte[i].toInt())
                }
            }
            dataOutputStream.close()
        }catch (e:Exception){
            e.printStackTrace()
            destroyThread()
        }
    }
}