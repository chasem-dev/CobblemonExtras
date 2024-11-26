package dev.chasem.cobblemonextras.services

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore
import com.cobblemon.mod.common.api.storage.pc.PCStore
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.lang
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import dev.chasem.cobblemonextras.CobblemonExtras
import kotlinx.coroutines.*
import net.minecraft.ChatFormatting
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerPlayer
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.LaxRedirectStrategy
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.DeflaterOutputStream
import kotlin.math.max

object ShowcaseService {

    private var hasFailed: Boolean = false

    // var API_BASE_URL: String = "http://localhost:3000/api";
    var API_BASE_URL: String = "https://cobblemonextras.com/api"
    private val minutes = max(CobblemonExtras.config.showcase.syncIntervalMinutes.toDouble(), 1.0).toFloat();
    private val delayMs = (minutes * 60 * 1000).toLong()

    init {
        CobblemonExtras.getLogger().info("ShowcaseService - Initializing...")
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            if (clientSecret == null) {
                CobblemonExtras.getLogger().info("Failed to enable Showcase")
                CobblemonExtras.getLogger().error("Invalid API Secret, please goto https://cobblemonextras.com/showcase to get your API Secret")
            } else {
                CobblemonExtras.getLogger().info("[CobblemonExtras] Showcase will sync every " + CobblemonExtras.config.showcase.syncIntervalMinutes + " minutes.")
                CoroutineScope(Dispatchers.Default).launch(CoroutineName("CobblemonExtrasShowcaseService")) {
                    // Set the coroutines name
                    Thread.currentThread().name = "ShowcaseService"
                    doRepeatingTask()
                }

                CobblemonExtras.getLogger().info("Showcase Enabled, Syncing every $delayMs ms")
            }
        } else {
            CobblemonExtras.getLogger().info("ShowcaseService - Disabled")

        }
    }

    fun stop() {
        CobblemonExtras.getLogger().info("Showcase Task Stopped")
    }


    private suspend fun doRepeatingTask() {
        while (true) {
            if (CobblemonExtras.config.showcase.debug) {
                CobblemonExtras.getLogger().info("Running showcase sync task...")
            }
            val server = Cobblemon.implementation.server()

            if (server == null) {
                delay(10000)
                CobblemonExtras.getLogger().error("Unable to get server instance, retrying in $minutes minutes")
                continue
            }

            try {
                // Sync all players
                syncPlayers(server.playerList.players)
            } catch (e: Exception) {
                e.printStackTrace()
                CobblemonExtras.getLogger().error("Error when syncing playerData.")
            }

            delay(delayMs)
        }
    }


    fun getPokemonJson(pokemon: Pokemon): JsonObject? {
        val pokemonJson = pokemon.saveToJSON(RegistryAccess.EMPTY, JsonObject())
        try {
            pokemonJson.addProperty("Nature", lang(pokemon.nature.displayName.replace("cobblemon.", "")).string)
            pokemonJson.addProperty("DexNumber", pokemon.species.nationalPokedexNumber)
            if (pokemon.nickname != null) {
                pokemonJson.addProperty("Nickname", pokemon.nickname!!.string)
            } else {
                pokemonJson.add("Nickname", JsonNull.INSTANCE)
            }
            pokemonJson.addProperty("Name", pokemon.species.translatedName.string)

            // Ability
            val abilityJson = pokemonJson.getAsJsonObject("Ability")
            abilityJson.addProperty("AbilityName", lang(pokemon.ability.displayName.replace("cobblemon.", "")).string)
            pokemonJson.add("Ability", abilityJson)

            // Form
            pokemonJson.addProperty("FormName", pokemon.form.name)

            pokemonJson.addProperty("Type1", pokemon.primaryType.displayName.string)
            if (pokemon.secondaryType != null) {
                pokemonJson.addProperty("Type2", pokemon.secondaryType!!.displayName.string)
            } else {
                pokemonJson.add("Type2", JsonNull.INSTANCE)
            }

            // Movesets
            val moveJsonArray = pokemonJson.getAsJsonArray("MoveSet")

            // Used for payload request to showcase API
            val moveJson = JsonObject()

            val moveSet0 = if (moveJsonArray.size() >= 1) moveJsonArray.get(0) else JsonNull.INSTANCE
            val moveSet1 = if (moveJsonArray.size() >= 2) moveJsonArray.get(1) else JsonNull.INSTANCE
            val moveSet2 = if (moveJsonArray.size() >= 3) moveJsonArray.get(2) else JsonNull.INSTANCE
            val moveSet3 = if (moveJsonArray.size() >= 4) moveJsonArray.get(3) else JsonNull.INSTANCE
            if (moveSet0 != null && !moveSet0.isJsonNull) {
                moveSet0.asJsonObject.addProperty("MoveName", pokemon.moveSet[0]!!.displayName.string)
                moveSet0.asJsonObject.addProperty("MoveType", pokemon.moveSet[0]!!.type.name)
                moveJson.add("MoveSet0", moveSet0)
            }
            if (moveSet1 != null && !moveSet1.isJsonNull) {
                moveSet1.asJsonObject.addProperty("MoveName", pokemon.moveSet[1]!!.displayName.string)
                moveSet1.asJsonObject.addProperty("MoveType", pokemon.moveSet[1]!!.type.name)
                moveJson.add("MoveSet1", moveSet1)
            }
            if (moveSet2 != null && !moveSet2.isJsonNull) {
                moveSet2.asJsonObject.addProperty("MoveName", pokemon.moveSet[2]!!.displayName.string)
                moveSet2.asJsonObject.addProperty("MoveType", pokemon.moveSet[2]!!.type.name)
                moveJson.add("MoveSet2", moveSet2)
            }
            if (moveSet3 != null && !moveSet3.isJsonNull) {
                moveSet3.asJsonObject.addProperty("MoveName", pokemon.moveSet[3]!!.displayName.string)
                moveSet3.asJsonObject.addProperty("MoveType", pokemon.moveSet[3]!!.type.name)
                moveJson.add("MoveSet3", moveSet3)
            }
            pokemonJson.add("MoveSet", moveJson)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return pokemonJson
    }

    fun getPlayerJson(player: ServerPlayer): JsonObject? {
        // Obtain cobblemon pc data
        var party: PlayerPartyStore? = null
        var pc: PCStore? = null
        try {
            party = Cobblemon.storage.getParty(player)
            pc = Cobblemon.storage.getPC(player)
        } catch (exc: Exception) {
            return null
        }
        val playerData = JsonObject()
        playerData.addProperty("uuid", player.uuid.toString())
        playerData.addProperty("name", player.displayName!!.string)

        val partyJson = JsonArray()
        for (i in 0..5) {
            val pokemon = party.get(i)
            if (pokemon == null) {
                partyJson.add(JsonNull.INSTANCE)
            } else {
                val pokemonJson = getPokemonJson(pokemon)
                partyJson.add(Objects.requireNonNullElse(pokemonJson, JsonNull.INSTANCE))
            }
        }

        var pcJson: JsonElement = JsonArray()
        if (pc != null) {
            for (box in pc.boxes) {
                val boxJson = JsonArray()
                for (slot in 0..29) {
                    val pokemon = box[slot]
                    if (pokemon == null) {
                        boxJson.add(JsonNull.INSTANCE)
                    } else {
                        val pokemonJson = getPokemonJson(pokemon)
                        boxJson.add(Objects.requireNonNullElse(pokemonJson, JsonNull.INSTANCE))
                    }
                }
                (pcJson as JsonArray).add(boxJson)
            }
        } else {
            pcJson = JsonNull.INSTANCE
        }
        playerData.add("party", partyJson)
        playerData.add("pc", pcJson)
        playerData.addProperty("lastUpdated", System.currentTimeMillis())
        return playerData
    }

    fun togglePlayerPublic(player: ServerPlayer, showcaseEnabled: Boolean) {
        val json = JsonObject()
        json.addProperty("uuid", player.uuid.toString())
        json.addProperty("showcaseEnabled", showcaseEnabled)

        val success = sendPlayerToggle(clientSecret, json)
        if (success) {
            if (showcaseEnabled) {
                val onText = Component.literal("ON").withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN))
                val msg = Component.literal("Showcase is now ").append(onText)
                player.sendSystemMessage(msg)
            } else {
                val offText = Component.literal("OFF").withStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                val msg = Component.literal("Showcase is now ").append(offText)
                player.sendSystemMessage(msg)
            }
        } else {
            player.sendSystemMessage(Component.literal("Failed to toggle showcase visibility.").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)))
        }
    }

    fun syncPlayers(player: List<ServerPlayer>) {
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            val isSecretValid = isValidSecret
            val apiSecret = clientSecret

            if (apiSecret == null || !isSecretValid) {
                CobblemonExtras.getLogger().error("Invalid API Secret, please goto https://cobblemonextras.com/showcase to get your API Secret")
                return
            }
            if (CobblemonExtras.config.showcase.debug) {
                CobblemonExtras.getLogger().info("Syncing " + player.size + " players...")
            }

            // Build JSonArray of player data
            val request = JsonObject()
            val playerData = JsonArray()
            // Send http request to cobblemonextras.com/api/showcase, header = apiSecret, body = json of player data
            for (p in player) {
                try {
                    val playerJson = getPlayerJson(p)
                    if (playerJson != null) {
                        playerData.add(playerJson)
                    }
                } catch (exc: Exception) {
                    CobblemonExtras.getLogger().error("Error when syncing playerData for " + p.displayName!!.string)
                    exc.printStackTrace()
                }
            }

            request.add("players", playerData)
            if (!playerData.isEmpty) {
                sendUpdateRequest(apiSecret, request)
            }
        }
    }

    private val isValidSecret: Boolean
        get() {
            try {
                val uuid = CobblemonExtras.config.showcase.apiSecret.substring(CobblemonExtras.config.showcase.apiSecret.indexOf('-') + 1)
                return UUID.fromString(uuid) != null
            } catch (e: IllegalArgumentException) {
                CobblemonExtras.getLogger().error("Invalid API Secret, please goto https://cobblemonextras.com/showcase to get your API Secret")
                return false
            }
        }

    private val clientSecret: String?
        get() {
            if (!isValidSecret) {
                return null
            }

            val text = CobblemonExtras.config.showcase.apiSecret
            if (text == null || text.isEmpty()) {
                CobblemonExtras.getLogger().error("API Secret Missing, please goto https://cobblemonextras.com/showcase to get your API Secret")
                return null
            }

            if (text == "To start using showcase, please goto https://cobblemonextras.com/showcase") {
                CobblemonExtras.getLogger().error("API Secret Missing, please goto https://cobblemonextras.com/showcase to get your API Secret")
                return null
            }
            return CobblemonExtras.config.showcase.apiSecret
        }

    private fun sendUpdateRequest(apiToken: String, requestBody: JsonObject) {
        val httpClient: HttpClient = HttpClientBuilder.create().setRedirectStrategy(LaxRedirectStrategy()).build()
        try {
            val post = HttpPost(API_BASE_URL + "/sync")
            post.setHeader("Accept-Encoding", "UTF-8")
            post.setHeader("Content-type", "application/json")
            post.setHeader("Authorization", Base64.getEncoder().encodeToString(apiToken.toByteArray(StandardCharsets.UTF_8)))

            var payload: String? = requestBody.toString()

            try {
                payload = compress(payload)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val postingString = StringEntity(payload, "UTF-8")
            post.entity = postingString
            val response = httpClient.execute(post)
            if (response.statusLine.statusCode == 403) {
                CobblemonExtras.getLogger().warn("Trouble syncing playerData, Maximum player count has been reached.")
            } else if (response.statusLine.statusCode == 404) {
                CobblemonExtras.getLogger().warn(response.statusLine.statusCode.toString() + " - " + response.statusLine.reasonPhrase)
                CobblemonExtras.getLogger().warn("No server was found matching your configured clientSecret.")
                CobblemonExtras.getLogger().warn("Navigate to your dashboard and copy your clientSecret. https://cobblemonextras.com/showcase/manage")
            } else if (response.statusLine.statusCode != 200) {
                CobblemonExtras.getLogger().warn("Error when syncing playerData.")
                CobblemonExtras.getLogger().warn(response.statusLine.statusCode.toString() + " - " + response.statusLine.reasonPhrase)
            } else {
                hasFailed = false
            }
        } catch (ex: Exception) {
            if (!hasFailed) {
                ex.printStackTrace()
                hasFailed = true
            } else {
                CobblemonExtras.getLogger().error("Failed to sync playerData to Showcase. Please report this to the CobblemonExtras Team.")
            }
        }
    }


    @Throws(IOException::class)
    private fun compress(string: String?): String? {
        if (string == null || string.isEmpty()) {
            return string
        }
        val baos = ByteArrayOutputStream()
        val dos = DeflaterOutputStream(baos)
        dos.write(string.toByteArray(StandardCharsets.UTF_8))
        dos.close()
        return Base64.getEncoder().encodeToString(baos.toByteArray())
    }

    private fun sendPlayerToggle(apiToken: String?, requestBody: JsonObject): Boolean {
        val httpClient: HttpClient = HttpClientBuilder.create().setRedirectStrategy(LaxRedirectStrategy()).build()
        try {
            val post = HttpPost(API_BASE_URL + "/player")
            post.setHeader("Accept-Encoding", "UTF-8")
            post.setHeader("Content-type", "application/json")
            post.setHeader("Authorization", Base64.getEncoder().encodeToString(apiToken!!.toByteArray(StandardCharsets.UTF_8)))
            val postingString = StringEntity(requestBody.toString(), "UTF-8") //convert to json
            post.entity = postingString
            val response = httpClient.execute(post)
            if (response.statusLine.statusCode == 403) {
                CobblemonExtras.getLogger().warn("Trouble syncing player public visibility")
                return false
            } else if (response.statusLine.statusCode == 404) {
                CobblemonExtras.getLogger().warn("Player attempted to turn off their showcase visibility.")
                CobblemonExtras.getLogger().warn("No server was found matching your configured clientSecret.")
                return false
            } else if (response.statusLine.statusCode != 200) {
                CobblemonExtras.getLogger().warn("Error when syncing player public visibility.")
                CobblemonExtras.getLogger().warn(response.statusLine.statusCode.toString() + " - " + response.statusLine.reasonPhrase)
                return false
            } else {
                hasFailed = false
                return true
            }
        } catch (ex: Exception) {
            if (!hasFailed) {
                ex.printStackTrace()
                hasFailed = true
            } else {
                CobblemonExtras.getLogger().error("Failed to toggle player public visibility.")
            }
            return false
        }
    }
}