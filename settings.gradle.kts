pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "IntentFlow"

include(":app")
include(":core:model")
include(":core:common")
include(":core:database")
include(":core:datastore")
include(":catalog:api")
include(":catalog:runtime")
include(":catalog:distribution")
include(":catalog:generator")
include(":catalog:validation")
include(":engine:search")
include(":engine:intent")
include(":engine:context")
include(":engine:graph")
include(":engine:planner")
include(":engine:session")
include(":engine:memory")
include(":engine:skill")
include(":engine:execution")
include(":engine:reasoning")
include(":engine:recorder")
include(":engine:benchmark")
include(":engine:replay")
include(":engine:regression")
include(":dynamic-ui")
include(":provider:api")
include(":provider:evaluator")
include(":provider:mock")
include(":provider:gemma")
include(":provider:gemini")
include(":plugin:api")
include(":plugin:system-telephony")
include(":plugin:system-contacts")
include(":plugin:system-calendar")
include(":plugin:system-settings")
include(":sdk")
include(":tooling:platform")
