package com.example.prayers_task.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prayers_task.model.*
import com.example.prayers_task.room.AppLocalPrayersDB
import java.util.Calendar

class SaveTime {
    var context:Context?=null
    var PrayersSP:SharedPreferences?=null

    var fullTime:Int=0
    var hourPart:Int=0
    var minutesPart:Int=0

    constructor(context: Context){
        this.context=context
        PrayersSP= context!!.getSharedPreferences(Constants.sharedPrefName, AppCompatActivity.MODE_PRIVATE)

    }

    fun setAlarm(month:Int,dayOfMonth:Int,hour:Int,minutes: Int,uniqueReqCode:Int){
        //the next two lines says i want you to set the hour of the day with the hour and minutes
        // i will send it to you
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.MONTH,month)
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND,0)

        val alarmManager=context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent=Intent(context,MyBroadcastReciever::class.java)
        intent.putExtra("message","Prayer Time Alarm")
        intent.putExtra(Constants.uniqueCode,uniqueReqCode)
        intent.action="com.tester.alarmmanager"
        //PendingIntent.FLAG_UPDATE_CURRENT
        val pendingInt=PendingIntent.getBroadcast(context,uniqueReqCode,intent,
            PendingIntent.FLAG_MUTABLE)
        //telling the system i want you to due this action at this time
        //what action
        //setExactandAllowWhileIdle to set alarm in the exact time given and also to work even if the battery is low
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
        pendingInt)
    }

    //the time is returned in the next format "04:11 (EEST)" and i only need the time part
    fun getTheTimingOnlyFromTxt(prayer:String):String{
        val arr=prayer.split(" ")
        return arr[0]
    }

    //the date is returned in the next format "dd-MM-yyyy" so to get the month part

    fun getMonth(prayer:String):String{
        val arr=prayer.split("-")
        return arr[1]
    }

    // to get the hours and minutes from the next format "04:11"
    fun getDetailedPrayerTime(prayerName:String){
        val m=prayerName?.split(":")
        hourPart=m?.get(0)!!.toInt()
        minutesPart=m?.get(1)!!.toInt()
        println("FullTime->${fullTime}: hourPart->${hourPart}: minutesPart:${minutesPart}")
//        Toast.makeText(context, "time is set  FullTime->${fullTime}: hourPart->${hourPart}: minutesPart:${minutesPart}", Toast.LENGTH_SHORT).show()
    }

    fun getAllDataFromRoom(){
        //get the date we checked on the setPrayersAlarm
        val prayT=PrayersSP?.getString("dateSetAlarmIsChecked","")
        val tempHolder=prayT?.split("-")
        val prayTDay=tempHolder?.get(0)?.toInt()

        //then filter the data saved in room from the day we checked till the end of the month
        val monthPrayersData= AppLocalPrayersDB.getDB(context!!).prayersDAO().getPrayersFromCurrentDateTillMonthEnd(prayTDay!!)
        monthPrayersData.forEach {
            val prayMonth=getMonth(it.date!!)

            //ex: 04:11
            var prayer0=getTheTimingOnlyFromTxt(it.fajr!!)
            var prayer1=getTheTimingOnlyFromTxt(it.dhuhr!!)
            var prayer2=getTheTimingOnlyFromTxt(it.asr!!)
            var prayer3=getTheTimingOnlyFromTxt(it.maghrib!!)
            var prayer4=getTheTimingOnlyFromTxt(it.isha!!)
            val prayersList= arrayListOf(
                prayer0,prayer1,prayer2,prayer3,prayer4
            )
            for (i in 0..4){
                //ex: hourPart=04, minutesPart11
                getDetailedPrayerTime(prayersList[i])
                //ex for fajr uniqueCode= 6022 for duhr=6122
                var uniqueReqCode="${prayMonth}${i}${it.day}".toInt()
                setAlarm(hour = hourPart, minutes = minutesPart, month = prayMonth.toInt(), uniqueReqCode =uniqueReqCode ,
                    dayOfMonth = it.day!!
                )
            }

        }
    }

}