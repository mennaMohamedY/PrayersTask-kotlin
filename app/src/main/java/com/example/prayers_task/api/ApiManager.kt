package com.example.prayers_task.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {

    companion object{

        var instance:Retrofit?=null

        @Synchronized
        private fun checkInstance():Retrofit{

            if(instance==null){
                instance=Retrofit.Builder().baseUrl("https://api.aladhan.com/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return instance!!
        }

        fun getAPIServices():ApiServices{
           return checkInstance().create(ApiServices::class.java)
        }



    }

}