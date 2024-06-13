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

//    fun saveData(hour:Int,minutes:Int){
//        var editor=PrayersSP!!.edit()
//        editor.putInt("Hour",hour)
//        editor.putInt("Minute",minutes)
//        editor.commit()
//    }
//
//    fun getHour():Int{
//        return  PrayersSP!!.getInt("Hour",0)
//    }
//    fun getMin():Int{
//        return  PrayersSP!!.getInt("Minute",0)
//    }

    fun setAlarm(month:Int,dayOfMonth:Int,hour:Int,minutes: Int,uniqueReqCode:Int){
//        var hour=getHour()
//        var minutes=getMin()

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
    fun getMonth(prayer:String):String{
        val arr=prayer.split("-")
        return arr[1]
    }
    //to get the hours and minutes from this format "04:11"
//    fun getDetailedPrayerTime(prayerName:String){
//      val n= PrayersSP?.getString(prayerName,"")
//
//
//        val fullTime=getTheTimingOnlyFromTxt(n!!)
//        val m=fullTime?.split(":")
//        hourPart=m?.get(0)!!.toInt()
//        minutesPart=m?.get(1)!!.toInt()
//        println("FullTime->${fullTime}: hourPart->${hourPart}: minutesPart:${minutesPart}")
//        Toast.makeText(context, "time is set  FullTime->${fullTime}: hourPart->${hourPart}: minutesPart:${minutesPart}", Toast.LENGTH_SHORT).show()
//    }
    fun getDetailedPrayerTime(prayerName:String){

        val m=prayerName?.split(":")
        hourPart=m?.get(0)!!.toInt()
        minutesPart=m?.get(1)!!.toInt()
        println("FullTime->${fullTime}: hourPart->${hourPart}: minutesPart:${minutesPart}")
        Toast.makeText(context, "time is set  FullTime->${fullTime}: hourPart->${hourPart}: minutesPart:${minutesPart}", Toast.LENGTH_SHORT).show()
    }
    val prayersNameList= arrayListOf<String>(
        Constants.fajrPrayer,
        Constants.dhuhrPrayer,
        Constants.asrPrayer,
        Constants.maghribPrayer,
        Constants.ishaPrayer
    )
    fun getAllDataFromRoom(){
        val prayT=PrayersSP?.getString("CurrentDate","")
        val tempHolder=prayT?.split("-")
        val prayTDay=tempHolder?.get(0)?.toInt()

        val monthPrayersData= AppLocalPrayersDB.getDB(context!!).prayersDAO().getPrayersFromCurrentDateTillMonthEnd(prayTDay!!)
        monthPrayersData.forEach {
            val prayMonth=getMonth(it.date!!)

            var prayer0=getTheTimingOnlyFromTxt(it.fajr!!)
            var prayer1=getTheTimingOnlyFromTxt(it.dhuhr!!)
            var prayer2=getTheTimingOnlyFromTxt(it.asr!!)
            var prayer3=getTheTimingOnlyFromTxt(it.maghrib!!)
            var prayer4=getTheTimingOnlyFromTxt(it.isha!!)
            val prayersList= arrayListOf(
                prayer0,prayer1,prayer2,prayer3,prayer4
            )
            for (i in 0..4){
                getDetailedPrayerTime(prayersList[i])
                var uniqueReqCode="${prayMonth}${i}${it.day}".toInt()
                setAlarm(hour = hourPart, minutes = minutesPart, month = prayMonth.toInt(), uniqueReqCode =uniqueReqCode ,
                    dayOfMonth = it.day!!
                )
            }

        }
    }

//    fun setAlarmForAllPrayers(){
//        for (i in 0..4){
//            getDetailedPrayerTime(prayersNameList[i])
//            setAlarm(hour = hourPart, minutes = minutesPart,)
//        }
//    }
}