package com.concinnity.tfcbiomegen.api;

import com.concinnity.tfcbiomegen.TFCBiomeGen;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;

import java.util.function.LongFunction;

/**
 * Parser utilities for converting string config values to TFC biome components.
 */
public final class BiomeParsers {

    private BiomeParsers() {}

    /**
     * Parse a heightmap name to a TFC BiomeNoise heightmap generator.
     * Supports both short names (e.g., "plains") and namespaced names (e.g., "tfc:plains").
     *
     * @param name The heightmap name to parse
     * @return A LongFunction that generates the appropriate heightmap noise
     */
    public static LongFunction<Noise2D> parseHeightmap(String name) {
        return switch (name.toLowerCase()) {
            case "plains", "tfc:plains" -> seed -> BiomeNoise.hills(seed, 4, 10);
            case "hills", "tfc:hills" -> seed -> BiomeNoise.hills(seed, -5, 16);
            case "lowlands", "tfc:lowlands" -> BiomeNoise::lowlands;
            case "low_canyons", "tfc:low_canyons" -> seed -> BiomeNoise.canyons(seed, -8, 21);

            case "rolling_hills", "tfc:rolling_hills" -> seed -> BiomeNoise.hills(seed, -5, 28);
            case "highlands", "tfc:highlands" -> seed -> BiomeNoise.sharpHills(seed, -3, 28);
            case "badlands", "tfc:badlands" -> seed -> BiomeNoise.badlands(seed, 22, 19.5f);
            case "plateau", "tfc:plateau" -> seed -> BiomeNoise.hills(seed, 20, 30);
            case "canyons", "tfc:canyons" -> seed -> BiomeNoise.canyons(seed, -2, 40);
            case "mountains", "tfc:mountains" -> seed -> BiomeNoise.mountains(seed, 10, 70);
            case "old_mountains", "tfc:old_mountains" -> seed -> BiomeNoise.mountains(seed, 16, 40);
            case "oceanic_mountains", "tfc:oceanic_mountains" -> seed -> BiomeNoise.mountains(seed, -16, 60);
            case "dunes", "tfc:dunes" -> seed -> BiomeNoise.dunes(seed, 2, 16);
            case "flats", "tfc:flats" -> BiomeNoise::flats;
            case "salt_flats", "tfc:salt_flats" -> BiomeNoise::saltFlats;

            default -> {
                TFCBiomeGen.LOGGER.warn("Unknown heightmap '{}', defaulting to plains", name);
                yield seed -> BiomeNoise.hills(seed, 4, 10);
            }
        };
    }

    /**
     * Parse a surface name to a BiomeEnums.Surface enum.
     *
     * @param name The surface name to parse
     * @return The corresponding Surface enum value
     */
    public static BiomeEnums.Surface parseSurface(String name) {
        try {
            return BiomeEnums.Surface.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            TFCBiomeGen.LOGGER.warn("Unknown surface '{}', defaulting to NORMAL", name);
            return BiomeEnums.Surface.NORMAL;
        }
    }

    /**
     * Parse a river blend name to a BiomeEnums.RiverBlend enum.
     *
     * @param name The river blend name to parse
     * @return The corresponding RiverBlend enum value
     */
    public static BiomeEnums.RiverBlend parseRiverBlend(String name) {
        try {
            return BiomeEnums.RiverBlend.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            TFCBiomeGen.LOGGER.warn("Unknown river blend '{}', defaulting to FLOODPLAIN", name);
            return BiomeEnums.RiverBlend.FLOODPLAIN;
        }
    }

    /**
     * Parse a layer name to a BiomeEnums.Layer enum.
     * Supports both short names (e.g., "plains") and namespaced names (e.g., "tfc:plains").
     *
     * @param name The layer name to parse
     * @return The corresponding Layer enum value, or null if not found
     */
    public static BiomeEnums.Layer parseLayer(String name) {
        try {
            String cleanName = name.toLowerCase().replace("tfc:", "").toUpperCase();
            return BiomeEnums.Layer.valueOf(cleanName);
        } catch (IllegalArgumentException e) {
            TFCBiomeGen.LOGGER.warn("Unknown layer '{}'", name);
            return null;
        }
    }
}
