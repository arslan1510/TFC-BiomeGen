package com.concinnity.tfcbiomegen.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Config-driven definition for the TFC {@code BiomeExtension} that powers terrain/surface generation.
 *
 * Placement rules are datapack-driven (see {@link com.concinnity.tfcbiomegen.data.BiomePlacementRuleReloadListener}).
 */
public record BiomeExtensionConfig(
    String heightmap,
    String surface,
    String riverBlend,
    boolean spawnable
) {
    public static final Codec<BiomeExtensionConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("heightmap").forGetter(BiomeExtensionConfig::heightmap),
            Codec.STRING.fieldOf("surface").forGetter(BiomeExtensionConfig::surface),
            Codec.STRING.fieldOf("river_blend").forGetter(BiomeExtensionConfig::riverBlend),
            Codec.BOOL.optionalFieldOf("spawnable", false).forGetter(BiomeExtensionConfig::spawnable)
        ).apply(instance, BiomeExtensionConfig::new)
    );
}

