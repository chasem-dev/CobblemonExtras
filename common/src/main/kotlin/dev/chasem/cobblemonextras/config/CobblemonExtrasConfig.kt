package dev.chasem.cobblemonextras.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

class CobblemonExtrasConfig {
    @SerializedName("permissionlevels")
    var permissionLevels: PermissionLevels = PermissionLevels()
    var showcase: ShowcaseConfig = ShowcaseConfig()
    @SerializedName("customModels")
    var customModels: CustomModelsConfig = CustomModelsConfig()


    inner class PermissionLevels {
        @SerializedName("command.poketrade")
        var COMMAND_POKETRADE_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokesee")
        var COMMAND_POKESEE_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokeseeother")
        var COMMAND_POKESEEOTHER_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pc")
        var COMMAND_PC_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokeshout")
        var COMMAND_POKESHOUT_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.itemshout")
        var COMMAND_ITEMSHOUT_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.compsee")
        var COMMAND_COMPSEE_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.compseeother")
        var COMMAND_COMPSEEOTHER_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokebattle")
        var COMMAND_POKEBATTLE_PERMISSION_LEVEL: Int = 3

        @SerializedName("command.battle")
        var COMMAND_BATTLE_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.compdelete")
        var COMMAND_COMPDELETE_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokedelete")
        var COMMAND_POKEDELETE_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokeivs")
        var COMMAND_POKEIVS_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.emptybox")
        var COMMAND_EMPTYBOX_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokeshoutall")
        var COMMAND_POKESHOUT_ALL_PERMISSION_LEVEL: Int = 2

        @SerializedName("command.pokeodds")
        var COMMAND_POKEODDS_PERMISSION_LEVEL: Int = 3

        @SerializedName("command.pokekill")
        var COMMAND_POKEKILL_PERMISSION_LEVEL: Int = 3

        @SerializedName("command.givepoketoken")
        var COMMAND_GIVE_POKETOKEN_PERMISSION: Int = 3

        @SerializedName("command.giveshinyball")
        var COMMAND_GIVE_SHINYBALL_PERMISSION: Int = 3

        @SerializedName("command.battlespectate")
        var COMMAND_BATTLESPECTATE_PERMISSION: Int = 2
    }

    inner class ShowcaseConfig {
        var isShowcaseEnabled: Boolean = true
        var apiSecret: String = "To start using showcase, please goto https://cobblemonextras.com/showcase"

        var syncIntervalMinutes: Int = 5
        var debug: Boolean = false
        var async: Boolean = true
    }

    inner class CustomModelsConfig {
        var SHINY_TOKEN = 100;
        var NATURE_TOKEN = 101;
        var IV_TOKEN = 102;
        var EV_TOKEN = 103;

        // PokeBall Item
        var SHINY_BALL = 100;
    }

    companion object {
        var GSON: Gson = GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
    }
}
