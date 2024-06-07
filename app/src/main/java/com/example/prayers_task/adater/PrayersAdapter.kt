package com.example.prayers_task.adater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.prayers_task.databinding.SinglePrayerDesignBinding
import com.example.prayers_task.model.DataItem

class PrayersAdapter(var prayersList:DataItem?) :Adapter<PrayersAdapter.PrayersViewHolder>(){

    fun upDateData(prayersList:DataItem){
        this.prayersList=prayersList
        notifyDataSetChanged()
    }
    class PrayersViewHolder(var prayersBinding:SinglePrayerDesignBinding):ViewHolder(prayersBinding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayersViewHolder {
        val prayersBinding=SinglePrayerDesignBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PrayersViewHolder(prayersBinding)
    }

    override fun onBindViewHolder(holder: PrayersViewHolder, position: Int) {
       if(position==0){
           val timee=prayersList?.timings?.fajr?.split(" ")

           holder.prayersBinding.prayerTime.text="الاذان " + timee?.get(0)
           holder.prayersBinding.prayerName.text="الفجر"

       }else if(position==1){
           val timee=prayersList?.timings?.sunrise?.split(" ")
           holder.prayersBinding.prayerTime.text="الاذان " + timee?.get(0)
           holder.prayersBinding.prayerName.text="الشروق"

       }else if(position==2){
           val timee=prayersList?.timings?.dhuhr?.split(" ")
           holder.prayersBinding.prayerTime.text="الاذان " + timee?.get(0)
           holder.prayersBinding.prayerName.text="الظهر"

       }else if(position==3){
           val timee= prayersList?.timings?.asr?.split(" ")
           holder.prayersBinding.prayerTime.text="الاذان " + timee?.get(0)
           holder.prayersBinding.prayerName.text="العصر"

       }else if(position==4){
           val timee=prayersList?.timings?.maghrib?.split(" ")
           holder.prayersBinding.prayerTime.text="الاذان " + timee?.get(0)
           holder.prayersBinding.prayerName.text="المغرب"

       }else if(position==5){
           val timee = prayersList?.timings?.isha?.split(" ")
           holder.prayersBinding.prayerTime.text="الاذان " + timee?.get(0)
           holder.prayersBinding.prayerName.text="العشاء"
       }
    }

    override fun getItemCount(): Int {
         if(prayersList==null){
            return 0
        }else
            return 6
    }
}