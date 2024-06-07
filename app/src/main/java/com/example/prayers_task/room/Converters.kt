package com.example.prayers_task.room

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.prayers_task.model.Date
import com.example.prayers_task.model.Timings
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun convertFromDateToString(date: Date):String{
        return date.gregorian?.date!!
    }

    @TypeConverter
    fun convertFromTimingToString(timings: Timings):ArrayList<String>{
        val timingArray= arrayListOf<String>()
        timingArray.add(timings?.fajr!!)
        timingArray.add(timings?.sunrise!!)
        timingArray.add(timings?.dhuhr!!)
        timingArray.add(timings?.asr!!)
        timingArray.add(timings?.maghrib!!)
        timingArray.add(timings?.isha!!)
        return timingArray
    }
    @TypeConverter
    fun convertToTiming(timingList:ArrayList<String>):Timings{
        return Timings(fajr = timingList.get(0),
        sunrise = timingList.get(1),
        dhuhr = timingList.get(2),
        asr = timingList.get(3),
        maghrib = timingList.get(4),
        isha = timingList.get(5))
    }




}