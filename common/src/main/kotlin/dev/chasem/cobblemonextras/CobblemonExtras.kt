package dev.chasem.cobblemonextras


import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.brigadier.CommandDispatcher
import dev.chasem.cobblemonextras.commands.*
import dev.chasem.cobblemonextras.config.CobblemonExtrasConfig
import dev.chasem.cobblemonextras.events.CobblemonExtrasEventHandler
import dev.chasem.cobblemonextras.events.PokeTokensInteractionHandler
import dev.chasem.cobblemonextras.permissions.CobblemonExtrasPermissions
import dev.chasem.cobblemonextras.services.ShowcaseService
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.entity.EntityEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter


object CobblemonExtras {
    public lateinit var permissions: CobblemonExtrasPermissions
    const val MODID = "cobblemonextras"
    lateinit var config: CobblemonExtrasConfig
    var LOGGER: Logger = LogManager.getLogger("[CobblemonExtras]")
    lateinit var showcaseService: ShowcaseService
    val eventHandler = CobblemonExtrasEventHandler()

    fun initialize() {
        getLogger().info("CobblemonExtras - Initialized")
        loadConfig() // must load before permissions so perms use default permission level.

        getLogger().info("CobblemonExtras - Custom Model Data")
        getLogger().info("IV Token: ${config.customModels.IV_TOKEN}")
        getLogger().info("EV Token: ${config.customModels.EV_TOKEN}")
        getLogger().info("Nature Token: ${config.customModels.NATURE_TOKEN}")
        getLogger().info("Shiny Token: ${config.customModels.SHINY_TOKEN}")
        getLogger().info("Shiny Ball: ${config.customModels.SHINY_BALL}")


        this.permissions = CobblemonExtrasPermissions()
        this.showcaseService = ShowcaseService
        PokeTokensInteractionHandler()
        CobblemonEvents.POKEMON_CAPTURED.subscribe { event -> eventHandler.onPokemonCapture(event) }
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
//                var loadedConfig = CobblemonExtrasConfig.GSON.fromJson(fileReader, CobblemonExtrasConfig::class.java)


                // Create a default config instance
                val defaultConfig = CobblemonExtrasConfig()
                val defaultConfigJson: String = CobblemonExtrasConfig.GSON.toJson(defaultConfig)


                val fileConfigElement: JsonElement = JsonParser.parseReader(fileReader)


                // Convert default config JSON string to JsonElement
                val defaultConfigElement: JsonElement = JsonParser.parseString(defaultConfigJson)


                // Merge default config with the file config
                val mergedConfigElement: JsonElement = mergeConfigs(defaultConfigElement.getAsJsonObject(), fileConfigElement.getAsJsonObject())


                // Deserialize the merged JsonElement back to CobblemonExtrasConfig
                val finalConfig: CobblemonExtrasConfig = CobblemonExtrasConfig.GSON.fromJson(mergedConfigElement, CobblemonExtrasConfig::class.java)

                this.config = finalConfig;

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

    private fun mergeConfigs(defaultConfig: JsonObject, fileConfig: JsonObject): JsonElement {
        // For every entry in the default config, check if it exists in the file config
        getLogger().info("Checking for config merge.");
        var merged = false;
        for (key in defaultConfig.keySet()) {
            if (!fileConfig.has(key)) {
                // If the file config does not have the key, add it from the default config
                fileConfig.add(key, defaultConfig.get(key))
                getLogger().info("[CobblemonExtras] $key not found in file config, adding from default.");
                merged = true;
            } else {
                // If it's a nested object, recursively merge it
                if (defaultConfig.get(key).isJsonObject() && fileConfig.get(key).isJsonObject()) {
                    mergeConfigs(defaultConfig.getAsJsonObject(key), fileConfig.getAsJsonObject(key))
                }
            }
        }
        if (merged) {
            getLogger().info("[CobblemonExtras] Successfully merged config.");
        }
        return fileConfig
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

    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        getLogger().info("CobblemonExtras registerCommands")
        CompSee().register(dispatcher)
        PokeSee().register(dispatcher)
        PokeShout().register(dispatcher)
        PokeTrade().register(dispatcher)
        Battle().register(dispatcher)
        PokeBattle().register(dispatcher)
        CompDelete().register(dispatcher)
        PokeIVs().register(dispatcher)

        PokeShoutAll().register(dispatcher)
        EmptyBox().register(dispatcher)

        if (config.showcase.isShowcaseEnabled) {
            Showcase().register(dispatcher)
        }
        ItemShout().register(dispatcher)
        PokeOdds().register(dispatcher)
        PokeKill().register(dispatcher)
        PokeDelete().register(dispatcher)
        GivePokeToken().register(dispatcher)
        BattleSpectate().register(dispatcher)
        GiveShinyBall().register(dispatcher)
        PlayerGames().register(dispatcher)
    }

}
