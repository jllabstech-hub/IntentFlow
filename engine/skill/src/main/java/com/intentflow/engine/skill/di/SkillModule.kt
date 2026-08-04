package com.intentflow.engine.skill.di

import com.intentflow.engine.skill.DefaultSkillEngine
import com.intentflow.engine.skill.SkillEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SkillModule {

    @Binds
    @Singleton
    abstract fun bindSkillEngine(impl: DefaultSkillEngine): SkillEngine
}
