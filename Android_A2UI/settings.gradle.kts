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
rootProject.name = "AndroidUIRenderer"
include(":app")

buildCache {
    local {
        directory = File(System.getProperty("user.home"), ".gradle/build-cache")
    }
}

gradle.projectsLoaded {
    rootProject.layout.buildDirectory.set(File(System.getProperty("user.home"), ".gradle/builds/AndroidUIRenderer"))
}