package dev.chasem.cobblemonextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


public class CobblemonExtrasConfig {

    public static Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    @SerializedName("permissionlevels") public PermissionLevels permissionLevels = new PermissionLevels();
    public ShowcaseConfig showcase = new ShowcaseConfig();
    public class PermissionLevels {
        @SerializedName("command.poketrade") public int COMMAND_POKETRADE_PERMISSION_LEVEL = 2;
        @SerializedName("command.pokesee") public int COMMAND_POKESEE_PERMISSION_LEVEL = 2;
        @SerializedName("command.pokeseeother") public int COMMAND_POKESEEOTHER_PERMISSION_LEVEL = 2;
        @SerializedName("command.pc") public int COMMAND_PC_PERMISSION_LEVEL = 2;
        @SerializedName("command.pokeshout") public int COMMAND_POKESHOUT_PERMISSION_LEVEL = 2;
        @SerializedName("command.itemshout") public int COMMAND_ITEMSHOUT_PERMISSION_LEVEL = 2;
        @SerializedName("command.compsee") public int COMMAND_COMPSEE_PERMISSION_LEVEL = 2;
        @SerializedName("command.compseeother") public int COMMAND_COMPSEEOTHER_PERMISSION_LEVEL = 2;
        @SerializedName("command.pokebattle") public int COMMAND_POKEBATTLE_PERMISSION_LEVEL = 3;
        @SerializedName("command.battle") public int COMMAND_BATTLE_PERMISSION_LEVEL = 2;
        @SerializedName("command.comptake") public int COMMAND_COMPTAKE_PERMISSION_LEVEL = 2;
        @SerializedName("command.pokeivs") public int COMMAND_POKEIVS_PERMISSION_LEVEL = 2;
        @SerializedName("command.emptybox") public int COMMAND_EMPTYBOX_PERMISSION_LEVEL = 2;
        @SerializedName("command.pokeshoutall") public int COMMAND_POKESHOUT_ALL_PERMISSION_LEVEL = 2;
        @SerializedName("command.pokeodds") public int COMMAND_POKEODDS_PERMISSION_LEVEL = 3;
        @SerializedName("command.pokekill") public int COMMAND_POKEKILL_PERMISSION_LEVEL = 3;
    }

    public class ShowcaseConfig {
        public boolean isShowcaseEnabled = true;
        public String apiSecret = "To start using showcase, please goto https://cobblemonextras.com/showcase";

        public int syncIntervalMinutes = 5;
        public boolean debug = false;
    }
}
