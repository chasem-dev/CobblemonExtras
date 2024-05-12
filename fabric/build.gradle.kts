plugins {
    id("cobblemon.platform-conventions")
    id("cobblemon.publish-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val generatedResources = file("src/generated/resources")

sourceSets {
    main {
        resources {
            srcDir(generatedResources)
        }
    }
}

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    maven { url = uri("https://maven.impactdev.net/repository/development/") }

    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    "developmentFabric"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    bundle(project(path = ":common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    // Fabric version
    // https://www.curseforge.com/minecraft/mc-mods/cobblemon/files/4977486
    modImplementation ("com.cobblemon:fabric:${rootProject.property("cobblemon_file")}") {
        isTransitive = false;
    }

    modApi(libs.fabricLoader)
    modApi(libs.fabricApi)
    modApi(libs.fabricKotlin)
    modApi(libs.fabricPermissionsApi)
    listOf(
        libs.stdlib,
        libs.reflect,
        libs.jetbrainsAnnotations,
        libs.serializationCore,
        libs.serializationJson,
        libs.httpclient
    ).forEach {
        bundle(it)
        runtimeOnly(it)
    }
}

tasks {
    // The AW file is needed in :fabric project resources when the game is run.
    val copyAccessWidener by registering(Copy::class) {
        from(loom.accessWidenerPath)
        into(generatedResources)
    }

    shadowJar {}

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("fabric.mod.json") {
            expand("version" to rootProject.version)
        }
    }
}