package com.concinnity.tfcbiomegen.api;

import com.concinnity.tfcbiomegen.TFCBiomeGen;
import net.dries007.tfc.world.biome.BiomeBuilder;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.noise.Noise2D;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.LongFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BiomeAPI {

    public record Rule(
        ResourceLocation biomeId,
        int layerId,
        int replacesLayerId,
        float minTemp,
        float maxTemp,
        float minRainfall,
        float maxRainfall,
        float rarity
    ) {}

    private static final Map<String, DeferredRegister<BiomeExtension>> REGISTERS = new ConcurrentHashMap<>();
    private static final List<Rule> RULES = new CopyOnWriteArrayList<>();
    private static final Map<ResourceLocation, BiomeExtension> BUILT_EXTENSIONS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Integer> LAYER_IDS = new ConcurrentHashMap<>();
    private static final Object RULE_LOCK = new Object();
    private static volatile boolean RULES_DIRTY = true;

    private BiomeAPI() {}

    public static void init(IEventBus modBus, String modId) {
        if (modId == null || modId.isBlank()) {
            throw new IllegalArgumentException("modId must be non-empty");
        }
        DeferredRegister<BiomeExtension> extensionReg = REGISTERS.computeIfAbsent(modId,
            id -> DeferredRegister.create(TFCBiomes.KEY, id));

        if (modBus != null) {
            extensionReg.register(modBus);
        }
    }


    public static DeferredHolder<BiomeExtension, BiomeExtension> registerBiome(
            String modId,
            String name,
            BiomeBuilder builder) {

        if (modId == null || modId.isBlank()) throw new IllegalArgumentException("modId must be non-empty");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must be non-empty");
        if (builder == null) throw new IllegalArgumentException("builder must be non-null");

        DeferredRegister<BiomeExtension> extensionReg = REGISTERS.get(modId);
        if (extensionReg == null) {
            throw new IllegalStateException("BiomeAPI.init must be called before registering biomes");
        }

        ResourceLocation id;
        try {
            id = ResourceLocation.fromNamespaceAndPath(modId, name);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid biome id: " + modId + ":" + name + " (" + e.getMessage() + ")", e);
        }
        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, id);

        // Build BiomeExtension immediately (like TFC does in static blocks)
        BiomeExtension extension = builder.build(key);

        // Store for later layer ID lookup
        BUILT_EXTENSIONS.put(id, extension);

        // Register the already-built extension
        return extensionReg.register(name, () -> extension);
    }

    public static DeferredHolder<BiomeExtension, BiomeExtension> registerBiome(
            String modId,
            String name,
            LongFunction<Noise2D> heightmap,
            BiomeEnums.Surface surface,
            BiomeEnums.RiverBlend riverBlend,
            boolean spawnable) {

        if (heightmap == null) throw new IllegalArgumentException("heightmap must be non-null");
        if (surface == null) throw new IllegalArgumentException("surface must be non-null");
        if (riverBlend == null) throw new IllegalArgumentException("riverBlend must be non-null");

        BiomeBuilder builder = BiomeBuilder.builder()
            .heightmap(heightmap)
            .surface(surface.factory())
            .type(riverBlend.type());

        if (spawnable) builder = builder.spawnable();

        return registerBiome(modId, name, builder);
    }

    public static RuleBuilder place(DeferredHolder<BiomeExtension, BiomeExtension> holder) {
        return new RuleBuilder(holder.getId(), holder);
    }

    public static List<Rule> rules() {
        rebuildRulesIfDirty();
        return List.copyOf(RULES);
    }

    // Delegated parse methods for convenience
    public static LongFunction<Noise2D> parseHeightmap(String name) {
        return BiomeParsers.parseHeightmap(name);
    }

    public static BiomeEnums.Surface parseSurface(String name) {
        return BiomeParsers.parseSurface(name);
    }

    public static BiomeEnums.RiverBlend parseRiverBlend(String name) {
        return BiomeParsers.parseRiverBlend(name);
    }

    public static BiomeEnums.Layer parseLayer(String name) {
        return BiomeParsers.parseLayer(name);
    }

    public static final class RuleBuilder {
        private final ResourceLocation biomeId;
        private int replacesLayerId = -1;
        private float minTemp = -20f, maxTemp = 30f;
        private float minRainfall = 0f, maxRainfall = 500f;
        private float rarity = 0.01f;

        private RuleBuilder(ResourceLocation id, DeferredHolder<BiomeExtension, BiomeExtension> holder) {
            this.biomeId = id;
        }

        public RuleBuilder replaces(int layerId) { this.replacesLayerId = layerId; return this; }
        public RuleBuilder replaces(BiomeEnums.Layer layer) { return replaces(layer == null ? -1 : layer.id()); }
        public RuleBuilder temperature(float min, float max) { this.minTemp = min; this.maxTemp = max; return this; }
        public RuleBuilder rainfall(float min, float max) { this.minRainfall = min; this.maxRainfall = max; return this; }
        public RuleBuilder rarity(float probability) { this.rarity = probability; return this; }

        public void register() {
            // Store deferred rule - layer ID will be resolved during worldgen
            JAVA_DEFERRED_RULES.add(new DeferredRule(biomeId, replacesLayerId, minTemp, maxTemp, minRainfall, maxRainfall, rarity));
            RULES_DIRTY = true;
            TFCBiomeGen.LOGGER.info("Registered placement rule for {} (rarity={})", biomeId, rarity);
        }
    }

    private record DeferredRule(
        ResourceLocation biomeId,
        int replacesLayerId,
        float minTemp,
        float maxTemp,
        float minRainfall,
        float maxRainfall,
        float rarity
    ) {}

    private static final List<DeferredRule> JAVA_DEFERRED_RULES = new CopyOnWriteArrayList<>();
    private static final List<DeferredRule> DATAPACK_DEFERRED_RULES = new CopyOnWriteArrayList<>();

    /**
     * Replace all datapack-provided placement rules.
     * This is intended to be called by a server datapack reload listener.
     */
    public static void setDatapackPlacements(Map<ResourceLocation, BiomePlacementRule> placementsByFileId) {
        synchronized (RULE_LOCK) {
            DATAPACK_DEFERRED_RULES.clear();
            if (placementsByFileId != null) {
                for (var entry : placementsByFileId.entrySet()) {
                    final ResourceLocation fileId = entry.getKey();
                    final BiomePlacementRule placement = entry.getValue();
                    if (fileId == null || placement == null) continue;

                    final ResourceLocation biomeId = placement.biome().orElse(fileId);
                    final int replacesLayerId = placement.replacesLayer()
                        .map(BiomeAPI::parseLayer)
                        .map(layer -> layer == null ? -1 : layer.id())
                        .orElse(-1);

                    DATAPACK_DEFERRED_RULES.add(new DeferredRule(
                        biomeId,
                        replacesLayerId,
                        placement.minTemp(),
                        placement.maxTemp(),
                        placement.minRainfall(),
                        placement.maxRainfall(),
                        placement.rarity()
                    ));
                }
            }
            RULES_DIRTY = true;
        }
    }

    private static void rebuildRulesIfDirty() {
        if (!RULES_DIRTY) return;
        synchronized (RULE_LOCK) {
            if (!RULES_DIRTY) return;

            final List<DeferredRule> ordered = new ArrayList<>(JAVA_DEFERRED_RULES.size() + DATAPACK_DEFERRED_RULES.size());
            ordered.addAll(JAVA_DEFERRED_RULES);
            ordered.addAll(DATAPACK_DEFERRED_RULES);

            RULES.clear();
            for (DeferredRule deferred : ordered) {
                try {
                    BiomeExtension extension = BUILT_EXTENSIONS.get(deferred.biomeId);
                    if (extension == null) {
                        TFCBiomeGen.LOGGER.error("No BiomeExtension found for {} (is it registered?)", deferred.biomeId);
                        continue;
                    }

                    // IMPORTANT: TFC expects biome_extension IDs to match the biome's ResourceLocation.
                    // In other words, a biome `foo:bar` must have a BiomeExtension registered as `foo:bar`
                    // in the `tfc:biome_extension` registry, or TFC won't find it.
                    final int layerId = LAYER_IDS.computeIfAbsent(deferred.biomeId, _id -> {
                        try {
                            return TFCLayers.idFor(extension);
                        } catch (IllegalStateException e) {
                            // exceeding the hard 128 layer limit in TFCLayers.
                            TFCBiomeGen.LOGGER.error("Failed to allocate TFCLayer id for {}: {}", deferred.biomeId, e.getMessage());
                            return -1;
                        }
                    });
                    if (layerId < 0) continue;

                    RULES.add(new Rule(
                        deferred.biomeId,
                        layerId,
                        deferred.replacesLayerId,
                        deferred.minTemp,
                        deferred.maxTemp,
                        deferred.minRainfall,
                        deferred.maxRainfall,
                        deferred.rarity
                    ));
                } catch (Exception e) {
                    TFCBiomeGen.LOGGER.error("Failed to resolve biome {}: {}", deferred.biomeId, e.getMessage(), e);
                }
            }

            RULES_DIRTY = false;
        }
    }
}
