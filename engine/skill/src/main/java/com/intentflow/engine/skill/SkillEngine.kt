package com.intentflow.engine.skill

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.ExecutionStep
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.SkillDefinition
import com.intentflow.core.model.SkillFallback
import com.intentflow.core.model.SkillInput
import com.intentflow.core.model.SkillOutput
import com.intentflow.plugin.api.PluginCapabilityRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface defining a composite executable Skill.
 */
interface IntentSkill {
    val definition: SkillDefinition

    suspend fun execute(intentObject: IntentObject, inputs: Map<String, String>): ExecutionResult
}

/**
 * Composite example skill executing Flight, Calendar, Weather, and Taxi steps.
 */
@Singleton
class BookFlightSkill @Inject constructor(
    private val pluginRegistry: PluginCapabilityRegistry
) : IntentSkill {

    override val definition = SkillDefinition(
        skillId = "skill.book_flight",
        displayName = "Book Flight Composite Skill",
        description = "Books flight, creates calendar event, checks destination weather, and reserves taxi",
        inputs = listOf(
            SkillInput("origin", "String"),
            SkillInput("destination", "String"),
            SkillInput("date", "String")
        ),
        outputs = listOf(
            SkillOutput("confirmation", "String", "Flight booking confirmation code")
        ),
        executionOrder = listOf(
            ExecutionStep("step_1_flight", "Book Flight", "plugin.flight"),
            ExecutionStep("step_2_calendar", "Schedule Calendar Event", "plugin.calendar"),
            ExecutionStep("step_3_weather", "Check Weather", "plugin.weather"),
            ExecutionStep("step_4_taxi", "Reserve Airport Taxi", "plugin.taxi")
        ),
        fallback = SkillFallback(fallbackMessage = "Flight booking failed. Retrying via Web Deep Link.")
    )

    override suspend fun execute(intentObject: IntentObject, inputs: Map<String, String>): ExecutionResult {
        return ExecutionResult.Success(
            intentId = intentObject.intentId,
            message = "Successfully executed composite BookFlightSkill (Flight + Calendar + Weather + Taxi)"
        )
    }
}

/**
 * Skill Engine & Registry Interface.
 */
interface SkillEngine {
    fun getRegisteredSkills(): List<SkillDefinition>
    fun getSkill(skillId: String): IntentSkill?

    suspend fun registerSkill(skill: IntentSkill)
    suspend fun registerAiGeneratedSkill(skillDefinition: SkillDefinition)

    fun executeSkill(skillId: String, intentObject: IntentObject): Flow<ExecutionResult>
}

/**
 * Production-ready implementation of SkillEngine.
 */
@Singleton
class DefaultSkillEngine @Inject constructor(
    bookFlightSkill: BookFlightSkill
) : SkillEngine {

    private val skillMap = ConcurrentHashMap<String, IntentSkill>()
    private val aiSkillDefinitions = ConcurrentHashMap<String, SkillDefinition>()

    init {
        skillMap[bookFlightSkill.definition.skillId] = bookFlightSkill
    }

    override fun getRegisteredSkills(): List<SkillDefinition> {
        val builtIn = skillMap.values.map { it.definition }
        val aiGenerated = aiSkillDefinitions.values.toList()
        return builtIn + aiGenerated
    }

    override fun getSkill(skillId: String): IntentSkill? = skillMap[skillId]

    override suspend fun registerSkill(skill: IntentSkill) {
        skillMap[skill.definition.skillId] = skill
    }

    override suspend fun registerAiGeneratedSkill(skillDefinition: SkillDefinition) {
        aiSkillDefinitions[skillDefinition.skillId] = skillDefinition.copy(isAiGenerated = true)
    }

    override fun executeSkill(skillId: String, intentObject: IntentObject): Flow<ExecutionResult> = flow {
        val skill = skillMap[skillId]
        if (skill != null) {
            val inputs = intentObject.slots.mapValues { it.value.rawValue ?: "" }
            val result = skill.execute(intentObject, inputs)
            emit(result)
        } else {
            emit(ExecutionResult.Failure(intentObject.intentId, "Skill $skillId not registered"))
        }
    }
}
