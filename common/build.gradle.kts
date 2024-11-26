plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}


architectury {
    common("neoforge", "fabric")
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    // The following line declares the mojmap mappings, you may use other mappings as well
    mappings(loom.officialMojangMappings())
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    modImplementation("org.apache.httpcomponents:httpclient:4.5.13")
    modImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }
}
