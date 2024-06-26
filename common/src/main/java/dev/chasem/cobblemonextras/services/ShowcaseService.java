package dev.chasem.cobblemonextras.services;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.api.scheduling.ServerRealTimeTaskTracker;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import dev.chasem.cobblemonextras.CobblemonExtras;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.gson.JsonObject;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DeflaterOutputStream;

public class ShowcaseService {
//    public static String API_BASE_URL = "http://localhost:3000/api";

    public static String API_BASE_URL = "https://cobblemonextras.com/api";

    public boolean hasFailed = false;
    public ScheduledTask showcaseTask;

    ExecutorService threadPool = Executors.newSingleThreadExecutor(  new ThreadFactoryBuilder().setNameFormat("CobblemonExtrasShowcaseSyncThread-%d").build());

    public void stop() {
        if (showcaseTask != null) {
            showcaseTask.expire();
            CobblemonExtras.INSTANCE.getLogger().info("Showcase Repeating Task Stopped");
        }
        if (threadPool != null) {
            threadPool.shutdownNow();
            CobblemonExtras.INSTANCE.getLogger().info("Showcase Thread Pool Stopped");
        }
    }

    public void init() {
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {
            CobblemonExtras.INSTANCE.getLogger().info("Enabling Showcase...");
            if (getClientSecret() == null) {
                CobblemonExtras.INSTANCE.getLogger().info("Failed to enable Showcase");
                CobblemonExtras.INSTANCE.getLogger().error("Invalid API Secret, please goto https://cobblemonextras.com/showcase to get your API Secret");
            } else {

                CobblemonExtras.INSTANCE.getLogger().info("[CobblemonExtras] Showcase will sync every " + CobblemonExtras.config.showcase.syncIntervalMinutes + " minutes.");

                ScheduledTask.Builder builder = new ScheduledTask.Builder();
                showcaseTask = builder.execute(scheduledTask -> {
                    if (CobblemonExtras.config.showcase.debug) {
                        System.out.println("Running showcase sync task...");
                    }
                    MinecraftServer server = Cobblemon.INSTANCE.getImplementation().server();
                    // Sync all players
                    syncPlayers(server.getPlayerManager().getPlayerList().toArray(new ServerPlayerEntity[0]), CobblemonExtras.config.showcase.async);
                    return null;
                })
                .interval(60 * Math.max(CobblemonExtras.config.showcase.syncIntervalMinutes, 1))
                .infiniteIterations()
                .identifier("cobblemonextras:showcase_sync_task")
                .build();
                ServerRealTimeTaskTracker.INSTANCE.addTask(showcaseTask);

                CobblemonExtras.INSTANCE.getLogger().info("Showcase Enabled");
            }
        }
    }

    public JsonObject getPokemonJson(Pokemon pokemon) {
        JsonObject pokemonJson = pokemon.saveToJSON(new JsonObject());
        try {
            pokemonJson.addProperty("Nature", LocalizationUtilsKt.lang(pokemon.getNature().getDisplayName().replace("cobblemon.", "")).getString());
            pokemonJson.addProperty("DexNumber", pokemon.getSpecies().getNationalPokedexNumber());
            if (pokemon.getNickname() != null) {
                pokemonJson.addProperty("Nickname", pokemon.getNickname().getString());
            } else {
                pokemonJson.add("Nickname", JsonNull.INSTANCE);
            }
            pokemonJson.addProperty("Name", pokemon.getSpecies().getTranslatedName().getString());

            // Ability
            JsonObject abilityJson = pokemonJson.getAsJsonObject("Ability");
            abilityJson.addProperty("AbilityName", LocalizationUtilsKt.lang(pokemon.getAbility().getDisplayName().replace("cobblemon.", "")).getString());
            pokemonJson.add("Ability", abilityJson);

            // Form
            pokemonJson.addProperty("FormName", pokemon.getForm().getName());

            pokemonJson.addProperty("Type1", pokemon.getPrimaryType().getDisplayName().getString());
            if (pokemon.getSecondaryType() != null) {
                pokemonJson.addProperty("Type2", pokemon.getSecondaryType().getDisplayName().getString());
            } else {
                pokemonJson.add("Type2", JsonNull.INSTANCE);
            }

            // Movesets
            JsonObject moveJson = pokemonJson.getAsJsonObject("MoveSet");
            JsonElement moveSet0 = moveJson.get("MoveSet0");
            JsonElement moveSet1 = moveJson.get("MoveSet1");
            JsonElement moveSet2 = moveJson.get("MoveSet2");
            JsonElement moveSet3 = moveJson.get("MoveSet3");
            if (moveSet0 != null && !moveSet0.isJsonNull()) {
                moveSet0.getAsJsonObject().addProperty("MoveName", pokemon.getMoveSet().get(0).getDisplayName().getString());
                moveSet0.getAsJsonObject().addProperty("MoveType", pokemon.getMoveSet().get(0).getType().getName());
                moveJson.add("MoveSet0", moveSet0);
            }
            if (moveSet1 != null && !moveSet1.isJsonNull()) {
                moveSet1.getAsJsonObject().addProperty("MoveName", pokemon.getMoveSet().get(1).getDisplayName().getString());
                moveSet1.getAsJsonObject().addProperty("MoveType", pokemon.getMoveSet().get(1).getType().getName());
                moveJson.add("MoveSet1", moveSet1);
            }
            if (moveSet2 != null && !moveSet2.isJsonNull()) {
                moveSet2.getAsJsonObject().addProperty("MoveName", pokemon.getMoveSet().get(2).getDisplayName().getString());
                moveSet2.getAsJsonObject().addProperty("MoveType", pokemon.getMoveSet().get(2).getType().getName());
                moveJson.add("MoveSet2", moveSet2);
            }
            if (moveSet3 != null && !moveSet3.isJsonNull()) {
                moveSet3.getAsJsonObject().addProperty("MoveName", pokemon.getMoveSet().get(3).getDisplayName().getString());
                moveSet3.getAsJsonObject().addProperty("MoveType", pokemon.getMoveSet().get(3).getType().getName());
                moveJson.add("MoveSet3", moveSet3);
            }
            pokemonJson.add("MoveSet", moveJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return pokemonJson;
    }

    public JsonObject getPlayerJson(ServerPlayerEntity player) {
        // Obtain cobblemon pc data
        PlayerPartyStore party = null;
        PCStore pc = null;
        try {
            party = Cobblemon.INSTANCE.getStorage().getParty(player);
            pc = Cobblemon.INSTANCE.getStorage().getPC(player.getUuid());
        } catch (NoPokemonStoreException e) {
//            System.out.println("No PCStore found for player, skipping sync - " + player.getDisplayName().getString());
        } catch (Exception exc) {
            return null;
        }
        JsonObject playerData = new JsonObject();
        playerData.addProperty("uuid", player.getUuid().toString());
        playerData.addProperty("name", player.getDisplayName().getString());

        JsonArray partyJson = new JsonArray();
        for (int i = 0; i < 6; i++) {
            Pokemon pokemon = party.get(i);
            if (pokemon == null) {
                partyJson.add(JsonNull.INSTANCE);
            } else {
                JsonObject pokemonJson = getPokemonJson(pokemon);
                partyJson.add(Objects.requireNonNullElse(pokemonJson, JsonNull.INSTANCE));
            }
        }

        JsonElement pcJson = new JsonArray();
        if (pc != null) {
            for (PCBox box : pc.getBoxes()) {
                JsonArray boxJson = new JsonArray();
                for (int slot = 0; slot < 30; slot++) {
                    Pokemon pokemon = box.get(slot);
                    if (pokemon == null) {
                        boxJson.add(JsonNull.INSTANCE);
                    } else {
                        JsonObject pokemonJson = getPokemonJson(pokemon);
                        boxJson.add(Objects.requireNonNullElse(pokemonJson, JsonNull.INSTANCE));
                    }
                }
                ((JsonArray) pcJson).add(boxJson);
            }
        } else {
            pcJson = JsonNull.INSTANCE;
        }
        playerData.add("party", partyJson);
        playerData.add("pc", pcJson);
        playerData.addProperty("lastUpdated", System.currentTimeMillis());
        return playerData;
    }

    public void syncPlayers(ServerPlayerEntity[] player, boolean async) {
        if (async && CobblemonExtras.config.showcase.async) {
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            threadPool.submit(() -> {
                if (CobblemonExtras.config.showcase.debug) {
                    System.out.println("Preparing to sync players async...");
                }
                // Confirming we're not halting the main thread
                // Thread.sleep(2000);
                if (CobblemonExtras.config.showcase.debug) {
                    System.out.println("Syncing players async...");
                }
                syncPlayers(player);
                completableFuture.complete("Done");
                return null;
            });
        } else {
            syncPlayers(player);
        }
    }

    public void togglePlayerPublic(ServerPlayerEntity player, boolean showcaseEnabled) {

        JsonObject json = new JsonObject();
        json.addProperty("uuid", player.getUuid().toString());
        json.addProperty("showcaseEnabled", showcaseEnabled);

        boolean success = sendPlayerToggle(getClientSecret(), json);
        if (success) {
            if (showcaseEnabled) {
                List<Text> onText = Text.literal("ON").getWithStyle(Style.EMPTY.withColor(Formatting.GREEN));
                Text msg = Text.literal("Showcase is now ").append(Texts.join(onText, Text.of("")));
                player.sendMessage(msg);
            } else {
                List<Text> onText = Text.literal("OFF").getWithStyle(Style.EMPTY.withColor(Formatting.RED));
                Text msg = Text.literal("Showcase is now ").append(Texts.join(onText, Text.of("")));
                player.sendMessage(msg);
            }
        } else {
            player.sendMessage(Text.of("Failed to toggle showcase visibility."));
        }
    }
    public void syncPlayers(ServerPlayerEntity[] player) {
        if (CobblemonExtras.config.showcase.isShowcaseEnabled) {

            boolean isSecretValid = isValidSecret();
            String apiSecret = getClientSecret();

            if (apiSecret == null || !isSecretValid) {
                CobblemonExtras.INSTANCE.getLogger().error("Invalid API Secret, please goto https://cobblemonextras.com/showcase to get your API Secret");
                return;
            }
            if (CobblemonExtras.config.showcase.debug) {
                CobblemonExtras.INSTANCE.getLogger().info("Syncing " + player.length + " players...");
            }
            // Build JSonArray of player data

            JsonObject request = new JsonObject();
            JsonArray playerData = new JsonArray();
            // Send http request to cobblemonextras.com/api/showcase, header = apiSecret, body = json of player data
            for (ServerPlayerEntity p : player) {
                try {
                    JsonObject playerJson = getPlayerJson(p);
                    if (playerJson != null) {
                        playerData.add(playerJson);
                    }
                } catch (Exception exc) {
                    CobblemonExtras.INSTANCE.getLogger().error("Error when syncing playerData for " + p.getDisplayName().getString());
                    exc.printStackTrace();
                }
            }

            request.add("players", playerData);
            if (!playerData.isEmpty()) {
                sendUpdateRequest(apiSecret, request);
            }
        }
    }

    private boolean isValidSecret() {
        try {
            String uuid = CobblemonExtras.config.showcase.apiSecret.substring(CobblemonExtras.config.showcase.apiSecret.indexOf('-') + 1);
            return UUID.fromString(uuid) != null;
        } catch (IllegalArgumentException e) {
            CobblemonExtras.INSTANCE.getLogger().error("Invalid API Secret, please goto https://cobblemonextras.com/showcase to get your API Secret");
            return false;
        }
    }

    private String getClientSecret() {

        if (!isValidSecret()) {
            return null;
        }

        String text = CobblemonExtras.config.showcase.apiSecret;
        if (text == null || text.isEmpty()) {
            CobblemonExtras.INSTANCE.getLogger().error("API Secret Missing, please goto https://cobblemonextras.com/showcase to get your API Secret");
            return null;
        }

        if (text.equals("To start using showcase, please goto https://cobblemonextras.com/showcase")) {
            CobblemonExtras.INSTANCE.getLogger().error("API Secret Missing, please goto https://cobblemonextras.com/showcase to get your API Secret");
            return null;
        }
        return CobblemonExtras.config.showcase.apiSecret;
    }

    private void sendUpdateRequest(String apiToken, JsonObject requestBody) {
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        try {
            HttpPost post = new HttpPost(API_BASE_URL + "/sync");
            post.setHeader("Accept-Encoding", "UTF-8");
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", Base64.getEncoder().encodeToString(apiToken.getBytes(StandardCharsets.UTF_8)));

            String payload = requestBody.toString();

            try {
                payload = compress(payload);
            } catch (Exception e) {
                e.printStackTrace();
            }

            StringEntity postingString = new StringEntity(payload, "UTF-8");
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == 403) {
                CobblemonExtras.INSTANCE.getLogger().warn("Trouble syncing playerData, Maximum player count has been reached.");
            } else if (response.getStatusLine().getStatusCode() == 404) {
                CobblemonExtras.INSTANCE.getLogger().warn(response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase());
                CobblemonExtras.INSTANCE.getLogger().warn("No server was found matching your configured clientSecret.");
                CobblemonExtras.INSTANCE.getLogger().warn("Navigate to your dashboard and copy your clientSecret. https://cobblemonextras.com/showcase/manage");
            } else if (response.getStatusLine().getStatusCode() != 200) {
                CobblemonExtras.INSTANCE.getLogger().warn("Error when syncing playerData.");
                CobblemonExtras.INSTANCE.getLogger().warn(response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase());
            } else {
                hasFailed = false;
            }
        } catch (Exception ex) {
            if (!hasFailed) {
                ex.printStackTrace();
                hasFailed = true;
            } else {
                CobblemonExtras.INSTANCE.getLogger().error("Failed to sync playerData to Showcase. Please report this to the CobblemonExtras Team.");
            }
        }
    }


    private String compress(String string) throws IOException {
        if (string == null || string.isEmpty()) {
            return string;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflaterOutputStream dos = new DeflaterOutputStream(baos);
        dos.write(string.getBytes(StandardCharsets.UTF_8));
        dos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private boolean sendPlayerToggle(String apiToken, JsonObject requestBody) {
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        try {
            HttpPost post = new HttpPost(API_BASE_URL + "/player");
            post.setHeader("Accept-Encoding", "UTF-8");
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", Base64.getEncoder().encodeToString(apiToken.getBytes(StandardCharsets.UTF_8)));
            StringEntity postingString = new StringEntity(requestBody.toString(), "UTF-8"); //convert to json
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == 403) {
                CobblemonExtras.INSTANCE.getLogger().warn("Trouble syncing player public visibility");
                return false;
            } else if (response.getStatusLine().getStatusCode() == 404) {
                CobblemonExtras.INSTANCE.getLogger().warn("Player attempted to turn off their showcase visibility.");
                CobblemonExtras.INSTANCE.getLogger().warn("No server was found matching your configured clientSecret.");
                return false;
            } else if (response.getStatusLine().getStatusCode() != 200) {
                CobblemonExtras.INSTANCE.getLogger().warn("Error when syncing player public visibility.");
                CobblemonExtras.INSTANCE.getLogger().warn(response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase());
                return false;
            } else {
                hasFailed = false;
                return true;
            }
        } catch (Exception ex) {
            if (!hasFailed) {
                ex.printStackTrace();
                hasFailed = true;
            } else {
                CobblemonExtras.INSTANCE.getLogger().error("Failed to toggle player public visibility.");
            }
            return false;
        }
    }

}
