package com.intentflow.plugin.telephony.di

import com.intentflow.plugin.api.AndroidPlugin
import com.intentflow.plugin.telephony.TelephonyPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TelephonyPluginModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideTelephonyPlugin(impl: TelephonyPlugin): AndroidPlugin = impl
}
