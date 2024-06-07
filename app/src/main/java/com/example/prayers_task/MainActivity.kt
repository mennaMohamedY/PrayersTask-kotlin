package com.example.prayers_task

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.prayers_task.adater.PrayersAdapter
import com.example.prayers_task.databinding.ActivityMainBinding
import com.example.prayers_task.model.DataItem
import com.example.prayers_task.model.Date
import com.example.prayers_task.model.Gregorian
import com.example.prayers_task.model.Timings
import com.example.prayers_task.room.AppLocalPrayersDB
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    lateinit var prayersActivityBinding:ActivityMainBinding
    lateinit var vm:PrayersScreenViewModel
    lateinit var prayersAdapter:PrayersAdapter


    var currentDate:String?=null
    var SPTime:String?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prayersActivityBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        vm=ViewModelProvider(this).get(PrayersScreenViewModel::class.java)
        prayersAdapter=PrayersAdapter(null)
        prayersActivityBinding.prayersRV.adapter=prayersAdapter

        val PrayersSP= applicationContext.getSharedPreferences("PrayersApp", MODE_PRIVATE)
        SPTime=PrayersSP.getString("CurrentDate","")
        println("timee---->${SPTime}")
        //check if the date saved in currentDate var is equal to the current date

        SPTime?.let { checkCurrentDate(it) }

        //if it's null then it's the first time to run the app or it's a new month so we need
        //to get the data from api
        if (SPTime.isNullOrEmpty()){
            vm.getPrayers(context = this)
            subscribeToLiveData()
        }else{
            //get the data from room db
            showDataFromRoomDB()
        }
        println("${AppLocalPrayersDB.getDB(this).prayersDAO().getAllMonthPrayers().size}")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun subscribeToLiveData(){
        vm.prayers.observe(this){
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            currentDate = LocalDateTime.now().format(formatter)
            val sp=getSharedPreferences("PrayersApp", MODE_PRIVATE)
            val editor:SharedPreferences.Editor =sp.edit()
            editor.putString("CurrentDate",currentDate)
            editor.commit()
            var dataItem:DataItem?=null
            it.data?.forEach{
                //to get the current day prayers from the list
                if(it?.date?.gregorian?.date.equals(currentDate)){
                    dataItem=it
                    prayersAdapter.upDateData(it!!)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkCurrentDate(timeholder:String){

        val dateholder= timeholder?.split("-")
        val savedMonth= dateholder?.get(1)

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val currentDatee = LocalDateTime.now().format(formatter)
        val currentDateHolder=currentDatee.split("-")
        val currentMonth= currentDateHolder.get(1)


        //each new month i need to set the currentdate var to null so that i can call the api
        //and get the data from it
        if(savedMonth==currentMonth){
            return
        }else{
            currentDate =null
            val PrayersSP= applicationContext.getSharedPreferences("PrayersApp", MODE_PRIVATE)
            val edit:SharedPreferences.Editor=PrayersSP.edit()
            edit.remove("CurrentDate")
            SPTime=null
            //need to delete data stored in room in order to save the month's data
            AppLocalPrayersDB.getDB(this).prayersDAO().deleteAll()
        }
    }

    fun showDataFromRoomDB(){
        val monthPrayersData=AppLocalPrayersDB.getDB(this).prayersDAO().getAllMonthPrayers()
        monthPrayersData.forEach {
            if(it.date.equals(SPTime)){
                val datte=Date(gregorian = Gregorian(date = it.date))
                val timmings=Timings(sunset =it.sunset , sunrise = it.sunrise,
                    fajr =it.fajr , dhuhr = it.dhuhr , asr = it.asr,
                    maghrib = it.maghrib, isha = it.isha)
                val dataItem=DataItem(date = datte, timings = timmings)
                prayersAdapter.upDateData(dataItem)
            }
        }
    }
}