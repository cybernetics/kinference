rootProject.name = "kotlin-mpp-inference"

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
