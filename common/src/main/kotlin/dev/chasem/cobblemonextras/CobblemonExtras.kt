package dev.chasem.cobblemonextras

import com.mojang.brigadier.CommandDispatcher
import dev.chasem.cobblemonextras.commands.*
import dev.chasem.cobblemonextras.config.CobblemonExtrasConfig
import dev.chasem.cobblemonextras.events.CobblemonExtrasEventHandler
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import dev.chasem.cobblemonextras.services.ShowcaseService
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


object CobblemonExtras {
    public lateinit var permissions: CobblemonExtrasPermissions
    const val MODID = "cobblemonextras"
    lateinit var config: CobblemonExtrasConfig
    var LOGGER: Logger = LogManager.getLogger("[CobblemonExtras]")
    val showcaseService = ShowcaseService()
    val eventHandler = CobblemonExtrasEventHandler()

    fun initialize() {
        System.out.println("CobblemonExtras - Initialized")
        loadConfig() // must load before permissions so perms use default permission level.
        this.permissions = CobblemonExtrasPermissions()
        showcaseService.init()
    }

    fun onShutdown() {
        System.out.println("CobblemonExtras - Shutting Down")
        showcaseService.stop();
    }

    public fun getLogger(): Logger {
        return this.LOGGER;
    }

    fun loadConfig() {
        val configFileLoc = System.getProperty("user.dir") + File.separator + "config" + File.separator + "cobblemonextras" + File.separator + "config.json";
        System.out.println("Loading config file found at: $configFileLoc")
        val configFile: File = File(configFileLoc)
        configFile.parentFile.mkdirs()

        // Check config existence and load if it exists, otherwise create default.
        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                config = CobblemonExtrasConfig.GSON.fromJson(fileReader, CobblemonExtrasConfig::class.java)
                fileReader.close()
            } catch (e: Exception) {
                System.err.println("[CobblemonExtras] Failed to load the config! Using default config as fallback")
                e.printStackTrace()
                config = CobblemonExtrasConfig()
            }
        } else {
            config = CobblemonExtrasConfig()
        }
        saveConfig()
    }

    private fun saveConfig() {
        try {
            val configFileLoc = System.getProperty("user.dir") + File.separator + "config" + File.separator + "cobblemonextras" + File.separator + "config.json";
            System.out.println("Saving config to: $configFileLoc")
            val configFile: File = File(configFileLoc)
            val fileWriter = FileWriter(configFile)
            CobblemonExtrasConfig.GSON.toJson(config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: java.lang.Exception) {
            System.err.println("[CobblemonExtras] Failed to save config")
            e.printStackTrace()
        }
    }


    fun registerCommands(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registry: CommandRegistryAccess,
        selection: CommandManager.RegistrationEnvironment
    ) {
        println("CobblemonExtras Commands Registered")
        CompSee().register(dispatcher)
        PC().register(dispatcher)
        PokeSee().register(dispatcher)
        PokeShout().register(dispatcher)
        PokeTrade().register(dispatcher)

        Battle().register(dispatcher)
        PokeBattle().register(dispatcher)
        PCDelete().register(dispatcher)
        PokeIVs().register(dispatcher)

        PokeShoutAll().register(dispatcher)
        EmptyBox().register(dispatcher)
        Showcase().register(dispatcher)
        ItemShout().register(dispatcher)
        PokeOdds().register(dispatcher)
        PokeKill().register(dispatcher)
    }

}
