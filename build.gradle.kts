import java.util.*

plugins {
    java
    idea
    id("net.minecraftforge.gradle") version "5.1.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
}

// Variable layout matching the gradle.properties. MATCH THE ORDERING!
// Mod
val modID: String by project
val modName: String by project
val modAuthors: String by project
val modCredits: String by project
val modIssueTrackerURL: String by project
val modHomeURL: String by project
val modDescription: String by project
val modLicense: String by project
val modVersion: String by project

// Resources
val resourcesPackFormat: String by project
val resourcesDescription: String by project

// Forge
val forgeVersion: String by project

// Minecraft
val minecraftVersion: String by project

// Dev Workspace
val mappingsType: String by project
val parchmentVersion: String by project
val mappingsVersion: String = when (mappingsType) {
    "parchment" -> "${minecraftVersion}-${parchmentVersion}-${minecraftVersion}"
    else -> minecraftVersion
}
//TODO: Fix this.
val useAdvancedClassRedef: Boolean = true

// Computed variables not found in the gradle.properties file.
val forgeVersionRange: String = "[" + forgeVersion.substring(0, 2) + ",)"

println("Used Mappings: $mappingsType / $mappingsVersion with mod version $modVersion")

val resourcesGenerated: String = "${buildDir}/generated-resources/resources"
val modsTomlGenerated: File = file("${resourcesGenerated}/META-INF/mods.toml")
val packMetaGenerated: File = file("${resourcesGenerated}/pack.mcmeta")

base {
    archivesName.set("${modID}-forge-${minecraftVersion}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(project.properties["javaVersion"] as String))
}

repositories {
    mavenCentral()
    maven(url = "https://www.cursemaven.com") // CurseMaven
    {
        content {
            includeGroup("curse.maven")
        }
    }
    flatDir {
        dir("libs")
    }
}

dependencies {
    minecraft("net.minecraftforge", "forge", version = "$minecraftVersion-$forgeVersion")
}

sourceSets {
    main {
        resources {
            srcDir(resourcesGenerated)
        }
    }
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Version"] = modVersion
        }
    }

    // Inserts the properties into whatever files.
    withType<ProcessResources>
    {
        mkdir("${resourcesGenerated}/META-INF")
        modsTomlGenerated.createNewFile()
        modsTomlGenerated.writeText(
                "modLoader = \"javafml\"\n" +
                        "loaderVersion = \"${forgeVersionRange}\"\n" +
                        "license = \"${modLicense}\"\n" +
                        "\n" +
                        "[[mods]]\n" +
                        "modId=\"${modID}\"\n" +
                        "version=\"${modVersion}\"\n" +
                        "displayName=\"${modName}\"\n" +
                        "issueTrackerURL=\"${modIssueTrackerURL}\"\n" +
                        "displayURL=\"${modHomeURL}\"\n" +
                        "credits=\"${modCredits}\"\n" +
                        "authors=\"${modAuthors}\"\n" +
                        "description='''\n" +
                        "${modDescription}\n" +
                        "'''")

        val reg = "^(dep\\d+).*\$".toRegex()
        val uniqueDep: MutableSet<String> = mutableSetOf()
        val depList: List<String> = getProperties().keys.filter { it.toLowerCase(Locale.ROOT).contains(reg) }

        depList.forEach { dep ->
            val subString: MatchResult? = reg.find(dep, 0)
            if (subString != null) {
                uniqueDep.add(subString.groupValues[1])
            }
        }
        uniqueDep.forEach { dep ->
            modsTomlGenerated.appendText(
                    "\n" +
                            "[[dependencies.${modID}]]\n" +
                            "    modId = \"" + project.properties[dep + "ID"] + "\"\n" +
                            "    mandatory = \"" + project.properties[dep + "Mandatory"] + "\"\n" +
                            "    versionRange = \"" + project.properties[dep + "VersionRange"] + "\"\n" +
                            "    ordering = \"" + project.properties[dep + "Order"] + "\"\n" +
                            "    side = \"" + project.properties[dep + "Side"] + "\"" +
                            "\n")
        }

        packMetaGenerated.createNewFile()
        packMetaGenerated.writeText("{\n" +
                "    \"pack\": {\n" +
                "       \"pack_format\": ${resourcesPackFormat},\n" +
                "       \"description\": \"${resourcesDescription}\"\n" +
                "    }\n" +
                "}")

        finalizedBy("fixResourcesForFML")
    }

    // REQUIRED TO FIX https://youtrack.jetbrains.com/issue/IDEA-196103/IntelliJ-Gradle-ProcessResources-out-directory-classpath-issue
    // Can't use suggested option of change the run actions to "Use Gradle" because FML seems to require the out directory.
    register<Copy>("fixResourcesForFML")
    {
        val outFile: File = file(layout.projectDirectory.dir("out"))
        if (outFile.exists()) {
            dependsOn(processResources, compileJava, "prepareRunClient")
            from(layout.projectDirectory.dir("build/resourcesGenerated"))
            into(layout.projectDirectory.dir("out/production/resources"))
        }
    }

    // Modify the java compile task to actually apply our resource fix.
    compileJava {
        //finalizedBy("fixResources")
    }
}

minecraft {
    mappings(mappingsType, mappingsVersion)

    runs {
        all {

            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modID)

            jvmArgs("-ea", "-Xmx4G", "-Xms4G")

            if (useAdvancedClassRedef) {
                jvmArg("-XX:+AllowEnhancedClassRedefinition")
            }

            ideaModule("${modName}.test")

            mods.create(modID) {
                source(sourceSets.main.get())
            }
        }

        register("client") {
            workingDirectory("${buildDir}/run/client")
            args("-XX:+AllowEnhancedClassRedefinition")
        }

        register("server") {
            workingDirectory("run/server")

            arg("--nogui")
        }

        register("gameTestServer") {
            workingDirectory("run/gametest")

            arg("--nogui")
            forceExit(false)
        }
    }
}

idea {
    module {
        for (fileName in listOf("run", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}