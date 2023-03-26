plugins {
    base
    id("cobblemon.root-conventions")
}

group = "dev.chasem.cobblemonextras"
version = "${project.property("mod_version")}+${project.property("mc_version")}"

val isSnapshot = project.property("snapshot")?.equals("true") ?: false
if (isSnapshot) {
    version = "$version-SNAPSHOT"
}