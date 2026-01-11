package com.concinnity.tfcbiomegen.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Datapack-driven placement rule for an already-registered biome.
 *
 * Loaded from: data/<namespace>/tfcbiomegen/biome_placement/<path>.json
 *
 * By default, the target biome id is the file id (<namespace>:<path>), but you can override it with {@code biome}.
 */
public record BiomePlacementRule(
    Optional<ResourceLocation> biome,
    Optional<String> replacesLayer,
    Optional<MinMaxFloat> temperature,
    Optional<MinMaxFloat> rainfall,
    float rarity
) {
    public static final Codec<BiomePlacementRule> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceLocation.CODEC.optionalFieldOf("biome").forGetter(BiomePlacementRule::biome),
            Codec.STRING.optionalFieldOf("replaces_layer").forGetter(BiomePlacementRule::replacesLayer),
            MinMaxFloat.CODEC.optionalFieldOf("temperature").forGetter(BiomePlacementRule::temperature),
            MinMaxFloat.CODEC.optionalFieldOf("rainfall").forGetter(BiomePlacementRule::rainfall),
            Codec.FLOAT.optionalFieldOf("rarity", 0.01f).forGetter(BiomePlacementRule::rarity)
        ).apply(instance, BiomePlacementRule::new)
    );

    public float minTemp() {
        return temperature.map(MinMaxFloat::min).orElse(-20f);
    }

    public float maxTemp() {
        return temperature.map(MinMaxFloat::max).orElse(30f);
    }

    public float minRainfall() {
        return rainfall.map(MinMaxFloat::min).orElse(0f);
    }

    public float maxRainfall() {
        return rainfall.map(MinMaxFloat::max).orElse(500f);
    }

    public BiomePlacementRule {
        if (rarity < 0f || rarity > 1f) {
            throw new IllegalArgumentException("rarity must be in [0.0, 1.0], got " + rarity);
        }
    }
}

