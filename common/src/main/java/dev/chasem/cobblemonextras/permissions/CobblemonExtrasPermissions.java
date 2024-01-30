package dev.chasem.cobblemonextras.permissions;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.command.CommandSource;

public class CobblemonExtrasPermissions {

    public final CobblemonPermission COMPSEE_PERMISSION;
    public final CobblemonPermission COMPESEE_OTHER_PERMISSION;
    public final CobblemonPermission PC_PERMISSION;
    public final CobblemonPermission POKESEE_PERMISSION;
    public final CobblemonPermission POKESEE_OTHER_PERMISSION;
    public final CobblemonPermission POKESHOUT_PERMISSION;
    public final CobblemonPermission POKETRADE_PERMISSION;
    public final CobblemonPermission POKEBATTLE_PERMISSION;
    public final CobblemonPermission BATTLE_PERMISSION;
    public final CobblemonPermission COMPTAKE_PERMISSION;
    public final CobblemonPermission POKEIVS_PERMISSION;
    public final CobblemonPermission EMPTYBOX_PERMISSION;
    public final CobblemonPermission POKESHOUT_ALL_PERMISSION;
    public final CobblemonPermission ITEMSHOUT_PERMISSION;
    public final CobblemonPermission POKEODDS_PERMISSION;
    public final CobblemonPermission POKEKILL_PERMISSION;

    public CobblemonExtrasPermissions() {
        this.COMPSEE_PERMISSION = new CobblemonPermission("cobblemonextras.command.compsee", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPSEE_PERMISSION_LEVEL));
        this.COMPESEE_OTHER_PERMISSION = new CobblemonPermission("cobblemonextras.command.compseeother", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPSEEOTHER_PERMISSION_LEVEL));
        this.PC_PERMISSION = new CobblemonPermission("cobblemonextras.command.pc", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_PC_PERMISSION_LEVEL));
        this.POKESEE_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokesee", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESEE_PERMISSION_LEVEL));
        this.POKESEE_OTHER_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeseeother", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESEEOTHER_PERMISSION_LEVEL));
        this.POKESHOUT_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeshout", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESHOUT_PERMISSION_LEVEL));
        this.POKETRADE_PERMISSION = new CobblemonPermission("cobblemonextras.command.poketrade", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKETRADE_PERMISSION_LEVEL));
        this.POKEBATTLE_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokebattle", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEBATTLE_PERMISSION_LEVEL));
        this.BATTLE_PERMISSION = new CobblemonPermission("cobblemonextras.command.battle", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_BATTLE_PERMISSION_LEVEL));
        this.COMPTAKE_PERMISSION = new CobblemonPermission("cobblemonextras.command.comptake", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPTAKE_PERMISSION_LEVEL));
        this.POKEIVS_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeivs", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEIVS_PERMISSION_LEVEL));
        this.EMPTYBOX_PERMISSION = new CobblemonPermission("cobblemonextras.command.emptybox", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_EMPTYBOX_PERMISSION_LEVEL));
        this.POKESHOUT_ALL_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeshoutall", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESHOUT_ALL_PERMISSION_LEVEL));
        this.ITEMSHOUT_PERMISSION = new CobblemonPermission("cobblemonextras.command.itemshout", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_ITEMSHOUT_PERMISSION_LEVEL));
        this.POKEODDS_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeodds", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEODDS_PERMISSION_LEVEL));
        this.POKEKILL_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokekill", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEKILL_PERMISSION_LEVEL));
    }

    public PermissionLevel toPermLevel(int permLevel) {
        for (PermissionLevel value : PermissionLevel.values()) {
            if (value.ordinal() == permLevel) {
                return value;
            }
        }
        return PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS;
    }

    public static boolean checkPermission(CommandSource source, CobblemonPermission permission) {
        return Cobblemon.INSTANCE.getPermissionValidator().hasPermission(source, permission);
    }
}
