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
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Habit-ify!"

include(":app")

include(":core:common")
include(":core:model")
include(":core:ui")
include(":core:data")
include(":core:database")
include(":core:domain")
include(":core:designsystem")

include(":feature:habit-agenda:api")
include(":feature:habit-agenda:impl")
include(":feature:habit-add-edit:api")
include(":feature:habit-add-edit:impl")
include(":feature:habit-details:api")
include(":feature:habit-details:impl")
