package com.example.prayers_task

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.prayers_task.adapter.PrayersAdapter
import com.example.prayers_task.broadcast.SaveTime
import com.example.prayers_task.databinding.ActivityMainBinding
import com.example.prayers_task.model.Constants
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    lateinit var prayersActivityBinding:ActivityMainBinding
    lateinit var vm:PrayersScreenViewModel
    lateinit var prayersAdapter:PrayersAdapter


    lateinit var picker:MaterialTimePicker
    lateinit var calendar:Calendar
    //lateinit var picker:
    var hour:Int=0
    var minutes:Int=0
    val channelID="CHANNEL_ID_PRAYERS"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude:Double=0.0
    var longtitude:Double=0.0
    lateinit var PrayersSP:SharedPreferences

    var sharedPreferencesTime:String?=null
    lateinit var spinnerList:List<String>
    lateinit var saveTime:SaveTime


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prayersActivityBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        vm=ViewModelProvider(this).get(PrayersScreenViewModel::class.java)
        prayersAdapter=PrayersAdapter(null)
        prayersActivityBinding.prayersRV.adapter=prayersAdapter

        //for spinner part
        initSpinnerList()
        prayersActivityBinding.spinner.adapter=ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)

        vm.getCurrentDate()
        //to get notification at prayerTimes
        createNotification()

        saveTime=SaveTime(applicationContext)
//        Toast.makeText(this, "Saved time in sharedPreferences ${saveTime.getHour()}" +
//                ":${saveTime.getMin()}", Toast.LENGTH_SHORT).show()

//        prayersActivityBinding.setAlarmForPrayersTxt.setOnClickListener{
//           //chose clock to set Alarm
//            showTimePicker()
//        }

        //location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        prayersActivityBinding.setAlarm.setOnClickListener{
            prayersActivityBinding.setAlarm.setImageResource(R.drawable.ic_box_checked)

            //setAlarm()
            //setTime()
            saveTime=SaveTime(applicationContext)
            saveTime.setAlarmForAllPrayers()
        }

        PrayersSP= applicationContext.getSharedPreferences("PrayersApp", MODE_PRIVATE)
        sharedPreferencesTime=PrayersSP.getString("CurrentDate","")
        val theDayweAreIN=PrayersSP.getString("currentDAY","")

        //if there is date daved in shared preferences go and check if it's
        //current month date or not if yes continue if not set it to null
        if(!sharedPreferencesTime.isNullOrEmpty()){
            vm.checkCurrentDate(sharedPreferencesTime!!,this,applicationContext)
        }

        //if it's null then it's the first time to run the app or it's a new month so we need
        //to get the data from api

        if (sharedPreferencesTime.isNullOrEmpty()){
            getCurrentLocationFun()
            Toast.makeText(this, "long:${ PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble()}" +
                    "late ${ PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble()}", Toast.LENGTH_SHORT).show()

            vm.getPrayers( this,vm.theCurrentYear.value!!,vm.theCurrentMonth.value!!,
                PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble(),
                PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble())
            subscribeToLiveData()
            observeOnConntectivity()
            if(!vm.locationErrorMsg.value.isNullOrEmpty()){
                observeOnGPS()
            }
            onTryAgainClickListener()
        }else{
            println("late ${PrayersSP.getString(Constants.latitude, "0.0")!!.toDouble()}")
            println("long ${PrayersSP.getString(Constants.longitude, "0.0")!!.toDouble()}")
            Log.e("location","late ${PrayersSP.getString(Constants.latitude, "0.0")!!.toDouble()}")
            Log.e("location","late ${PrayersSP.getString(Constants.longitude, "0.0")!!.toDouble()}")

            Toast.makeText(this, "long:${ PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble()}" +
                    "late ${ PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble()}", Toast.LENGTH_SHORT).show()

            //get the data from room db
            //we need to pass the current day
            vm.showDataFromRoomDB( this,vm.theCurrentDate.value!!)
            subscribeToLiveData()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun subscribeToLiveData(){
        vm.dataItm.observe(this){
            prayersAdapter.upDateData(it)
        }
        vm.showLoading.observe(this){
            if(it){
                prayersActivityBinding.circularProgressIndicator.visibility=View.VISIBLE
            }
            else{
                prayersActivityBinding.circularProgressIndicator.visibility=View.INVISIBLE
            }
        }
    }
    //to handle ui if there is no internet connection and we need to get data from api
    fun observeOnConntectivity(){
        vm.errorMsg.observe(this){
            prayersActivityBinding.errorTxt.text=it
        }
        vm.isInternetConnected.observe(this){
            if(it){
                prayersActivityBinding.errorTxt.visibility=View.INVISIBLE
                prayersActivityBinding.tryAgainBtn.visibility=View.INVISIBLE
                prayersActivityBinding.circularProgressIndicator.visibility=View.INVISIBLE

            }
            else{
                prayersActivityBinding.errorTxt.text=vm.errorMsg.value
                prayersActivityBinding.errorTxt.visibility=View.VISIBLE
                prayersActivityBinding.tryAgainBtn.visibility=View.VISIBLE
                prayersActivityBinding.circularProgressIndicator.visibility=View.INVISIBLE

            }
        }
    }
    //to handle ui if the gps is turned off from the settings but permission is granted
    fun observeOnGPS(){
        vm.errorMsg.observe(this){
            prayersActivityBinding.errorTxt.text=it
        }

        //don't forget we need to handle the function try again for the gps
        vm.isCurrentLocationGranted.observe(this){
            if(it){
                prayersActivityBinding.errorTxt.visibility=View.INVISIBLE
                prayersActivityBinding.tryAgainBtn.visibility=View.INVISIBLE
                prayersActivityBinding.circularProgressIndicator.visibility=View.INVISIBLE

            }
            else{
                prayersActivityBinding.errorTxt.text=vm.locationErrorMsg.value
                prayersActivityBinding.errorTxt.visibility=View.VISIBLE
                prayersActivityBinding.tryAgainBtn.visibility=View.VISIBLE
                prayersActivityBinding.circularProgressIndicator.visibility=View.INVISIBLE

            }
        }
    }

    //reload btn if we re connected the wifi
    @RequiresApi(Build.VERSION_CODES.O)
    fun onTryAgainClickListener(){
        prayersActivityBinding.tryAgainBtn.setOnClickListener {
            if(!vm.locationErrorMsg.value.isNullOrEmpty()){
                getCurrentLocationFun()
            }
            vm.getPrayers(this,vm.theCurrentYear.value!!,vm.theCurrentMonth.value!!,
                PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble(),
                PrayersSP.getString(Constants.latitude,"0.0")!!.toDouble())
        }
    }

    //spinner part
    fun initSpinnerList(){
        spinnerList= arrayListOf(
            "1- جامعه العلوم الاسلاميه",
            "2- الجمعيه الاسلاميه لامريكا الشماليه",
            "3- رابطه العالم الاسلامي",
            "4- جامعه ام القري بمكه المكرمه",
            "5- الهيئه المصريه العامه للمساحه",
            " 6 -الكويت",
            "  7 -قطر",
        )
    }

    fun createNotification(){
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),101)
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel: NotificationChannel = NotificationChannel(
                channelID,"my channel", NotificationManager.IMPORTANCE_DEFAULT)

            val manager: NotificationManager =getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notificationCompat= NotificationManagerCompat.from(this)
    }

//    fun showTimePicker() {
//
//        picker= MaterialTimePicker.Builder()
//            .setTimeFormat(TimeFormat.CLOCK_24H)
//            .setHour(23)
//            .setMinute(0)
//            .setTitleText("select Time")
//            .build()
//
//        picker.show(supportFragmentManager,"PrayersReminder")
//        picker.addOnPositiveButtonClickListener {
//            hour=picker.hour
//            minutes=picker.minute
//            if(picker.hour>12){
//                String.format("%02d",picker.hour - 12) + ":" + String.format("%02d",picker.minute) +"PM"
//
//                hour=picker.hour
//                minutes=picker.minute
//                Toast.makeText(this, "time selected->${picker.hour}", Toast.LENGTH_SHORT).show()
//            }
//            else{
//                String.format("%02d",picker.hour) + ":" + String.format("%02d",picker.minute) +"AM"
//                Toast.makeText(this, "time selected->${picker.hour}", Toast.LENGTH_SHORT).show()
//            }
//            calendar= Calendar.getInstance()
//            calendar[Calendar.HOUR_OF_DAY]= picker.hour
//            calendar[Calendar.MINUTE]= picker.minute
//            calendar[Calendar.SECOND]= 0
//            calendar[Calendar.MILLISECOND]= 0
//        }
//    }

//    fun setTime(){
//
//        //Toast.makeText(this, "time is set to ${hour}:${min}", Toast.LENGTH_SHORT).show()
//        saveTime=SaveTime(applicationContext)
//        //supposed that the data is already saved from getPrayer and getDataFromRoom functiona
//        //saveTime.saveData(hour,min)
//        saveTime.setAlarmForAllPrayers()
//    }

    //start of location part
    private fun getCurrentLocationFun() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //if yes then we need to check on the gps whether its turned on or off
            if(isGPSPermissionGranted()){
                getCurrentLocation()
            }else{
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            //if permission is not granted ask for the permission
            requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            // requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
    }

    fun isGPSPermissionGranted():Boolean{
        return ContextCompat.
        checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(){
        val currentLocationBuilder= CurrentLocationRequest.Builder()
        currentLocationBuilder.setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        val currentLocationReq=currentLocationBuilder.build()
        fusedLocationClient.getCurrentLocation(currentLocationReq,null).addOnSuccessListener {
            latitude=it?.latitude?:0.0
            longtitude=it?.longitude?:0.0
            Toast.makeText(this, "current Location is Latitude : ${latitude} & longtitude : ${longtitude} ", Toast.LENGTH_SHORT).show()

            val editSP=PrayersSP.edit()
             editSP.putString(Constants.latitude,latitude.toString())
            editSP.putString(Constants.longitude,longtitude.toString())
            editSP.commit()
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()

            } else {
                 showDialog()
//                requestPermission()
            }
        }

//    fun requestPermission(){
//        if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){
//            //show explanation to the user why i need to access his location
//            showDialog()
//
//        }else{
//            requestPermissionLauncher.launch(
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }


    fun showDialog(){
        val alertDialog= AlertDialog.Builder(this)
        alertDialog.setMessage("We Need To Access Your Location in order to get the Exact Prayers Time" +
                "if you didn't enable the location permission you will get the prayers Time of Alexandria,Egypt ")
        alertDialog.setPositiveButton("Accept",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, p1: Int) {
                //show the permission again
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                dialog?.dismiss()
            }
        })
        alertDialog.setNegativeButton("Cancel",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, p1: Int) {
                dialog?.dismiss()
            }
        })
        alertDialog.create()
        alertDialog.show()
    }
    // end of location part


}