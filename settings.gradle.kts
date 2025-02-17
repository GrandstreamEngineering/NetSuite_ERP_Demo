pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "Netsuite ERP"
include(":app")
include(":mvi")
include(":toast")
project(":mvi").projectDir = File(settingsDir, "../../sharedLibs/AppCommonLibs/mvi")
project(":toast").projectDir = File(settingsDir, "../../sharedLibs/AppUILibs/toast")