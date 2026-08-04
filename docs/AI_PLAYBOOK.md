# IntentFlow AI Playbook
Version: 1.0
Status: ACTIVE

> This document is the permanent operating manual for all AI coding assistants
> working on the IntentFlow project.

---

# 1. Purpose

This playbook defines how every AI assistant (Antigravity, Cursor, Codex,
ChatGPT, Claude, Gemini, or future assistants) must implement IntentFlow.

The architecture is frozen.

AI assistants must implement features—not redesign the platform.

---

# 2. Source of Truth (Highest → Lowest)

1. implementation_plan.md
2. walkthrough.md
3. MASTER_SPEC.md
4. AI_PLAYBOOK.md

If conflicts exist:
- Stop
- Explain the conflict
- Wait for approval

---

# 3. Architecture Status

Architecture: IntentFlow Runtime v1.0
Platform Engineering: v1.0
Status: FROZEN

Never:
- Rename packages
- Rename modules
- Introduce new architecture
- Change dependency directions
- Replace core technologies without approval

---

# 4. Product Vision

IntentFlow is an Android-native Universal Intent Layer.

Pipeline:

Natural Language
→ Intent Understanding
→ Intent Graph
→ Intent Planning
→ IntentObject
→ Execution
→ Result

IntentObject is the platform's internal language.

---

# 5. Technology Stack

- Kotlin
- Jetpack Compose
- Material 3
- Clean Architecture
- MVVM
- Repository Pattern
- SOLID
- Hilt
- Coroutines
- StateFlow
- Kotlin Serialization
- Room
- DataStore
- Ktor
- Coil
- Timber
- JUnit
- MockK

---

# 6. Module Structure

Do not change.

- app
- sdk
- catalog
- core
- engine
- presentation
- provider
- plugin
- tooling
- benchmark
- samples
- docs

---

# 7. Universal Rules

Everything must be:

- Offline-first
- Metadata-driven
- Modular
- Reusable
- Thread-safe
- Testable
- Production-ready

Never hardcode:

- Screens
- Domains
- Intents
- Slots
- Permissions
- Forms

---

# 8. Dynamic UI Rules

Always render:

IntentObject
→ IntentSchema
→ UISchema
→ Renderer
→ Compose

Never create feature-specific screens such as:

- FlightScreen
- ReminderScreen
- PaymentScreen

---

# 9. Provider Rules

Providers are interchangeable.

Supported:
- Gemma
- Gemini
- OpenAI
- Claude
- Mock

Execution always flows through:

Execution Engine
→ Capability Registry
→ Provider

---

# 10. Plugin Rules

Plugins expose Android capabilities only.

Examples:
- Contacts
- SMS
- Calendar
- Maps
- Camera
- Gallery
- Settings

Plugins never call providers directly.

---

# 11. Coding Standards

Generate:

- Complete code
- Imports
- Package declarations
- KDoc
- Unit tests where appropriate

Never generate:

- TODO
- FIXME
- Placeholder implementations
- Pseudocode

---

# 12. Performance

- Lazy loading
- Immutable models
- Background I/O
- Minimal allocations
- Cold-start optimized

---

# 13. Security

- Android Keystore
- AES encryption for local memory
- Respect runtime permissions
- Never upload user data implicitly

---

# 14. Testing

Generate tests for:

- Engines
- Validators
- Repositories
- Search
- Planner
- Kernel
- Providers
- Plugins

---

# 15. Implementation Workflow

Before coding:

1. Read implementation_plan.md
2. Read walkthrough.md
3. Read MASTER_SPEC.md
4. Read AI_PLAYBOOK.md
5. Scan repository
6. Search existing code
7. Reuse implementations
8. Implement only requested feature

---

# 16. Session Continuity

Before every task:

- Read project documents
- Scan repository
- Detect completed work
- Continue from last completed sprint
- Never regenerate existing functionality
- Update implementation progress

Always continue from the current repository state.

---

# 17. AI Collaboration Rules

Assume another AI assistant has modified the repository.

Before generating code:

- Scan repository
- Compare current state
- Reuse existing implementations
- Never overwrite working code unnecessarily
- Prefer incremental modifications
- Preserve architecture consistency

If conflicts exist:

STOP and explain.

---

# 18. Code Ownership Rules

Before modifying any file:

- Understand current responsibility
- Preserve backward compatibility
- Avoid unrelated refactoring
- Modify only required files

---

# 19. Build Rules

Every sprint must end with:

- Successful build
- Passing tests
- Updated documentation
- No TODOs
- No placeholders

---

# 20. Definition of Done

A task is complete only if:

- Code compiles
- Tests pass
- Architecture respected
- Documentation updated
- Existing functionality preserved

---

# 21. Prompt Template

For every implementation task:

Read:
- AI_PLAYBOOK.md
- implementation_plan.md
- walkthrough.md
- MASTER_SPEC.md

Then:

- Scan repository
- Reuse existing code
- Implement only the requested sprint
- Do not modify architecture
- Generate production-ready code
- Update implementation status

---

# 22. Final Rule

Architecture is frozen.

Implementation only.

If an architectural change appears necessary:

STOP.

Explain the issue.

Wait for approval.

Never silently redesign the platform.
