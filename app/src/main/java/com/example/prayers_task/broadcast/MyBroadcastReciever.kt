package com.example.prayers_task.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.prayers_task.model.Constants

class MyBroadcastReciever :BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent!!.action.equals("com.tester.alarmmanager")){
            var b=intent.extras
            println("alram is clocking ${b?.getString("message")}")
            var uniqueCode=b?.getString(Constants.uniqueCode)

            val notifyMe= Notifications()
            notifyMe.makeNotification(context!!,uniqueCode?.toInt()?:0)
        }
        //in case the phone is reboted
        else if(intent!!.action.equals("android.intent.action.BOOT_COMPLETED")){
            //set time
            val saveTime=SaveTime(context!!)
            saveTime.getAllDataFromRoom()
        }






    }
}