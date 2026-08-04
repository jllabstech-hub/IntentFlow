package com.intentflow.tooling.platform.di

import com.intentflow.tooling.platform.DefaultIntentFlowTools
import com.intentflow.tooling.platform.IntentFlowTools
import com.intentflow.tooling.platform.MigrationTool
import com.intentflow.tooling.platform.PackagingTool
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ToolingModule {

    @Binds
    @Singleton
    abstract fun bindIntentFlowTools(impl: DefaultIntentFlowTools): IntentFlowTools

    @Binds
    @Singleton
    abstract fun bindPackagingTool(impl: DefaultIntentFlowTools): PackagingTool

    @Binds
    @Singleton
    abstract fun bindMigrationTool(impl: DefaultIntentFlowTools): MigrationTool
}
