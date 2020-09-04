package com.example.audioandvideo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 *@author:lenovo
 *@date:2020/9/3
 **/
class Utils private constructor(){
    companion object{
        val instances:Utils by lazy(mode=LazyThreadSafetyMode.SYNCHRONIZED) {
            Utils()
        }

        const val REQUEST_PERMISSION_CODE=332
    }

    fun requestPermiss(context: Activity,permissions:Array<String>, disccription:String,f:()->Unit){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            var permissgroup=0
            for (element in permissions){
                val checkSelfPermission = ContextCompat.checkSelfPermission(context, element)
                if (checkSelfPermission==PackageManager.PERMISSION_GRANTED){//权限已经有
                    permissgroup++
                }else if(checkSelfPermission==PackageManager.PERMISSION_DENIED){//权限未赋予
                    //若无权限，引导用户赋权
                    showDialogTipUserRequestPermiss(context,disccription,permissions)
                }
            }
            if (permissgroup==permissions.size){
                f()
            }
        }
    }


    private fun showDialogTipUserRequestPermiss(context: Activity,disccription:String,permissions:Array<String>) {
        AlertDialog.Builder(context)
            .setTitle("允许${disccription}权限")
            .setMessage("录音需要${disccription}权限，否则无法使用该功能！！！")
            .setPositiveButton("允许") { _, _ ->
                startRequestPermission(context,permissions)
            }
            .setNegativeButton("拒绝"){
                _,_->
                Toast.makeText(context,"拒绝将无法使用该功能！！！",Toast.LENGTH_LONG).show()
            }
            .show()

    }

    private fun startRequestPermission(activity:Activity,permissions:Array<String>) {
        ActivityCompat.requestPermissions(activity,permissions,REQUEST_PERMISSION_CODE)
    }

    fun showDialogTipUserGoToSetting(context: Activity,disccription:String,permissions:Array<String>){
        AlertDialog.Builder(context)
            .setTitle("权限不可用")
            .setMessage("请在-应用设置-权限-中允许该权限")
            .setPositiveButton("去开启"){
                _,_->
                goToSetting(context)
            }
            .setNegativeButton("不去"){
                _,_->
                Toast.makeText(context,"任性拒绝将无法使用该功能哦！",Toast.LENGTH_LONG).show()
            }
    }

    private fun goToSetting(context: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package",context.packageName,null))
        context.startActivity(intent)
    }
}