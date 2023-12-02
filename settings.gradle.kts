val modID: String by settings

include(modID)
pluginManagement {
    val forgeGradleVersion: String by settings
    val parchmentLibrarianVersion: String by settings

    plugins {
        id("net.minecraftforge.gradle") version "${forgeGradleVersion}"
        id("org.parchmentmc.librarian.forgegradle") version "${parchmentLibrarianVersion}"
    }

    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://maven.parchmentmc.org")
        maven(url = "https://www.cursemaven.com/")
    }
}