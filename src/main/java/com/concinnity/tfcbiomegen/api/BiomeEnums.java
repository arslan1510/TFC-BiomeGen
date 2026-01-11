package com.concinnity.tfcbiomegen.api;

import net.dries007.tfc.world.layer.TFCLayers;
import net.dries007.tfc.world.river.RiverBlendType;
import net.dries007.tfc.world.surface.builder.*;

/**
 * TFC biome configuration enums - surfaces, river blends, and layer IDs.
 */
public final class BiomeEnums {

    private BiomeEnums() {}

    public enum Surface {
        NORMAL(NormalSurfaceBuilder.INSTANCE),
        ROCKY(NormalSurfaceBuilder.ROCKY),
        LOWLANDS(LowlandsSurfaceBuilder.INSTANCE),
        BADLANDS_NORMAL(BadlandsSurfaceBuilder.NORMAL),
        BADLANDS_WARPED(BadlandsSurfaceBuilder.WARPED),
        BADLANDS_MESAS(BadlandsSurfaceBuilder.MESAS),
        BADLANDS_HOODOOS(BadlandsSurfaceBuilder.HOODOOS),
        FLATS_MUDDY(FlatsSurfaceBuilder.MUDDY),
        FLATS_SALTY(FlatsSurfaceBuilder.SALTY),
        DUNES(DuneSurfaceBuilder.INSTANCE),
        GRASSY_DUNES(GrassyDunesSurfaceBuilder.INSTANCE),
        VOLCANIC_SOIL(SimpleSurfaceBuilder.VOLCANIC_SOIL),
        ROCKY_VOLCANIC_SOIL(SimpleSurfaceBuilder.ROCKY_VOLCANIC_SOIL),
        ROCKY_PLATEAU(RockyPlateauSurfaceBuilder.INSTANCE),
        BURREN(BurrenSurfaceBuilder.INSTANCE),
        SHILIN(ShilinSurfaceBuilder.INSTANCE),
        SHIELD_VOLCANO_ACTIVE(ShieldVolcanoSurfaceBuilder.ACTIVE),
        SHIELD_VOLCANO_DORMANT(ShieldVolcanoSurfaceBuilder.DORMANT),
        ICE_SHEET_NORMAL(IceSheetSurfaceBuilder.NORMAL),
        ICE_SHEET_EDGE(IceSheetSurfaceBuilder.EDGE),
        ICE_SHEET_OCEANIC(IceSheetSurfaceBuilder.OCEANIC),
        ICE_SHEET_MOUNTAINS(IceSheetSurfaceBuilder.ICE_SHEET_MOUNTAINS),
        ICE_SHEET_OCEANIC_MOUNTAINS(IceSheetSurfaceBuilder.ICE_SHEET_OCEANIC_MOUNTAINS),
        ICE_SHEET_GLACIATED_MOUNTAINS(IceSheetSurfaceBuilder.GLACIATED_MOUNTAINS),
        ICE_SHEET_GLACIATED_OCEANIC_MOUNTAINS(IceSheetSurfaceBuilder.GLACIATED_OCEANIC_MOUNTAINS),
        ICE_SHEET_HIDDEN_LAKE(IceSheetSurfaceBuilder.HIDDEN_LAKE),
        ICE_SHEET_EDGE_LAKE(IceSheetSurfaceBuilder.EDGE_LAKE),
        PATTERNED_GROUND(PatternedGroundSurfaceBuilder.INSTANCE),
        STONE_CIRCLES(StoneCirclesSurfaceBuilder.INSTANCE),
        SHORE_OCEAN(ShoreAndOceanSurfaceBuilder.OCEAN),
        SHORE_SANDY(ShoreAndOceanSurfaceBuilder.SANDY),
        SHORE_ROCKY(ShoreAndOceanSurfaceBuilder.ROCKY_SHORE),
        SHORE_SEA_CLIFFS(ShoreAndOceanSurfaceBuilder.SEA_CLIFFS),
        SHORE_MOUNTAINS(ShoreAndOceanSurfaceBuilder.MOUNTAINS),
        SHORE_VOLCANIC_MOUNTAINS(ShoreAndOceanSurfaceBuilder.VOLCANIC_MOUNTAINS),
        RIVER(RiverSurfaceBuilder.INSTANCE);

        private final SurfaceBuilderFactory factory;

        Surface(SurfaceBuilderFactory factory) {
            this.factory = factory;
        }

        public SurfaceBuilderFactory factory() {
            return factory;
        }
    }

    public enum RiverBlend {
        FLOODPLAIN(RiverBlendType.FLOODPLAIN),
        BANKED(RiverBlendType.BANKED),
        TALL_BANKED(RiverBlendType.TALL_BANKED),
        WIDE(RiverBlendType.WIDE),
        WIDE_DEEP(RiverBlendType.WIDE_DEEP),
        CANYON(RiverBlendType.CANYON),
        TALL_CANYON(RiverBlendType.TALL_CANYON),
        TALUS(RiverBlendType.TALUS),
        CAVE(RiverBlendType.CAVE),
        TERRACES(RiverBlendType.TERRACES);

        private final RiverBlendType type;

        RiverBlend(RiverBlendType type) {
            this.type = type;
        }

        public RiverBlendType type() {
            return type;
        }
    }

    public enum Layer {
        OCEAN(TFCLayers.OCEAN), OCEAN_REEF(TFCLayers.OCEAN_REEF),
        DEEP_OCEAN(TFCLayers.DEEP_OCEAN), DEEP_OCEAN_TRENCH(TFCLayers.DEEP_OCEAN_TRENCH),
        PLAINS(TFCLayers.PLAINS), HILLS(TFCLayers.HILLS),
        LOWLANDS(TFCLayers.LOWLANDS), LOW_CANYONS(TFCLayers.LOW_CANYONS),
        ROLLING_HILLS(TFCLayers.ROLLING_HILLS), HIGHLANDS(TFCLayers.HIGHLANDS),
        BADLANDS(TFCLayers.BADLANDS), PLATEAU(TFCLayers.PLATEAU),
        PLATEAU_WIDE(TFCLayers.PLATEAU_WIDE), CANYONS(TFCLayers.CANYONS),
        MOUNTAINS(TFCLayers.MOUNTAINS), OLD_MOUNTAINS(TFCLayers.OLD_MOUNTAINS),
        OCEANIC_MOUNTAINS(TFCLayers.OCEANIC_MOUNTAINS),
        VOLCANIC_MOUNTAINS(TFCLayers.VOLCANIC_MOUNTAINS),
        VOLCANIC_OCEANIC_MOUNTAINS(TFCLayers.VOLCANIC_OCEANIC_MOUNTAINS),
        ICE_SHEET(TFCLayers.ICE_SHEET), ICE_SHEET_MOUNTAINS(TFCLayers.ICE_SHEET_MOUNTAINS),
        ICE_SHEET_OCEANIC(TFCLayers.ICE_SHEET_OCEANIC),
        ICE_SHEET_OCEANIC_MOUNTAINS(TFCLayers.ICE_SHEET_OCEANIC_MOUNTAINS),
        ICE_SHEET_TUYAS(TFCLayers.ICE_SHEET_TUYAS), ICE_SHEET_EDGE(TFCLayers.ICE_SHEET_EDGE),
        PATTERNED_GROUND(TFCLayers.PATTERNED_GROUND),
        INVERTED_PATTERNED_GROUND(TFCLayers.INVERTED_PATTERNED_GROUND),
        KNOB_AND_KETTLE(TFCLayers.KNOB_AND_KETTLE), STONE_CIRCLES(TFCLayers.STONE_CIRCLES),
        DRUMLINS(TFCLayers.DRUMLINS), TUYAS(TFCLayers.TUYAS),
        DUNE_SEA(TFCLayers.DUNE_SEA), GRASSY_DUNES(TFCLayers.GRASSY_DUNES),
        SALT_FLATS(TFCLayers.SALT_FLATS), MUD_FLATS(TFCLayers.MUD_FLATS),
        BUTTES(TFCLayers.BUTTES), HOODOOS(TFCLayers.HOODOOS), MESAS(TFCLayers.MESAS),
        STAIR_STEP_CANYONS(TFCLayers.STAIR_STEP_CANYONS),
        WHORLED_CANYONS(TFCLayers.WHORLED_CANYONS), ROCKY_PLATEAU(TFCLayers.ROCKY_PLATEAU),
        GLACIATED_MOUNTAINS(TFCLayers.GLACIATED_MOUNTAINS),
        GLACIATED_OCEANIC_MOUNTAINS(TFCLayers.GLACIATED_OCEANIC_MOUNTAINS),
        GLACIALLY_CARVED_MOUNTAINS(TFCLayers.GLACIALLY_CARVED_MOUNTAINS),
        GLACIALLY_CARVED_OCEANIC_MOUNTAINS(TFCLayers.GLACIALLY_CARVED_OCEANIC_MOUNTAINS),
        DOLINE_PLAINS(TFCLayers.DOLINE_PLAINS), DOLINE_HILLS(TFCLayers.DOLINE_HILLS),
        DOLINE_ROLLING_HILLS(TFCLayers.DOLINE_ROLLING_HILLS),
        DOLINE_HIGHLANDS(TFCLayers.DOLINE_HIGHLANDS), DOLINE_PLATEAU(TFCLayers.DOLINE_PLATEAU),
        DOLINE_CANYONS(TFCLayers.DOLINE_CANYONS),
        CENOTE_PLAINS(TFCLayers.CENOTE_PLAINS), CENOTE_HILLS(TFCLayers.CENOTE_HILLS),
        CENOTE_ROLLING_HILLS(TFCLayers.CENOTE_ROLLING_HILLS),
        CENOTE_HIGHLANDS(TFCLayers.CENOTE_HIGHLANDS), CENOTE_PLATEAU(TFCLayers.CENOTE_PLATEAU),
        CENOTE_CANYONS(TFCLayers.CENOTE_CANYONS),
        BURREN_PLAINS(TFCLayers.BURREN_PLAINS), BURREN_BADLANDS(TFCLayers.BURREN_BADLANDS),
        BURREN_BADLANDS_TALL(TFCLayers.BURREN_BADLANDS_TALL),
        BURREN_PLATEAU(TFCLayers.BURREN_PLATEAU),
        BURREN_ROCHE_MOUTONEE(TFCLayers.BURREN_ROCHE_MOUTONEE),
        SHILIN_PLAINS(TFCLayers.SHILIN_PLAINS), SHILIN_HILLS(TFCLayers.SHILIN_HILLS),
        SHILIN_HIGHLANDS(TFCLayers.SHILIN_HIGHLANDS), SHILIN_PLATEAU(TFCLayers.SHILIN_PLATEAU),
        SHILIN_CANYONS(TFCLayers.SHILIN_CANYONS),
        TOWER_KARST_PLAINS(TFCLayers.TOWER_KARST_PLAINS),
        TOWER_KARST_HILLS(TFCLayers.TOWER_KARST_HILLS),
        TOWER_KARST_HIGHLANDS(TFCLayers.TOWER_KARST_HIGHLANDS),
        TOWER_KARST_CANYONS(TFCLayers.TOWER_KARST_CANYONS),
        TOWER_KARST_LAKE(TFCLayers.TOWER_KARST_LAKE), TOWER_KARST_BAY(TFCLayers.TOWER_KARST_BAY),
        EXTREME_DOLINE_PLATEAU(TFCLayers.EXTREME_DOLINE_PLATEAU),
        EXTREME_DOLINE_MOUNTAINS(TFCLayers.EXTREME_DOLINE_MOUNTAINS),
        SALT_MARSH(TFCLayers.SALT_MARSH), GUANO_ISLAND(TFCLayers.GUANO_ISLAND),
        ACTIVE_SHIELD_VOLCANO(TFCLayers.ACTIVE_SHIELD_VOLCANO),
        DORMANT_SHIELD_VOLCANO(TFCLayers.DORMANT_SHIELD_VOLCANO),
        EXTINCT_SHIELD_VOLCANO(TFCLayers.EXTINCT_SHIELD_VOLCANO),
        ANCIENT_SHIELD_VOLCANO(TFCLayers.ANCIENT_SHIELD_VOLCANO),
        SUNKEN_SHIELD_VOLCANO(TFCLayers.SUNKEN_SHIELD_VOLCANO),
        ICE_SHEET_SHIELD_VOLCANO(TFCLayers.ICE_SHEET_SHIELD_VOLCANO),
        GLACIATED_SHIELD_VOLCANO(TFCLayers.GLACIATED_SHIELD_VOLCANO);

        private final int id;

        Layer(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }
    }
}
