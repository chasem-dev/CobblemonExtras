plugins {
    id("cobblemon.base-conventions")
    id("cobblemon.publish-conventions")
}

architectury {
    common()
}

repositories {
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
    implementation ("org.apache.httpcomponents:httpclient:4.5.13")

    modImplementation(libs.fabricLoader)
    modApi ("curse.maven:cobblemon-687131:4797468")
    // org.apache.http

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'


    compileOnly("net.luckperms:api:${rootProject.property("luckperms_version")}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
