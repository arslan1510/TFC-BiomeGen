package com.concinnity.tfcbiomegen.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Config-driven definition for the TFC {@code BiomeExtension} that powers terrain/surface generation.
 *
 * Placement rules are also config-driven (same file), evaluated during TFC worldgen via mixin.
 */
public record BiomeExtensionConfig(
    String heightmap,
    String surface,
    String riverBlend,
    boolean spawnable,

    Optional<String> replacesLayer,
    Optional<MinMaxFloat> temperature,
    Optional<MinMaxFloat> rainfall,
    float rarity
) {
    public static final Codec<BiomeExtensionConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("heightmap").forGetter(BiomeExtensionConfig::heightmap),
            Codec.STRING.fieldOf("surface").forGetter(BiomeExtensionConfig::surface),
            Codec.STRING.fieldOf("river_blend").forGetter(BiomeExtensionConfig::riverBlend),
            Codec.BOOL.optionalFieldOf("spawnable", false).forGetter(BiomeExtensionConfig::spawnable),

            Codec.STRING.optionalFieldOf("replaces_layer").forGetter(BiomeExtensionConfig::replacesLayer),
            MinMaxFloat.CODEC.optionalFieldOf("temperature").forGetter(BiomeExtensionConfig::temperature),
            MinMaxFloat.CODEC.optionalFieldOf("rainfall").forGetter(BiomeExtensionConfig::rainfall),
            Codec.FLOAT.optionalFieldOf("rarity", 0.01f).forGetter(BiomeExtensionConfig::rarity)
        ).apply(instance, BiomeExtensionConfig::new)
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

    public BiomeExtensionConfig {
        if (rarity < 0f || rarity > 1f) {
            throw new IllegalArgumentException("rarity must be in [0.0, 1.0], got " + rarity);
        }
        replacesLayer = replacesLayer == null ? Optional.empty() : replacesLayer;
        temperature = temperature == null ? Optional.empty() : temperature;
        rainfall = rainfall == null ? Optional.empty() : rainfall;
    }
}

