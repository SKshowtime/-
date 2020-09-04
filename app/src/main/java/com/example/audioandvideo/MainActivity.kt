package com.example.audioandvideo

import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Duration
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private var nameValue = arrayOf("MediaRecorder使用", "AudioRecord使用","MediaPlay播放器")
    private var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val myAdapter = MyAdapter()
        main_rv.adapter = myAdapter
        myAdapter.setOnItemClick(object : OnItemClickListener {
            override fun OnItemClick(view: View?, position: Int) {
//                Toast.makeText(this@MainActivity,nameValue[position],Toast.LENGTH_LONG).show()
                this@MainActivity.position = position
                Utils.instances.requestPermiss(
                    this@MainActivity
                    , arrayOf(
                        android.Manifest.permission.RECORD_AUDIO
                        , android.Manifest.permission.READ_EXTERNAL_STORAGE
                        , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    , ""
                ) {
                    GoToActivity(position)
                }
            }

        })
    }

    fun GoToActivity(position: Int) {
        if (position == 0) {
            startActivity(Intent(this, MediaRecorderActivity::class.java))
        } else if(position==1){
            startActivity(Intent(this, AudioRecordActivity::class.java))
        }else if(position==2){
            startActivity(Intent(this,MediaPlayActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//判断用户是否点击了不再提醒
                    val shouldShowRequestPermissionRationale =
                        shouldShowRequestPermissionRationale(permissions[0])
                    if (!shouldShowRequestPermissionRationale) {//未点击不在提醒
                        Utils.instances.showDialogTipUserGoToSetting(this, "", permissions)
                    } else {
                        Toast.makeText(this, "请允许权限", Toast.LENGTH_LONG).show()
                    }
                } else {
                    GoToActivity(position)
                }
            }
        }
    }


    interface OnItemClickListener {
        fun OnItemClick(view: View?, position: Int)
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        private var listener: OnItemClickListener? = null

        fun setOnItemClick(listener: OnItemClickListener) {
            this.listener = listener
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textView = itemView as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            var textView = TextView(this@MainActivity)
            val layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300)
//            layoutParams.gravity=Gravity.CENTER
            textView.layoutParams = layoutParams
            textView.gravity = Gravity.CENTER
            return MyViewHolder(textView)
        }

        override fun getItemCount(): Int {
            return nameValue.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = nameValue[position]
            holder.textView.setOnClickListener { p0 -> listener?.OnItemClick(p0, position) }
        }
    }
}