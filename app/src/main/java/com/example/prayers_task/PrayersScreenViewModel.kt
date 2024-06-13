package com.example.prayers_task

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayers_task.api.ApiManager
import com.example.prayers_task.model.*
import com.example.prayers_task.room.AppLocalPrayersDB
import com.example.prayers_task.room.PrayersDC
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PrayersScreenViewModel :ViewModel() {
    val errorMsg=MutableLiveData<String>("")
    val locationErrorMsg=MutableLiveData<String>()
    val showLoading=MutableLiveData<Boolean>(true)
    val dataItm=MutableLiveData<DataItem>()
    var currentDate=MutableLiveData<String?>()
    var isInternetConnected=MutableLiveData<Boolean>(false)

    var theCurrentYear=MutableLiveData<Int>()
    var theCurrentMonth=MutableLiveData<Int>()
    val theCurrentDate=MutableLiveData<String>()

    var isCurrentLocationGranted=MutableLiveData<Boolean>(false)
    var isCheckChecked=MutableLiveData<Boolean>(false)
    var imgResID=MutableLiveData<Int>(R.drawable.ic_check_box)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPrayers(context: Context,year:Int,month:Int,latitude:Double,longitude:Double){
        viewModelScope.launch {
            try{
                if(isNetworkAvailable(context)) {
                    isInternetConnected.value=true
                }else{
                    isInternetConnected.value=false
                    errorMsg.value="الانترنت غير متصل برجاء الاتصال به واعاده المحاوله"
                    return@launch
                }
                //latitude 31.2001,
                //longitude 29.9187
                if (latitude==0.0 || longitude == 0.0){
                    isCurrentLocationGranted.value=false
                    locationErrorMsg.value="برجاء فتح ال gps من الاعدادات واعاده المحاوله مره اخري"
                    return@launch
                }else{
                    isCurrentLocationGranted.value=true
                }
                val response=ApiManager.getAPIServices().getAllMonthPrayers(year,month,latitude,longitude)

                showLoading.value=false
                getCurrentDate()
                val formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy")
                currentDate.value=LocalDateTime.now().format(formatter)
                val sp=context.getSharedPreferences(Constants.sharedPrefName, AppCompatActivity.MODE_PRIVATE)
                val editor:SharedPreferences.Editor =sp.edit()
                editor.putString("CurrentDate",currentDate.value)
                editor.commit()
                clearPrayersTimesfromSP(context)
                Toast.makeText(context, "In Success PrayerFun", Toast.LENGTH_SHORT).show()
                Log.e("PrayersFun","In Success PrayerFun")

                response.data?.forEach {
                    Log.e("in response","in response")
                    if(it?.date?.gregorian?.date!!.equals(currentDate.value)){
                        Log.e("in response","found it")

                        dataItm.value=it
                        val dc=PrayersDC(date = it?.date?.gregorian?.date,
                            sunrise =it?.timings?.sunrise ,
                            sunset =it?.timings?.sunset ,
                            fajr = it?.timings?.fajr,
                            dhuhr =it?.timings?.dhuhr ,
                            asr =it?.timings?.asr ,
                            maghrib =it?.timings?.maghrib ,
                            isha = it?.timings?.isha)
                        storeCurrentDayPrayersTimesToSP(context,dc)
                        Log.e("roomlength","${AppLocalPrayersDB.getDB(context).prayersDAO().getAllMonthPrayers().size}")
                    }
                    val dc=PrayersDC(date = it?.date?.gregorian?.date,
                        sunrise =it?.timings?.sunrise ,
                        sunset =it?.timings?.sunset ,
                        fajr = it?.timings?.fajr,
                        dhuhr =it?.timings?.dhuhr ,
                        asr =it?.timings?.asr ,
                        maghrib =it?.timings?.maghrib ,
                        isha = it?.timings?.isha,
                        day = it.date.gregorian.day?.toInt()
                    )
                    AppLocalPrayersDB.getDB(context).prayersDAO().addPrayerDataItem(dc)
                }
                Log.e("roomlength","${AppLocalPrayersDB.getDB(context).prayersDAO().getAllMonthPrayers().size}")

            }catch (e:Exception){
                showLoading.value=true
                errorMsg.value=e.localizedMessage.toString()
            }
        }
    }
    fun checkCheckedMark(context: Context){
        val sp=context.getSharedPreferences(Constants.sharedPrefName, AppCompatActivity.MODE_PRIVATE)
        val checked=sp.getBoolean(Constants.setPrayerAlarmChecked,false)
        val n:Int
        isCheckChecked.value=checked
        if(isCheckChecked.value!!){
            n= R.drawable.ic_box_checked
        }else{
             n= R.drawable.ic_check_box
        }
        imgResID.value=n


    }

    fun isNetworkAvailable(appContext: Context):Boolean {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        var isAvailable = false;

        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isAvailable = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isAvailable = true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isAvailable = true;
            }
        }
        return isAvailable
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
            //here i just need to update the current day date so that i can get the
            //data from room of the current date i am in
            val sharedpref=context.getSharedPreferences("PrayersApp", AppCompatActivity.MODE_PRIVATE)

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            currentDate.value = LocalDateTime.now().format(formatter)
            val editor:SharedPreferences.Editor =sharedpref.edit()
            editor.putString("currentDAY",currentDate.value)
            editor.commit()

            return
        }else{
            currentDate.value=null
            val PrayersSP= appContext.getSharedPreferences("PrayersApp",
                AppCompatActivity.MODE_PRIVATE
            )
            val edit: SharedPreferences.Editor=PrayersSP.edit()
            edit.remove("CurrentDate")

            //need to delete data stored in room in order to save the month's data
            AppLocalPrayersDB.getDB(context).prayersDAO().deleteAll()

            getCurrentDate()
            getPrayers(context,theCurrentYear.value!!,theCurrentMonth.value!!,
            PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble(),
            PrayersSP.getString(Constants.longitude,"0.0")!!.toDouble(),
            )

        }
    }

    fun showDataFromRoomDB(context: Context,timeSP:String){
        Log.e("roomlength","${AppLocalPrayersDB.getDB(context).prayersDAO().getAllMonthPrayers().size}")

        clearPrayersTimesfromSP(context)
        val monthPrayersData=AppLocalPrayersDB.getDB(context).prayersDAO().getAllMonthPrayers()
        showLoading.value=false
        monthPrayersData.forEach {
            Log.e("inroom","inRoom for each")

            if(it.date?.equals(timeSP)!!){
                Log.e("inRoom","found it")

                val datte= Date(gregorian = Gregorian(date = it.date, day = it.day.toString()))
                val timmings= Timings(sunset =it.sunset , sunrise = it.sunrise,
                    fajr =it.fajr , dhuhr = it.dhuhr , asr = it.asr,
                    maghrib = it.maghrib, isha = it.isha)
                dataItm.value=DataItem(date = datte, timings = timmings)
                storeCurrentDayPrayersTimesToSP(context,it)
            }
        }
    }

    fun storeCurrentDayPrayersTimesToSP(context: Context,prayerdc:PrayersDC){
        val sp=context.getSharedPreferences("PrayersApp", AppCompatActivity.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =sp.edit()
        editor.putString(Constants.sunrisePrayer,prayerdc.sunrise)
        editor.putString(Constants.sunsetPrayer,prayerdc.sunset)
        editor.putString(Constants.fajrPrayer,prayerdc.fajr)
        editor.putString(Constants.dhuhrPrayer,prayerdc.dhuhr)
        editor.putString(Constants.asrPrayer,prayerdc.asr)
        editor.putString(Constants.maghribPrayer,prayerdc.maghrib)
        editor.putString(Constants.ishaPrayer,prayerdc.isha)
        editor.commit()
    }

    fun clearPrayersTimesfromSP(context: Context){
        val sp=context.getSharedPreferences("PrayersApp", AppCompatActivity.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =sp.edit()
        editor.remove(Constants.sunrisePrayer)
        editor.remove(Constants.sunsetPrayer)
        editor.remove(Constants.fajrPrayer)
        editor.remove(Constants.dhuhrPrayer)
        editor.remove(Constants.asrPrayer)
        editor.remove(Constants.maghribPrayer)
        editor.remove(Constants.ishaPrayer)
        editor.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(){
        val formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy")
        theCurrentDate.value=LocalDateTime.now().format(formatter)
        val temp=theCurrentDate.value.toString().split("-")
        theCurrentYear.value=temp.get(2).toInt()
        theCurrentMonth.value=temp.get(1).toInt()
        Log.e("currentDate","${theCurrentYear.value}")
        Log.e("currentDate","${theCurrentMonth.value}")
    }






}