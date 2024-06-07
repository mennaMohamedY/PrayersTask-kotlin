package com.example.prayers_task.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.prayers_task.model.DataItem


@Dao
interface PrayersRoomDAO {
    @Query("select * from prayersPerMonth")
    fun getAllMonthPrayers():List<PrayersDC>

    @Insert
    fun addPrayerDataItem(prayerDataItem: PrayersDC)

    @Query("SELECT * FROM prayersPerMonth WHERE date IN (:date)")
    fun loadCurrentDayPrayers(date: String): PrayersDC

    @Query("DELETE FROM prayersPerMonth")
    fun deleteAll()

}

