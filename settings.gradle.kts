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
        maven {
            url = uri("https://artifact.bytedance.com/repository/Volcengine")
            name = ""
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/Volcengine/")
            name = ""
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://artifact.bytedance.com/repository/Volcengine")
            name = ""
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/Volcengine/")
            name = ""
        }
    }
}

rootProject.name = "MateFairy0.1"
include(":app")
include(":editor-asset")
include(":mylibrary")
