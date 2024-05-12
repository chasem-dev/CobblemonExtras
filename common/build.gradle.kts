plugins {
    id("cobblemon.base-conventions")
    id("cobblemon.publish-conventions")
}

architectury {
    common()
}

repositories {
    maven { url = uri("https://maven.impactdev.net/repository/development/") }
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    mavenLocal()
}

dependencies {
    implementation(libs.stdlib)
    implementation(libs.reflect)
    implementation(libs.httpclient)
//    implementation(libs.shadow)
//    implementation ("org.apache.httpcomponents:httpclient:4.5.13")

    modImplementation(libs.fabricLoader)
    // Fabric version
    // https://www.curseforge.com/minecraft/mc-mods/cobblemon/files/4977486
    modApi ("com.cobblemon:fabric:${rootProject.property("cobblemon_file")}")

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'


    compileOnly("net.luckperms:api:${rootProject.property("luckperms_version")}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
