package com.concinnity.tfcbiomegen.data;

import com.concinnity.tfcbiomegen.TFCBiomeGen;
import com.concinnity.tfcbiomegen.api.BiomeAPI;
import com.concinnity.tfcbiomegen.api.BiomePlacementRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

/**
 * Loads datapack-driven biome placement rules.
 *
 * Files live at: data/<namespace>/tfcbiomegen/biome_placement/<path>.json
 */
public final class BiomePlacementRuleReloadListener extends SimpleJsonResourceReloadListener {
    public static final String DIRECTORY = "tfcbiomegen/biome_placement";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public BiomePlacementRuleReloadListener() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> json, ResourceManager resourceManager, ProfilerFiller profiler) {
        final Map<ResourceLocation, BiomePlacementRule> placementsByFileId = new java.util.HashMap<>();

        for (var entry : json.entrySet()) {
            final ResourceLocation fileId = entry.getKey(); // <namespace>:<path>
            final JsonElement element = entry.getValue();

            try {
                final var parsed = BiomePlacementRule.CODEC.parse(JsonOps.INSTANCE, element);
                final var placementOpt = parsed.resultOrPartial(err ->
                    TFCBiomeGen.LOGGER.error("Biome placement parse error in {}: {}", fileId, err)
                );
                if (placementOpt.isEmpty()) continue;
                placementsByFileId.put(fileId, placementOpt.get());
            } catch (Exception e) {
                TFCBiomeGen.LOGGER.error("Failed to load biome placement {}: {}", fileId, e.getMessage(), e);
            }
        }

        BiomeAPI.setDatapackPlacements(placementsByFileId);
        TFCBiomeGen.LOGGER.info("Loaded {} biome placement file(s) from datapacks ({})", placementsByFileId.size(), DIRECTORY);
    }
}

