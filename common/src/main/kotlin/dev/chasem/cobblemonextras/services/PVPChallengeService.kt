
package dev.chasem.cobblemonextras.services

import com.cobblemon.mod.common.api.interaction.RequestManager
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.text.aqua
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.battles.ChallengeManager
import com.cobblemon.mod.common.net.messages.client.battle.BattleChallengeExpiredPacket
import com.cobblemon.mod.common.net.messages.client.battle.BattleChallengeNotificationPacket
import com.cobblemon.mod.common.util.party
import net.minecraft.server.level.ServerPlayer
import java.util.*

/**
 * Clone of the ChallengeManager with an override for the isValidInteraction method.
 */

object PVPChallengeService : RequestManager<ChallengeManager.BattleChallenge>() {

    init {
        register(this)
    }
    /** Mapping of players to their lead Pokemon. */
    val selectedLead = mutableMapOf<UUID, UUID>()

    override fun expirationPacket(request: ChallengeManager.BattleChallenge): NetworkPacket<*> = BattleChallengeExpiredPacket(request)

    override fun notificationPacket(request: ChallengeManager.BattleChallenge): NetworkPacket<*> = BattleChallengeNotificationPacket(request)

    override fun onDecline(request: ChallengeManager.BattleChallenge) {
        request.notifySender(true, "decline.sender", request.receiver.name.copy().aqua(), request.battleFormat.battleType.displayName)
        request.notifyReceiver(false, "decline.receiver", request.sender.name.copy().aqua(), request.battleFormat.battleType.displayName)
    }

    override fun onSend(request: ChallengeManager.BattleChallenge) {
        request.notifySender(false, "sent", request.receiver.name.copy().aqua(), request.battleFormat.battleType.displayName)
        request.notifyReceiver(false, "received", request.sender.name.copy().aqua(), request.battleFormat.battleType.displayName)
    }

    override fun onAccept(request: ChallengeManager.BattleChallenge) {
        BattleBuilder.pvp1v1(
                request.receiver,
                request.sender,
                selectedLead.get(request.receiver.uuid),
                selectedLead.get(request.sender.uuid),
                request.battleFormat
        ).ifErrored {
            it.sendTo(request.receiver) { it.red() }
            it.sendTo(request.sender) { it.red() }
        }
    }

    override fun isValidInteraction(player: ServerPlayer, target: ServerPlayer) = true;

    override fun canAccept(request: ChallengeManager.BattleChallenge): Boolean {
        // validate parties
        if (request.receiver.party().none()) {
            request.notifySender(true, "error.insufficient_pokemon.other", request.receiver.name.copy().aqua())
            request.notifyReceiver(true, "error.insufficient_pokemon.self")
        }
        else if (request.sender.party().none()) {
            request.notifySender(true, "error.insufficient_pokemon.self")
            request.notifyReceiver(true, "error.insufficient_pokemon.other", request.sender.name.copy().aqua())
        }
        // TODO worth validating size and party health here? BattleBuilder already does it
        else return true
        return false
    }
}