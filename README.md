# IntentFlow — Universal Intent Layer for Android

[![Sprint](https://img.shields.io/badge/Sprint-1%20Complete-brightgreen)](docs/IMPLEMENTATION_STATUS.md)
[![Architecture](https://img.shields.io/badge/Architecture-v1.0%20Frozen-blue)](docs/MASTER_SPEC.md)
[![Platform Engineering](https://img.shields.io/badge/Platform%20Engineering-v1.0%20Frozen-blue)](docs/walkthrough.md)
[![Min SDK](https://img.shields.io/badge/MinSDK-31%20(Android%2012)-orange)]()
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-purple)]()

**IntentFlow** converts natural language into structured, deterministic, offline-first executable intents (`IntentObject`). Instead of asking users to write complex prompts, IntentFlow progressively understands user intent, identifies missing information, renders interactive Jetpack Compose UI dynamically, and executes completed intents via any supported AI provider or native Android capability.

> **Architecture is frozen at v1.0. Do not redesign. Implement only.**

---

## Core Philosophy

```
Natural Language
  → Intent Detection
  → Slot Extraction
  → Context Enrichment
  → Dynamic UI
  → Execution Plan
  → Provider Execution
  → Result
```

**`IntentObject` is the universal internal language.** Every module communicates exclusively through `IntentObject`. No domain-specific screens (FlightScreen, PaymentScreen, etc.) are ever permitted.

---

## Technology Stack

| Category | Technology |
|---|---|
| Language | Kotlin 1.9.23 |
| UI Framework | Jetpack Compose + Material 3 |
| Dependency Injection | Hilt 2.51.1 |
| Navigation | Navigation Compose 2.7.7 |
| Local Database | Room 2.6.1 + FTS5 |
| Preferences | DataStore Preferences 1.1.1 |
| Networking | Ktor 2.3.9 |
| Serialization | Kotlinx Serialization 1.6.3 |
| Async / Reactive | Kotlin Coroutines 1.8.0 + StateFlow |
| Image Loading | Coil 2.6.0 |
| Logging | Timber 5.0.1 |
| Testing | JUnit 4 + MockK 1.13.10 |
| Min SDK | Android 12 (API 31) |
| Target SDK | Android 14 (API 34) |
| Java Target | 17 |

---

## Multi-Module Architecture

```
IntentFlow/
├── gradle/libs.versions.toml       # Version Catalog (all dependency versions)
├── app/                            # Entry point: Hilt, MainActivity, Navigation, Theme
├── core/
│   ├── model/                      # IntentObject, SlotValue, Domain, all data models
│   ├── common/                     # DispatcherProvider, IntentLogger, IntentResult
│   ├── database/                   # Room + FTS5 database, DAOs
│   └── datastore/                  # DataStore preferences repository
├── catalog/
│   ├── api/                        # KnowledgeCatalog interface contracts
│   ├── runtime/                    # Room/FTS5 catalog repository
│   ├── distribution/               # OTA catalog delivery (CatalogManager)
│   ├── generator/                  # Automated catalog generation pipeline
│   └── validation/                 # 9-point catalog validation pipeline
├── engine/
│   ├── intent/                     # Intent Kernel OS — core pipeline
│   ├── search/                     # FTS5 + Levenshtein hybrid search
│   ├── context/                    # 11 on-device context providers
│   ├── graph/                      # Intent DAG builder
│   ├── planner/                    # Execution planner
│   ├── session/                    # Intent Session Manager
│   ├── memory/                     # Intent Memory Engine
│   ├── skill/                      # Composite Skill Engine
│   ├── execution/                  # Execution engine (queue, retry, parallel)
│   ├── reasoning/                  # Optional AI Reasoning Engine
│   ├── recorder/                   # Interaction trace recorder
│   ├── benchmark/                  # Self-evaluation benchmark framework
│   ├── replay/                     # Session replay engine
│   └── regression/                 # Automated regression CI gate
├── dynamic-ui/                     # Jetpack Compose dynamic form renderer
├── provider/
│   ├── api/                        # IntentExecutorProvider interface
│   ├── mock/                       # Deterministic mock provider
│   ├── gemma/                      # Google AI Edge on-device provider
│   ├── gemini/                     # Gemini Cloud API provider
│   └── evaluator/                  # Multi-provider comparison (AI benchmarking)
├── plugin/
│   ├── api/                        # AndroidPlugin base + CapabilityRegistry
│   ├── system-telephony/           # SMS + Call plugin
│   ├── system-contacts/            # Contacts plugin
│   ├── system-calendar/            # Calendar plugin
│   └── system-settings/            # Settings plugin
├── sdk/                            # Public SDK facade for 3rd-party developers
├── tooling/
│   └── platform/                   # IntentFlowTools CLI + 10 developer tools
└── docs/
    ├── AI_PLAYBOOK.md              # AI assistant operating manual
    ├── MASTER_SPEC.md              # Master platform specification
    ├── IMPLEMENTATION_STATUS.md    # Sprint tracker
    ├── implementation_plan.md      # Architecture document (frozen)
    └── walkthrough.md              # Platform engineering v1.0 (frozen)
```

---

## Sprint Status

| Sprint | Name | Status |
|---|---|---|
| **Sprint 1** | **Android Foundation** | **✅ Complete** |
| Sprint 2 | Core Models + Catalog Engine | ⬜ Not Started |
| Sprint 3 | Search Engine | ⬜ Not Started |
| Sprint 4 | Intent Kernel | ⬜ Not Started |
| Sprint 5 | Dynamic UI | ⬜ Not Started |
| Sprint 6 | Provider Layer | ⬜ Not Started |
| Sprint 7 | Plugin Layer | ⬜ Not Started |
| Sprint 8 | Session + Memory | ⬜ Not Started |
| Sprint 9 | Platform Console | ⬜ Not Started |
| Sprint 10 | SDK + Tooling | ⬜ Not Started |

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK (API 34)

### Build

```bash
./gradlew assembleDebug
```

### Run Tests

```bash
./gradlew test
```

---

## Architecture Rules (Frozen — Do Not Violate)

1. **Never hardcode domain screens** — `FlightScreen`, `PaymentScreen`, etc. are forbidden.
2. **All UI is generated dynamically** from `IntentObject → SlotDefinitions → Compose`.
3. **No AI dependency in core engine** — providers are pluggable executors.
4. **Offline-first** — all core functionality works without network.
5. **`IntentObject` is the universal language** — all modules communicate via it.
6. **No user data leaves the device** — privacy-first by design.
7. **Catalog updates require zero APK changes** — OTA catalog delivery only.
8. **Dependency direction**: `app → engine → catalog → core:model` (never reversed).

---

## Documentation

| Document | Purpose |
|---|---|
| [AI_PLAYBOOK.md](docs/AI_PLAYBOOK.md) | Operating manual for AI coding assistants |
| [MASTER_SPEC.md](docs/MASTER_SPEC.md) | Full platform specification |
| [IMPLEMENTATION_STATUS.md](docs/IMPLEMENTATION_STATUS.md) | Sprint progress tracker |
| [implementation_plan.md](docs/implementation_plan.md) | Architecture definition (frozen v1.0) |
| [walkthrough.md](docs/walkthrough.md) | Platform Engineering v1.0 (frozen) |

---

## License

Internal development project — IntentFlow Platform Engineering v1.0.
