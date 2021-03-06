rootProject.name = "kinference"

include(":algorithms")
include(":loaders")
include(":inference")
include(":ndarray")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.squareup.wire") {
                useModule("com.squareup.wire:wire-gradle-plugin:${requested.version}")
            }
        }
    }
}
