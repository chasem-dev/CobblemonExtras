package dev.chasem.cobblemonextras.permissions

import com.cobblemon.mod.common.Cobblemon.permissionValidator
import com.cobblemon.mod.common.api.permission.PermissionLevel
import dev.chasem.cobblemonextras.CobblemonExtras
import net.minecraft.commands.CommandSourceStack

class CobblemonExtrasPermissions {
    val COMPSEE_PERMISSION: CobblemonExtrasPermission
    val COMPESEE_OTHER_PERMISSION: CobblemonExtrasPermission
    val PC_PERMISSION: CobblemonExtrasPermission
    val POKESEE_PERMISSION: CobblemonExtrasPermission
    val POKESEE_OTHER_PERMISSION: CobblemonExtrasPermission
    val POKESHOUT_PERMISSION: CobblemonExtrasPermission
    val POKETRADE_PERMISSION: CobblemonExtrasPermission
    val POKEBATTLE_PERMISSION: CobblemonExtrasPermission
    val BATTLE_PERMISSION: CobblemonExtrasPermission
    val COMPDELETE_PERMISSION: CobblemonExtrasPermission
    val POKEDELETE_PERMISSION: CobblemonExtrasPermission
    val POKEIVS_PERMISSION: CobblemonExtrasPermission
    val EMPTYBOX_PERMISSION: CobblemonExtrasPermission
    val POKESHOUT_ALL_PERMISSION: CobblemonExtrasPermission
    val ITEMSHOUT_PERMISSION: CobblemonExtrasPermission
    val POKEODDS_PERMISSION: CobblemonExtrasPermission
    val POKEKILL_PERMISSION: CobblemonExtrasPermission
    val GIVE_POKETOKEN_PERMISSION: CobblemonExtrasPermission
    val GIVE_SHINYBALL_PERMISSION: CobblemonExtrasPermission
    val BATTLE_SPECTATE_PERMISSION: CobblemonExtrasPermission

    init {
        this.COMPSEE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.compsee", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPSEE_PERMISSION_LEVEL))
        this.COMPESEE_OTHER_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.compseeother", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPSEEOTHER_PERMISSION_LEVEL))
        this.PC_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pc", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_PC_PERMISSION_LEVEL))
        this.POKESEE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokesee", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESEE_PERMISSION_LEVEL))
        this.POKESEE_OTHER_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokeseeother", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESEEOTHER_PERMISSION_LEVEL))
        this.POKESHOUT_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokeshout", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESHOUT_PERMISSION_LEVEL))
        this.POKETRADE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.poketrade", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKETRADE_PERMISSION_LEVEL))
        this.POKEBATTLE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokebattle", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEBATTLE_PERMISSION_LEVEL))
        this.BATTLE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.battle", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_BATTLE_PERMISSION_LEVEL))
        this.COMPDELETE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.compdelete", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_COMPDELETE_PERMISSION_LEVEL))
        this.POKEDELETE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokedelete", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEDELETE_PERMISSION_LEVEL))
        this.POKEIVS_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokeivs", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEIVS_PERMISSION_LEVEL))
        this.EMPTYBOX_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.emptybox", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_EMPTYBOX_PERMISSION_LEVEL))
        this.POKESHOUT_ALL_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokeshoutall", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKESHOUT_ALL_PERMISSION_LEVEL))
        this.ITEMSHOUT_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.itemshout", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_ITEMSHOUT_PERMISSION_LEVEL))
        this.POKEODDS_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokeodds", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEODDS_PERMISSION_LEVEL))
        this.POKEKILL_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.pokekill", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_POKEKILL_PERMISSION_LEVEL))
        this.GIVE_POKETOKEN_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.givepoketoken", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_GIVE_POKETOKEN_PERMISSION))
        this.GIVE_SHINYBALL_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.giveshinyball", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_GIVE_SHINYBALL_PERMISSION))
        this.BATTLE_SPECTATE_PERMISSION = CobblemonExtrasPermission("cobblemonextras.command.battlespectate", toPermLevel(CobblemonExtras.config.permissionLevels.COMMAND_BATTLESPECTATE_PERMISSION))
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
        fun checkPermission(source: CommandSourceStack?, permission: CobblemonExtrasPermission?): Boolean {
            return permissionValidator.hasPermission(source!!, permission!!)
        }
    }
}
