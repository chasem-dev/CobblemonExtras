package dev.chasem.cobblemonextras.permissions

import com.cobblemon.mod.common.api.permission.Permission
import com.cobblemon.mod.common.api.permission.PermissionLevel
import dev.chasem.cobblemonextras.CobblemonExtras
import net.minecraft.resources.ResourceLocation

data class CobblemonExtrasPermission(
    private val node: String,
    override val level: PermissionLevel
) : Permission {

    override val identifier = ResourceLocation.fromNamespaceAndPath(CobblemonExtras.MODID, this.node)

    override val literal = "${CobblemonExtras.MODID}.${this.node}"
}
