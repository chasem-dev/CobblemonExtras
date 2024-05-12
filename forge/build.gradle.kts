plugins {
    id("cobblemon.platform-conventions")
    id("cobblemon.publish-conventions")
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    forge {
        mixinConfig("mixins.cobblemonextras-forge.json")
        mixinConfig("mixins.cobblemonextras-common.json")
    }
}

repositories {
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/")
    mavenLocal()
    maven { url = uri("https://maven.impactdev.net/repository/development/") }
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    forge(libs.forge)
//    modApi(libs.architecturyForge)
//    modApi(libs.kotlinForForge)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    implementation(libs.kotlinForForge)
    "developmentForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    bundle(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
        exclude(group = "thedarkcolour")
    }
    testImplementation(project(":common", configuration = "namedElements"))

    // Forge version
    // https://www.curseforge.com/minecraft/mc-mods/cobblemon/files/4797451
    modImplementation ("com.cobblemon:fabric:${rootProject.property("cobblemon_file")}") {
        exclude(group = "net.minecraftforge")
    }


    // bundle libs.httpclient and remap it to the mod's namespace
    bundle("org.apache.httpcomponents:httpclient:4.5.13") {
//        isTransitive = false
    }

}

tasks {
    shadowJar {
        exclude("architectury-common.accessWidener")
        relocate ("com.ibm.icu", "com.cobblemon.mod.relocations.ibm.icu")
        relocate("org.apache.http", "dev.chasem.apache.http")
        relocate("org.apache.commons", "dev.chasem.apache.commons")
    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to rootProject.version)
        }
    }
}

//jar {
//    classifier("dev")
//    manifest {
//        attributes(
//                "Specification-Title" to rootProject.mod_id,
//                "Specification-Vendor" to "Cable MC",
//                "Specification-Version" to "1",
//                "Implementation-Title" to rootProject.mod_id,
//                "Implementation-Version" to project.version,
//                "Implementation-Vendor" to "Cable MC",
//        )
//    }
//}
//
//sourcesJar {
//    def commonSources = project(":common").sourcesJar
//    dependsOn commonSources
//    from commonSources.archiveFile.map { zipTree(it) }
//}
//
//components.java {
//    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements) {
//        skip()
//    }
//}