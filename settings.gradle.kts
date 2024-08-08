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
        maven { url = uri("https://jitpack.io") }

    }


}

rootProject.name = "CalorieMateWise"
include(":app")

include(":core:data")
include(":core:domain")
include(":core:utils")
include(":feature")

include(":feature:auth")
include(":feature:detail")
include(":feature:home")
include(":feature:profile")
include(":feature:search")
include(":feature:signin")
include(":feature:signup")
include(":feature:search-interactive")
include(":feature:meal-planning")
