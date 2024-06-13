package com.example.prayers_task.connectivity

import kotlinx.coroutines.flow.Flow

interface CheckConnectivityObserver {

    fun observe():Flow<Status>

    enum class Status{
        Available, UnAvailable
    }
}