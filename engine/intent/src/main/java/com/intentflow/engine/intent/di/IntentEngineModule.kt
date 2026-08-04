package com.intentflow.engine.intent.di

import android.content.Context
import com.intentflow.engine.execution.ExecutionEngine
import com.intentflow.engine.intent.ConfidenceScorer
import com.intentflow.engine.intent.SlotExtractor
import com.intentflow.engine.intent.SlotResolver
import com.intentflow.engine.intent.SlotSuggestionEngine
import com.intentflow.engine.intent.SlotValidator
import com.intentflow.engine.intent.event.DefaultPipelineEventBus
import com.intentflow.engine.intent.event.PipelineEventBus
import com.intentflow.engine.intent.learning.LearningEngine
import com.intentflow.engine.intent.runtime.ExecutionPlanner
import com.intentflow.engine.intent.runtime.IntentRuntime
import com.intentflow.engine.intent.runtime.RuntimeErrorHandler
import com.intentflow.engine.intent.runtime.RuntimeStateMachine
import com.intentflow.engine.intent.telemetry.LocalTelemetryManager
import com.intentflow.engine.intent.voice.SpeechRecognizerProvider
import com.intentflow.engine.intent.voice.VoiceIntentEnginePipeline
import com.intentflow.engine.kernel.DefaultIntentKernel
import com.intentflow.engine.kernel.IntentKernel
import com.intentflow.engine.planner.CapabilityExecutionPlanner
import com.intentflow.engine.planner.IntentPlanningEngine
import com.intentflow.engine.session.IntentSessionManager
import com.intentflow.engine.understanding.IntentUnderstandingEngine
import com.intentflow.engine.understanding.RuleBasedIntentUnderstandingEngine
import com.intentflow.plugin.api.CapabilityRegistry
import com.intentflow.plugin.api.PluginCapabilityRegistry
import com.intentflow.provider.api.IntentExecutorProvider
import com.intentflow.provider.api.ProviderManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IntentEngineModule {

    @Binds
    @Singleton
    abstract fun bindIntentUnderstandingEngine(
        impl: RuleBasedIntentUnderstandingEngine
    ): IntentUnderstandingEngine

    @Binds
    @Singleton
    abstract fun bindPipelineEventBus(
        impl: DefaultPipelineEventBus
    ): PipelineEventBus

    @Binds
    @Singleton
    abstract fun bindIntentKernel(
        impl: DefaultIntentKernel
    ): IntentKernel

    companion object {
        @Provides
        @Singleton
        fun provideSlotExtractor(): SlotExtractor = SlotExtractor()

        @Provides
        @Singleton
        fun provideConfidenceScorer(): ConfidenceScorer = ConfidenceScorer()

        @Provides
        @Singleton
        fun provideSlotValidator(): SlotValidator = SlotValidator()

        @Provides
        @Singleton
        fun provideSlotSuggestionEngine(): SlotSuggestionEngine = SlotSuggestionEngine()

        @Provides
        @Singleton
        fun provideSlotResolver(
            validator: SlotValidator,
            suggestionEngine: SlotSuggestionEngine
        ): SlotResolver = SlotResolver(validator, suggestionEngine)

        @Provides
        @Singleton
        fun provideLearningEngine(): LearningEngine = LearningEngine()

        @Provides
        @Singleton
        fun provideLocalTelemetryManager(): LocalTelemetryManager = LocalTelemetryManager()

        @Provides
        @Singleton
        fun provideRuntimeErrorHandler(): RuntimeErrorHandler = RuntimeErrorHandler()

        @Provides
        @Singleton
        fun provideExecutionPlanner(): ExecutionPlanner = ExecutionPlanner()

        @Provides
        @Singleton
        fun provideRuntimeStateMachine(): RuntimeStateMachine = RuntimeStateMachine()

        @Provides
        @Singleton
        fun provideSpeechRecognizerProvider(@ApplicationContext context: Context): SpeechRecognizerProvider =
            SpeechRecognizerProvider(context)

        @Provides
        @Singleton
        fun provideVoiceIntentEnginePipeline(
            speechProvider: SpeechRecognizerProvider,
            intentRuntime: IntentRuntime
        ): VoiceIntentEnginePipeline = VoiceIntentEnginePipeline(speechProvider, intentRuntime)

        @Provides
        @Singleton
        fun provideIntentRuntime(
            understandingEngine: IntentUnderstandingEngine,
            planningEngine: IntentPlanningEngine,
            executorProvider: IntentExecutorProvider,
            stateMachine: RuntimeStateMachine,
            errorHandler: RuntimeErrorHandler,
            eventBus: PipelineEventBus,
            telemetryManager: LocalTelemetryManager
        ): IntentRuntime {
            return IntentRuntime(
                understandingEngine = understandingEngine,
                planningEngine = planningEngine,
                executorProvider = executorProvider,
                stateMachine = stateMachine,
                errorHandler = errorHandler,
                eventBus = eventBus,
                telemetryManager = telemetryManager
            )
        }
    }
}
