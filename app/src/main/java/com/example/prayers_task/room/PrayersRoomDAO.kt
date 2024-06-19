package com.example.prayers_task.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PrayersRoomDAO {
    @Query("select * from prayersPerMonth")
    fun getAllMonthPrayers():List<PrayersDC>

    @Insert
    fun addPrayerDataItem(prayerDataItem: PrayersDC)

    @Query("DELETE FROM prayersPerMonth")
    fun deleteAll()

    @Query("SELECT * FROM prayersPerMonth WHERE prayer_day >= :currentDay")
    fun getPrayersFromCurrentDateTillMonthEnd(currentDay:Int):List<PrayersDC>

}


