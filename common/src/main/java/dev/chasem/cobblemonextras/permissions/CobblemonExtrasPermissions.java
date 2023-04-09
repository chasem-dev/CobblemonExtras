package dev.chasem.cobblemonextras.permissions;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import dev.chasem.cobblemonextras.config.CobblemonExtrasConfig;
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

    public CobblemonExtrasPermissions() {
        this.COMPSEE_PERMISSION = new CobblemonPermission("cobblemonextras.command.compsee", toPermLevel(CobblemonExtrasConfig.COMMAND_COMPSEE_PERMISSION_LEVEL));
        this.COMPESEE_OTHER_PERMISSION = new CobblemonPermission("cobblemonextras.command.compseeother", toPermLevel(CobblemonExtrasConfig.COMMAND_COMPSEEOTHER_PERMISSION_LEVEL));
        this.PC_PERMISSION = new CobblemonPermission("cobblemonextras.command.pc", toPermLevel(CobblemonExtrasConfig.COMMAND_PC_PERMISSION_LEVEL));
        this.POKESEE_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokesee", toPermLevel(CobblemonExtrasConfig.COMMAND_POKESEE_PERMISSION_LEVEL));
        this.POKESEE_OTHER_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeseeother", toPermLevel(CobblemonExtrasConfig.COMMAND_POKESEEOTHER_PERMISSION_LEVEL));
        this.POKESHOUT_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeshout", toPermLevel(CobblemonExtrasConfig.COMMAND_POKESHOUT_PERMISSION_LEVEL));
        this.POKETRADE_PERMISSION = new CobblemonPermission("cobblemonextras.command.poketrade", toPermLevel(CobblemonExtrasConfig.COMMAND_POKETRADE_PERMISSION_LEVEL));
        this.POKEBATTLE_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokebattle", toPermLevel(CobblemonExtrasConfig.COMMAND_POKEBATTLE_PERMISSION_LEVEL));
        this.BATTLE_PERMISSION = new CobblemonPermission("cobblemonextras.command.battle", toPermLevel(CobblemonExtrasConfig.COMMAND_BATTLE_PERMISSION_LEVEL));
        this.COMPTAKE_PERMISSION = new CobblemonPermission("cobblemonextras.command.comptake", toPermLevel(CobblemonExtrasConfig.COMMAND_COMPTAKE_PERMISSION_LEVEL));
        this.POKEIVS_PERMISSION = new CobblemonPermission("cobblemonextras.command.pokeivs", toPermLevel(CobblemonExtrasConfig.COMMAND_POKEIVS_PERMISSION_LEVEL));
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
