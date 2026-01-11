package com.concinnity.tfcbiomegen.mixin;

import com.concinnity.tfcbiomegen.api.BiomeAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.dries007.tfc.world.region.ChooseBiomes;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.layer.framework.Area;

/**
 * after TFC chooses biomes, apply addon placements registered through
 * {@link BiomeAPI}.
 */
@Mixin(ChooseBiomes.class)
public final class ChooseBiomesMixin
{
    /**
     * Rule resolution/priority:
     * - Rules are evaluated in registration order.
     * - If multiple rules match, later rules will overwrite earlier ones (no early-exit).
     *
     * This is intentional so packs/mods can define broad rules and then override with narrower rules later.
     */
    @Inject(
        method = "apply",
        at = @At("TAIL")
    )
    private void tfcbiomegen$afterChooseBiomes(
        RegionGenerator.Context context,
        CallbackInfo ci
    )
    {
        final var rules = BiomeAPI.rules();
        if (rules.isEmpty()) return;

        final Region region = context.region;
        final Area blobArea = context.generator().biomeArea.get();
        final long rngSeed = context.random.nextLong();

        for (final var point : region.points())
        {
            final int areaSeed = blobArea.get(point.x, point.z);
            for (int i = 0; i < rules.size(); i++) {
                final BiomeAPI.Rule p = rules.get(i);

                if (p.replacesLayerId() != -1 && point.biome != p.replacesLayerId()) continue;

                if (point.temperature < p.minTemp() || point.temperature > p.maxTemp()) continue;
                if (point.rainfall < p.minRainfall() || point.rainfall > p.maxRainfall()) continue;

                final double roll = tfcbiomegen$unitDouble(tfcbiomegen$mix64(rngSeed ^ (long) areaSeed ^ (0x9E3779B97F4A7C15L * (i + 1))));
                if (roll >= p.rarity()) continue;

                point.biome = p.layerId();
            }
        }
    }

    @Unique
    private static long tfcbiomegen$mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    @Unique
    private static double tfcbiomegen$unitDouble(long z) {
        return (double) ((z >>> 11) & ((1L << 53) - 1)) * (1.0 / (1L << 53));
    }
}
