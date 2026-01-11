package com.concinnity.tfcbiomegen.data;

import com.concinnity.tfcbiomegen.TFCBiomeGen;
import com.concinnity.tfcbiomegen.api.BiomeAPI;
import com.concinnity.tfcbiomegen.api.BiomeExtensionConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Loads TFC {@code BiomeExtension} configs from the config directory during mod initialization.
 * Must run BEFORE RegisterEvent fires.
 */
public final class BiomeExtensionConfigLoader {
    private static final String CONFIG_DIR = "tfcbiomegen/biomes";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private BiomeExtensionConfigLoader() {}

    public static void load() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_DIR);

        try {
            Files.createDirectories(configPath);

            try (Stream<Path> paths = Files.walk(configPath)) {
                long count = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .peek(BiomeExtensionConfigLoader::loadFromFile)
                    .count();

                TFCBiomeGen.LOGGER.info("Loaded {} biome extension config(s) from {}", count, configPath);
            }
        } catch (IOException e) {
            TFCBiomeGen.LOGGER.error("Failed to load biome extensions from config", e);
        }
    }

    private static void loadFromFile(Path path) {
        try {
            String json = Files.readString(path);
            JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);

            final var parsed = BiomeExtensionConfig.CODEC.parse(JsonOps.INSTANCE, jsonObject);
            final var definitionOpt = parsed.resultOrPartial(err ->
                TFCBiomeGen.LOGGER.error("Biome extension config parse error in {}: {}", path, err)
            );
            if (definitionOpt.isEmpty()) return;
            final BiomeExtensionConfig definition = definitionOpt.get();

            String fileName = path.getFileName().toString();
            if (!fileName.endsWith(".json")) return;
            fileName = fileName.substring(0, fileName.length() - ".json".length());
            if (fileName.isBlank()) {
                TFCBiomeGen.LOGGER.error("Invalid biome extension config filename {} (empty biome name)", path);
                return;
            }
            try {
                ResourceLocation.fromNamespaceAndPath(TFCBiomeGen.MODID, fileName);
            } catch (Exception e) {
                TFCBiomeGen.LOGGER.error("Invalid biome extension config filename {} (must be a valid resource path): {}", path, e.getMessage());
                return;
            }

            register(TFCBiomeGen.MODID, fileName, definition);
        } catch (Exception e) {
            TFCBiomeGen.LOGGER.error("Failed to load biome extension from {}: {}", path, e.getMessage(), e);
        }
    }

    private static void register(String namespace, String name, BiomeExtensionConfig definition) {
        var heightmap = BiomeAPI.parseHeightmap(definition.heightmap());
        var surface = BiomeAPI.parseSurface(definition.surface());
        var riverBlend = BiomeAPI.parseRiverBlend(definition.riverBlend());

        BiomeAPI.registerBiome(
            namespace,
            name,
            heightmap,
            surface,
            riverBlend,
            definition.spawnable()
        );
    }
}

