package ru.neva.drivers.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.neva.drivers.App
import javax.inject.Inject
import javax.inject.Singleton
import ru.neva.drivers.fptr.Fptr
import ru.neva.drivers.fptr.FptrHolder

@Module
@InstallIn (SingletonComponent::class)
object MainModule {
    @Singleton
    @Provides
    fun provideFptr(@ApplicationContext context: Context): Fptr {
        return Fptr(context)
    }
    @Singleton
    @Provides
    fun provideFptrHolder(fptr: Fptr): FptrHolder{
        return FptrHolder(fptr)
    }
}