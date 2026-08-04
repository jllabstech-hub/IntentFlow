package com.intentflow.plugin.contacts.di

import com.intentflow.plugin.api.AndroidPlugin
import com.intentflow.plugin.contacts.ContactsPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContactsPluginModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideContactsPlugin(impl: ContactsPlugin): AndroidPlugin = impl
}
