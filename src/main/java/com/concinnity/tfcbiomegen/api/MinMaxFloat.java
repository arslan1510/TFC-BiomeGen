package com.concinnity.tfcbiomegen.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * JSON-friendly min/max float range.
 */
public record MinMaxFloat(float min, float max) {
    public static final Codec<MinMaxFloat> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(MinMaxFloat::min),
            Codec.FLOAT.fieldOf("max").forGetter(MinMaxFloat::max)
        ).apply(instance, MinMaxFloat::new)
    );

    public MinMaxFloat {
        if (min > max) throw new IllegalArgumentException("min must be <= max");
    }
}

