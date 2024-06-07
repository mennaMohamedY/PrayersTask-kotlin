package com.example.prayers_task

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prayers_task.api.ApiManager
import com.example.prayers_task.model.DataItem
import com.example.prayers_task.model.PrayersResponse
import com.example.prayers_task.room.AppLocalPrayersDB
import com.example.prayers_task.room.PrayersDC
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrayersScreenViewModel :ViewModel() {
    val prayers=MutableLiveData<PrayersResponse>()
    val hideLoading=MutableLiveData<Boolean>()
    val errorMsg=MutableLiveData<String>()

    fun getPrayers(context: Context){
        ApiManager.getAPIServices().getAllMonthPrayers(2024,6,31.2001,29.9187)
            .enqueue(object :Callback<PrayersResponse>{
            override fun onResponse(
                call: Call<PrayersResponse>,
                response: Response<PrayersResponse>
            ) {
                prayers.value=response.body()
                hideLoading.value=true
                println("Success!!!!")
                println("Success2 lengthhh!!!!->>>>> ${response.body()?.data?.size}")
                response.body()?.data?.forEach {
                    println("success---> ${it?.date?.gregorian?.date}")

                    val dc=PrayersDC(date = it?.date?.gregorian?.date,
                        sunrise =it?.timings?.sunrise ,
                    sunset =it?.timings?.sunset ,
                    fajr = it?.timings?.fajr,
                    dhuhr =it?.timings?.dhuhr ,
                    asr =it?.timings?.asr ,
                    maghrib =it?.timings?.maghrib ,
                    isha = it?.timings?.isha)
                    AppLocalPrayersDB.getDB(context).prayersDAO().addPrayerDataItem(dc)
                    println("${AppLocalPrayersDB.getDB(context).prayersDAO().getAllMonthPrayers().size}")

                    Toast.makeText(context, "room DB length--->${AppLocalPrayersDB.getDB(context).prayersDAO().getAllMonthPrayers().size}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<PrayersResponse>, t: Throwable) {
                hideLoading.value=false
                errorMsg.value=t.localizedMessage.toString()
                println("Failed!!!!!!!, ${t.localizedMessage.toString()}")
            }
        })
    }

}