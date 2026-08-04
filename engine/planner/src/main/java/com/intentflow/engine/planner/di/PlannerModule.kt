package com.intentflow.engine.planner.di

import com.intentflow.engine.planner.CapabilityExecutionPlanner
import com.intentflow.engine.planner.DefaultCapabilityExecutionPlanner
import com.intentflow.engine.planner.DefaultIntentPlanner
import com.intentflow.engine.planner.DefaultIntentPlanningEngine
import com.intentflow.engine.planner.IntentPlanner
import com.intentflow.engine.planner.IntentPlanningEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlannerModule {

    @Binds
    @Singleton
    abstract fun bindIntentPlanningEngine(impl: DefaultIntentPlanningEngine): IntentPlanningEngine

    @Binds
    @Singleton
    abstract fun bindCapabilityExecutionPlanner(impl: DefaultCapabilityExecutionPlanner): CapabilityExecutionPlanner

    @Binds
    @Singleton
    abstract fun bindIntentPlanner(impl: DefaultIntentPlanner): IntentPlanner
}
