package dev.chasem.cobblemonextras.permissions

import com.cobblemon.mod.common.Cobblemon.permissionValidator
import com.cobblemon.mod.common.api.permission.CobblemonPermission
import com.cobblemon.mod.common.api.permission.PermissionLevel
import dev.chasem.cobblemonextras.CobblemonExtras
import net.minecraft.commands.CommandSourceStack

class CobblemonExtrasPermissions {
    val COMPSEE_PERMISSION: CobblemonPermission
    val COMPESEE_OTHER_PERMISSION: CobblemonPermission
    val PC_PERMISSION: CobblemonPermission
    val POKESEE_PERMISSION: CobblemonPermission
    val POKESEE_OTHER_PERMISSION: CobblemonPermission
    val POKESHOUT_PERMISSION: CobblemonPermission
    val POKETRADE_PERMISSION: CobblemonPermission
    val POKEBATTLE_PERMISSION: CobblemonPermission
    val BATTLE_PERMISSION: CobblemonPermission
    val COMPDELETE_PERMISSION: CobblemonPermission
    val POKEDELETE_PERMISSION: CobblemonPermission
    val POKEIVS_PERMISSION: CobblemonPermission
    val EMPTYBOX_PERMISSION: CobblemonPermission
    val POKESHOUT_ALL_PERMISSION: CobblemonPermission
    val ITEMSHOUT_PERMISSION: CobblemonPermission
    val POKEODDS_PERMISSION: CobblemonPermission
    val POKEKILL_PERMISSION: CobblemonPermission
    val GIVE_POKETOKEN_PERMISSION: CobblemonPermission
    val GIVE_SHINYBALL_PERMISSION: CobblemonPermission
    val BATTLE_SPECTATE_PERMISSION: CobblemonPermission

    init {
        this.COMPSEE_PERMISSION = CobblemonPermission("cobblemonextras.command.compsee", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPSEE_PERMISSION_LEVEL))
        this.COMPESEE_OTHER_PERMISSION = CobblemonPermission("cobblemonextras.command.compseeother", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPSEEOTHER_PERMISSION_LEVEL))
        this.PC_PERMISSION = CobblemonPermission("cobblemonextras.command.pc", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_PC_PERMISSION_LEVEL))
        this.POKESEE_PERMISSION = CobblemonPermission("cobblemonextras.command.pokesee", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESEE_PERMISSION_LEVEL))
        this.POKESEE_OTHER_PERMISSION = CobblemonPermission("cobblemonextras.command.pokeseeother", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESEEOTHER_PERMISSION_LEVEL))
        this.POKESHOUT_PERMISSION = CobblemonPermission("cobblemonextras.command.pokeshout", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESHOUT_PERMISSION_LEVEL))
        this.POKETRADE_PERMISSION = CobblemonPermission("cobblemonextras.command.poketrade", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKETRADE_PERMISSION_LEVEL))
        this.POKEBATTLE_PERMISSION = CobblemonPermission("cobblemonextras.command.pokebattle", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEBATTLE_PERMISSION_LEVEL))
        this.BATTLE_PERMISSION = CobblemonPermission("cobblemonextras.command.battle", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_BATTLE_PERMISSION_LEVEL))
        this.COMPDELETE_PERMISSION = CobblemonPermission("cobblemonextras.command.compdelete", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPDELETE_PERMISSION_LEVEL))
        this.POKEDELETE_PERMISSION = CobblemonPermission("cobblemonextras.command.pokedelete", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEDELETE_PERMISSION_LEVEL))
        this.POKEIVS_PERMISSION = CobblemonPermission("cobblemonextras.command.pokeivs", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEIVS_PERMISSION_LEVEL))
        this.EMPTYBOX_PERMISSION = CobblemonPermission("cobblemonextras.command.emptybox", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_EMPTYBOX_PERMISSION_LEVEL))
        this.POKESHOUT_ALL_PERMISSION = CobblemonPermission("cobblemonextras.command.pokeshoutall", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESHOUT_ALL_PERMISSION_LEVEL))
        this.ITEMSHOUT_PERMISSION = CobblemonPermission("cobblemonextras.command.itemshout", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_ITEMSHOUT_PERMISSION_LEVEL))
        this.POKEODDS_PERMISSION = CobblemonPermission("cobblemonextras.command.pokeodds", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEODDS_PERMISSION_LEVEL))
        this.POKEKILL_PERMISSION = CobblemonPermission("cobblemonextras.command.pokekill", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEKILL_PERMISSION_LEVEL))
        this.GIVE_POKETOKEN_PERMISSION = CobblemonPermission("cobblemonextras.command.givepoketoken", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_GIVE_POKETOKEN_PERMISSION))
        this.GIVE_SHINYBALL_PERMISSION = CobblemonPermission("cobblemonextras.command.giveshinyball", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_GIVE_SHINYBALL_PERMISSION))
        this.BATTLE_SPECTATE_PERMISSION = CobblemonPermission("cobblemonextras.command.battlespectate", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_BATTLESPECTATE_PERMISSION))
    }

    fun toPermLevel(permLevel: Int): PermissionLevel {
        for (value in PermissionLevel.entries) {
            if (value.ordinal == permLevel) {
                return value
            }
        }
        return PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
    }

    companion object {
        fun checkPermission(source: CommandSourceStack?, permission: CobblemonPermission?): Boolean {
            return permissionValidator.hasPermission(source!!, permission!!)
        }
    }
}
