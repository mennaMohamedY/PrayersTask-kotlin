package com.example.prayers_task.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [PrayersDC::class], version = 1)
abstract class AppLocalPrayersDB :RoomDatabase() {
    abstract fun prayersDAO(): PrayersRoomDAO

    companion object{

        var roomInstance:AppLocalPrayersDB?=null

        @Synchronized
        fun getDB(context: Context):AppLocalPrayersDB{
            if (roomInstance==null){
                roomInstance=Room.databaseBuilder(
                    context,
                    AppLocalPrayersDB::class.java,
                    "prayersDB"
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
            }
            return roomInstance!!
        }
    }

}