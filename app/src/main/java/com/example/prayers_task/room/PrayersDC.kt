package com.example.prayers_task.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "prayersPerMonth")
data class PrayersDC(

    @PrimaryKey(autoGenerate = true)
    var prayersDataItemID:Int?=null,
    @ColumnInfo
    val date: String? = null,
    @ColumnInfo
    val sunset: String? = null,
    @ColumnInfo
    val fajr: String? = null,
    @ColumnInfo
    val sunrise: String? = null,
    @ColumnInfo
    val dhuhr: String? = null,
    @ColumnInfo
    val asr: String? = null,
    @ColumnInfo
    val maghrib: String? = null,
    @ColumnInfo
    val isha: String? = null,
    )