package dev.chasem.cobblemonextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.chasem.cobblemonextras.CobblemonExtras;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

public class CobblemonExtrasConfig {
    Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static int COMMAND_POKETRADE_PERMISSION_LEVEL = 2;
    public static int COMMAND_POKESEE_PERMISSION_LEVEL = 2;
    public static int COMMAND_POKESEEOTHER_PERMISSION_LEVEL = 2;
    public static int COMMAND_PC_PERMISSION_LEVEL = 2;
    public static int COMMAND_POKESHOUT_PERMISSION_LEVEL = 2;
    public static int COMMAND_COMPSEE_PERMISSION_LEVEL = 2;
    public static int COMMAND_COMPSEEOTHER_PERMISSION_LEVEL = 2;
    public static int COMMAND_POKEBATTLE_PERMISSION_LEVEL = 3;
    public static int COMMAND_BATTLE_PERMISSION_LEVEL = 2;
    public static int COMMAND_COMPTAKE_PERMISSION_LEVEL = 2;

    public static int COMMAND_POKEIVS_PERMISSION_LEVEL = 2;

    public CobblemonExtrasConfig() {
        init();
    }

    public void init() {
        File configFolder = new File(System.getProperty("user.dir") + "/config/cobblemonextras");
        File configFile = new File(configFolder, "config.json");
        System.out.println("CobblemonExtras config -> " + configFolder.getAbsolutePath());
        if (!configFolder.exists()) {
            configFolder.mkdirs();
            createConfig(configFolder);
        } else if (!configFile.exists()) {
            createConfig(configFolder);
        }

        try {
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            JsonObject obj = GSON.fromJson(new FileReader(configFile), JsonObject.class);
            JsonObject permLevels = obj.get("permissionlevels").getAsJsonObject();
            HashMap<String, Integer> permissionMap = GSON.fromJson(permLevels, type);

            COMMAND_POKETRADE_PERMISSION_LEVEL = permissionMap.getOrDefault("command.poketrade", 2);
            COMMAND_POKESEE_PERMISSION_LEVEL = permissionMap.getOrDefault("command.pokesee", 2);
            COMMAND_POKESEEOTHER_PERMISSION_LEVEL = permissionMap.getOrDefault("command.pokeseeother", 2);
            COMMAND_PC_PERMISSION_LEVEL = permissionMap.getOrDefault("command.pc", 2);
            COMMAND_POKESHOUT_PERMISSION_LEVEL = permissionMap.getOrDefault("command.pokeshout", 2);
            COMMAND_COMPSEE_PERMISSION_LEVEL = permissionMap.getOrDefault("command.compsee", 2);
            COMMAND_COMPSEEOTHER_PERMISSION_LEVEL = permissionMap.getOrDefault("command.compseeother", 2);
            COMMAND_BATTLE_PERMISSION_LEVEL = permissionMap.getOrDefault("command.battle", 2);
            COMMAND_POKEBATTLE_PERMISSION_LEVEL = permissionMap.getOrDefault("command.pokebattle", 2);
            COMMAND_COMPTAKE_PERMISSION_LEVEL = permissionMap.getOrDefault("command.comptake", 2);
            COMMAND_POKEIVS_PERMISSION_LEVEL = permissionMap.getOrDefault("command.pokeivs", 2);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createConfig(File configFolder) {
        File file = new File(configFolder, "config.json");
        try {
            file.createNewFile();
            JsonWriter writer = GSON.newJsonWriter(new FileWriter(file));
            writer.beginObject()
                    .name("permissionlevels")
                    .beginObject()
                        .name("command.poketrade")
                        .value(2)
                        .name("command.pokesee")
                        .value(2)
                        .name("command.pokeseeother")
                        .value(2)
                        .name("command.pc")
                        .value(2)
                        .name("command.pokeshout")
                        .value(2)
                        .name("command.compsee")
                        .value(2)
                        .name("command.compseeother")
                        .value(2)
                        .name("command.battle")
                        .value(2)
                        .name("command.pokebattle")
                        .value(2)
                        .name("command.comptake")
                        .value(2)
                        .name("command.pokeivs")
                        .value(2)
                    .endObject()
                .endObject()
                .flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
