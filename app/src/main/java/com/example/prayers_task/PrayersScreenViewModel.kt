package com.example.prayers_task

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prayers_task.api.ApiManager
import com.example.prayers_task.model.*
import com.example.prayers_task.room.AppLocalPrayersDB
import com.example.prayers_task.room.PrayersDC
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PrayersScreenViewModel :ViewModel() {
    val errorMsg=MutableLiveData<String>()
    val showLoading=MutableLiveData<Boolean>(true)
    val dataItm=MutableLiveData<DataItem>()
    var currentDate=MutableLiveData<String?>()

    fun getPrayers(context: Context){
        ApiManager.getAPIServices().getAllMonthPrayers(2024,6,31.2001,29.9187)
            .enqueue(object :Callback<PrayersResponse>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<PrayersResponse>,
                response: Response<PrayersResponse>
            ) {
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                showLoading.value=false
                currentDate.value = LocalDateTime.now().format(formatter)
                val sp=context.getSharedPreferences("PrayersApp", AppCompatActivity.MODE_PRIVATE)
                val editor:SharedPreferences.Editor =sp.edit()
                editor.putString("CurrentDate",currentDate.value)
                editor.commit()

                response.body()?.data?.forEach {
                    if(it?.date?.gregorian?.date.equals(currentDate.value)){
                        dataItm.value=it
                    }

                    val dc=PrayersDC(date = it?.date?.gregorian?.date,
                        sunrise =it?.timings?.sunrise ,
                    sunset =it?.timings?.sunset ,
                    fajr = it?.timings?.fajr,
                    dhuhr =it?.timings?.dhuhr ,
                    asr =it?.timings?.asr ,
                    maghrib =it?.timings?.maghrib ,
                    isha = it?.timings?.isha)
                    AppLocalPrayersDB.getDB(context).prayersDAO().addPrayerDataItem(dc)

                }
            }
            override fun onFailure(call: Call<PrayersResponse>, t: Throwable) {
                showLoading.value=true
                errorMsg.value=t.localizedMessage.toString()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkCurrentDate(timeholder:String,context: Context,appContext: Context){

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
            currentDate.value=null
            val PrayersSP= appContext.getSharedPreferences("PrayersApp",
                AppCompatActivity.MODE_PRIVATE
            )
            val edit: SharedPreferences.Editor=PrayersSP.edit()
            edit.remove("CurrentDate")
            getPrayers(context)

            //need to delete data stored in room in order to save the month's data
            AppLocalPrayersDB.getDB(context).prayersDAO().deleteAll()
        }
    }

    fun showDataFromRoomDB(context: Context,timeSP:String){
        val monthPrayersData=AppLocalPrayersDB.getDB(context).prayersDAO().getAllMonthPrayers()
        showLoading.value=false
        monthPrayersData.forEach {
            if(it.date?.equals(timeSP)!!){
                val datte= Date(gregorian = Gregorian(date = it.date))
                val timmings= Timings(sunset =it.sunset , sunrise = it.sunrise,
                    fajr =it.fajr , dhuhr = it.dhuhr , asr = it.asr,
                    maghrib = it.maghrib, isha = it.isha)
                dataItm.value=DataItem(date = datte, timings = timmings)
            }
        }
    }

}