# TFC-BiomeGen

Adds **custom biomes** to TerraFirmaCraft

## Quickstart (modpacks)

1. **Config (extension + placement)**: add `config/tfcbiomegen/biomes/<name>.json`
2. **Biome JSON must exist**: add `data/<namespace>/worldgen/biome/<name>.json`


### Example config (`config/tfcbiomegen/biomes/tropical_savanna.json`)

```json
{
  "heightmap": "plains",
  "surface": "normal",
  "river_blend": "floodplain",
  "spawnable": true,

  "replaces_layer": "plains",
  "temperature": {"min": 18.0, "max": 28.0},
  "rainfall": {"min": 100.0, "max": 250.0},
  "rarity": 0.8
}
```

## Where do I put datapack files (`data/<namespace>/...`)?

Rule of thumb: **the biome id determines the `data/<namespace>/...` folder**.

- **If you’re using this mod’s config (modpacks)**:
  - Config filename `config/tfcbiomegen/biomes/foo.json` registers biome id **`tfcbiomegen:foo`**
  - So your biome JSON goes in: `data/tfcbiomegen/worldgen/biome/foo.json`
- **If you’re a mod adding a biome via Java API**:
  - If you register biome id **`yourmodid:custom_biome`**
  - Put biome JSON in: `data/yourmodid/worldgen/biome/custom_biome.json`
- **Do NOT put your new biome JSON under `data/tfc/...`**
  - `data/tfc/...` is for TerraFirmaCraft’s own resources, and only makes sense if you intentionally want to **override TFC’s datapack content**.

## Config reference (`config/tfcbiomegen/biomes/*.json`)

### Fields
- **`heightmap`** (required): string
- **`surface`** (required): string
- **`river_blend`** (required): string
- **`spawnable`** (optional, default `false`): boolean

- **`replaces_layer`** (optional): string
- **`temperature`** (optional): `{ "min": float, "max": float }` (default `-20..30`)
- **`rainfall`** (optional): `{ "min": float, "max": float }` (default `0..500`)
- **`rarity`** (optional, default `0.01`): float in `[0.0, 1.0]`

### Allowed values / parsing
- **General**: parsing is **case-insensitive**.
- **`heightmap`**: accepts these names (also accepts an optional `tfc:` prefix):
  - `plains`, `hills`, `lowlands`, `low_canyons`, `rolling_hills`, `highlands`, `badlands`, `plateau`, `canyons`, `mountains`, `old_mountains`, `oceanic_mountains`, `dunes`, `flats`, `salt_flats`

- **`river_blend`**: maps to TFC river blend types:
  - `floodplain`, `banked`, `tall_banked`, `wide`, `wide_deep`, `canyon`, `tall_canyon`, `talus`, `terraces`, `cave`
  
- **`surface`**: uses `BiomeEnums.Surface` enum names (see `src/main/java/com/concinnity/tfcbiomegen/api/BiomeEnums.java`)
  - Examples: `normal`, `rocky`, `lowlands`, `dunes`, `grassy_dunes`, `river`, `shore_sandy`, `ice_sheet_normal`, `badlands_normal`

- **`replaces_layer`**: uses `BiomeEnums.Layer` enum names; also accepts optional `tfc:` prefix
  - Examples: `plains`, `hills`, `lowlands`, `mountains`, `ocean`, `river`

### Biome id source
This mod uses the **config filename** as the biome id path:
- `config/tfcbiomegen/biomes/foo.json` → `tfcbiomegen:foo`

## Gotchas

- **Biome JSON is required**: if you register `tfcbiomegen:<name>` via config/API, you must also provide the biome at `data/tfcbiomegen/worldgen/biome/<name>.json` (or a datapack biome with the same id). Missing biome JSON can crash world creation with “Registry is already frozen … biome …”.
- **Restart required**: config is read at startup.
- **Hard limit**: TFC biome layers have a fixed **128 total layer ID** limit; exceeding it means extra biomes can’t be allocated/placed.

## Adding features / spawns / carvers (datapack biome JSON)

These live in your biome file at `data/<namespace>/worldgen/biome/<name>.json` (fields like `features`, `spawners`, `spawn_costs`, `carvers`).

Useful TFC reference folders (from the TFC datapack):
- Placed-feature tags: `data/tfc/tags/worldgen/placed_feature/in_biome/` (e.g. `#tfc:in_biome/veins`)
- Configured carvers: `data/tfc/worldgen/configured_carver/` (e.g. `tfc:cave`, `tfc:canyon`)
- Biome JSON examples: `data/tfc/worldgen/biome/*.json`

### Examples

Minimal biome JSON skeleton reusing TFC tags + carvers:

```json
{
  "has_precipitation": true,
  "temperature": 1.2,
  "downfall": 0.3,
  "effects": {
    "fog_color": 12638463,
    "sky_color": 7907327,
    "water_color": 4159204,
    "water_fog_color": 329011
  },
  "carvers": {
    "air": ["tfc:cave", "tfc:canyon"]
  },
  "features": [
    ["#tfc:in_biome/erosion"],
    ["#tfc:in_biome/all_lakes"],
    [],
    ["#tfc:in_biome/underground_structures"],
    ["#tfc:in_biome/surface_structures"],
    ["#tfc:in_biome/strongholds"],
    ["#tfc:in_biome/veins"],
    ["#tfc:in_biome/underground_decoration"],
    [],
    [],
    ["#tfc:in_biome/top_layer_modification"]
  ],
  "spawners": {},
  "spawn_costs": {}
}
```

## For Mod Developers (Java API)

```java
// In the mod constructor
BiomeAPI.init(modEventBus, "yourmodid");

// Register a biome extension (must match your biome JSON id)
var biome = BiomeAPI.registerBiome(
    "yourmodid", "custom_biome",
    BiomeAPI.parseHeightmap("mountains"),
    BiomeEnums.Surface.NORMAL,
    BiomeEnums.RiverBlend.FLOODPLAIN,
    true
);

// Place it (later rules override earlier ones if multiple match)
BiomeAPI.place(biome)
    .replaces(BiomeEnums.Layer.PLAINS)
    .temperature(15f, 25f)
    .rainfall(200f, 400f)
    .rarity(0.02f)
    .register();
```

**TFC requirement**: the biome id and the `BiomeExtension` id must match (e.g. `yourmodid:custom_biome` for both).

## Requirements

- Minecraft **1.21.1**
- TerraFirmaCraft **4.0.0+**

## License

MIT
