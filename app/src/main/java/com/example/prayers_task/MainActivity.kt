package com.example.prayers_task

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.prayers_task.adater.PrayersAdapter
import com.example.prayers_task.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener


class MainActivity : AppCompatActivity() {

    lateinit var prayersActivityBinding:ActivityMainBinding
    lateinit var vm:PrayersScreenViewModel
    lateinit var prayersAdapter:PrayersAdapter


    var sharedPreferencesTime:String?=null
    lateinit var spinnerList:List<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prayersActivityBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        vm=ViewModelProvider(this).get(PrayersScreenViewModel::class.java)
        prayersAdapter=PrayersAdapter(null)
        prayersActivityBinding.prayersRV.adapter=prayersAdapter
        initSpinnerList()
        prayersActivityBinding.spinner.adapter=ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)


        val PrayersSP= applicationContext.getSharedPreferences("PrayersApp", MODE_PRIVATE)
        sharedPreferencesTime=PrayersSP.getString("CurrentDate","")

        //if there is date daved in shared preferences go and check if it's
        //current month date or not if yes continue if not set it to null
        if(!sharedPreferencesTime.isNullOrEmpty()){
            vm.checkCurrentDate(sharedPreferencesTime!!,this,applicationContext)
        }

        //if it's null then it's the first time to run the app or it's a new month so we need
        //to get the data from api

        if (sharedPreferencesTime.isNullOrEmpty()){
            vm.getPrayers( this)
            subscribeToLiveData()
        }else{
            //get the data from room db
            vm.showDataFromRoomDB( this,sharedPreferencesTime!!)
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




}