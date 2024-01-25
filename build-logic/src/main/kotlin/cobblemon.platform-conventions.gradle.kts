plugins {
    id("cobblemon.base-conventions")
    id("com.github.johnrengelman.shadow")
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks {

    jar {
        archiveBaseName.set("CobblemonExtras-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("CobblemonExtras-${project.name}")
        configurations = listOf(bundle)
        mergeServiceFiles()
        // include org.apache.httpcomponents:httpclient 4.4.1 in jar
//        dependencies {
//            include("org.apache.httpcomponents:httpclient:4.4.1")
//        }

    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("CobblemonExtras-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }

}