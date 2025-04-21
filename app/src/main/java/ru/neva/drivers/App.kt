package ru.neva.drivers

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.neva.drivers.fptr.FptrHolder
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var fptrHolder: FptrHolder
    override fun onCreate() {
        super.onCreate()
        fptrHolder.fptr.close()
    }

}