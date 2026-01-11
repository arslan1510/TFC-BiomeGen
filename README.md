# TFC-BiomeGen

A mod for adding custom biomes to TerraFirmaCraft using config + datapacks.

This mod is intentionally split into three parts:


- **TFC integration** (BiomeExtension: terrain/surface/river blend): provided via **config** at `config/tfcbiomegen/biomes/<name>.json`
- **Placement rules** (where the biome spawns): provided via **datapack** at `data/<namespace>/tfcbiomegen/biome_placement/<path>.json`
- **Biome content** (vanilla biome JSON): provided via **datapack** at `data/<namespace>/worldgen/biome/<name>.json`

1. Add the TFC extension config (config): `config/tfcbiomegen/biomes/<name>.json`
2. Add the placement rule (datapack): `data/<namespace>/tfcbiomegen/biome_placement/<path>.json`
3. Add the biome JSON (datapack): `data/<namespace>/worldgen/biome/<name>.json`
4. Restart Minecraft (**required**) (extensions load at startup)

**Example (extension config):** `config/tfcbiomegen/biomes/tropical_savanna.json`
```json
{
  "heightmap": "plains",
  "surface": "normal",
  "river_blend": "floodplain",
  "spawnable": true
}
```

**Example (placement rule):** `data/tfcbiomegen/tfcbiomegen/biome_placement/tropical_savanna.json` (targets `tfcbiomegen:tropical_savanna` by default)

```json
{
  "replaces_layer": "plains",
  "temperature": {"min": 18.0, "max": 28.0},
  "rainfall": {"min": 100.0, "max": 250.0},
  "rarity": 0.8
}
```

**Example (biome JSON):** `data/tfcbiomegen/worldgen/biome/tropical_savanna.json`
```json
{
  "temperature": 1.2,
  "downfall": 0.3,
  "effects": {
    "fog_color": 12638463,
    "sky_color": 7907327,
    "water_color": 4159204,
    "water_fog_color": 329011,
    "grass_color_modifier": "none"
  },
  "spawners": {},
  "spawn_costs": {},
  "carvers": {
    "air": [
      "tfc:cave",
      "tfc:canyon"
    ]
  },
  "features": [
    [],
    [],
    [],
    [
      "#tfc:in_biome/veins"
    ],
    [],
    [],
    [],
    [
      "#tfc:in_biome/surface_structures"
    ],
    [],
    [
      "#tfc:in_biome/plant_decoration"
    ],
    [
      "#tfc:in_biome/top_layer_modification"
    ]
  ]
}
```

**Important:** the biome id is derived from the config filename:
- `config/tfcbiomegen/biomes/tropical_savanna.json` → `tfcbiomegen:tropical_savanna`
- If other mods use the `tfcbiomegen` namespace, use a unique filename or your own namespace (e.g. `config/mypack/biomes/savanna.json` → `mypack:savanna`)

**Hard limitation:** TFC biome layers are backed by a fixed-size array. If too many mods/packs add biomes, you can hit the **128 total layer ID** limit and extra biomes will not be placed.

### TFC references
Biome JSON (`data/<namespace>/worldgen/biome/*.json`) can (and usually should) reference TFC’s existing carvers/features via IDs and tags.

- **carvers**: `data/tfc/worldgen/configured_carver/` (e.g. `tfc:cave`, `tfc:canyon`)
- **placed-feature tags**: `data/tfc/tags/worldgen/placed_feature/in_biome/` (e.g. `#tfc:in_biome/veins`)
- **examples**: `data/tfc/worldgen/biome/*.json`

TFC docs: [TerraFirmaCraft documentation](https://terrafirmacraft.github.io/Documentation/).

---

## Config + Data

### Extension config (`config/tfcbiomegen/biomes/<name>.json`)
- **`heightmap`**: string (required)
- **`surface`**: string (required)
- **`river_blend`**: string (required)
- **`spawnable`**: boolean (optional, default `false`)

### Placement rule (`data/<namespace>/tfcbiomegen/biome_placement/<path>.json`)
- **`biome`**: string (optional) explicit biome id to place; default is the file id (`<namespace>:<path>`)
- **`replaces_layer`**: string (optional) only replace a specific TFC layer (e.g. `plains`)
- **`temperature`**: `{ "min": float, "max": float }` (optional)
- **`rainfall`**: `{ "min": float, "max": float }` (optional)
- **`rarity`**: float in `[0.0, 1.0]` (optional, default `0.01`)

### Allowed values (enums / parsers)
Parsing is **case-insensitive**. Heightmaps and layers also accept an optional `tfc:` prefix.

- **`heightmap`**: selects a TFC terrain noise factory.
  - TFC collections of biome noise factories in `net.dries007.tfc.world.biome.BiomeNoise`.
  - This mod exposes a curated subset via `BiomeParsers.parseHeightmap`: `plains`, `hills`, `lowlands`, `low_canyons`, `rolling_hills`, `highlands`, `badlands`, `plateau`, `canyons`, `mountains`, `old_mountains`, `oceanic_mountains`, `dunes`, `flats`, `salt_flats`.

- **`river_blend`**: selects a TFC river valley/blend shape.
  - Values map to `net.dries007.tfc.world.river.RiverBlendType`: `floodplain`, `banked`, `tall_banked`, `wide`, `wide_deep`, `canyon`, `tall_canyon`, `talus`, `terraces`, `cave`.

- **`surface`**: selects a TFC surface builder factory (top blocks + shore/ice/badlands patterns).
  - Values map to `BiomeEnums.Surface` (backed by TFC `SurfaceBuilderFactory`). See `src/main/java/com/concinnity/tfcbiomegen/api/BiomeEnums.java`.

- **`replaces_layer`**: filters placement to positions where TFC selected a specific layer id in `ChooseBiomes`.
  - Values map to `BiomeEnums.Layer` (backed by TFC `TFCLayers`). See `src/main/java/com/concinnity/tfcbiomegen/api/BiomeEnums.java`.

---

## For Mod Developers

### Java API

```java
// In the mod constructor
BiomeAPI.init(modEventBus, "yourmodid");

// Register a biome
var biome = BiomeAPI.registerBiome(
    "yourmodid", "custom_biome",
    BiomeAPI.parseHeightmap("mountains"),  // Or custom: seed -> BiomeNoise.mountains(seed, 10, 70)
    BiomeEnums.Surface.NORMAL,
    BiomeEnums.RiverBlend.FLOODPLAIN,
    true  // spawnable
);

// Place it in the world
BiomeAPI.place(biome)
    .replaces(BiomeEnums.Layer.PLAINS)
    .temperature(15f, 25f)
    .rainfall(200f, 400f)
    .rarity(0.02f)
    .register();
```

**Mod developer convention:** mods typically use their own namespace (e.g. `examplemod`).

The mod provides the biome JSON at `data/examplemod/worldgen/biome/<name>.json`, and the extension is registered as `examplemod:<name>` via `BiomeAPI.registerBiome(...)`.

If modpack authors need to tweak placement without code, they can ship datapack rules at `data/examplemod/tfcbiomegen/biome_placement/<path>.json`.

**Important (TFC requirement):** the biome ID and the `BiomeExtension` ID must match.

- Biome: `yourmodid:custom_biome` (datapack JSON at `data/yourmodid/worldgen/biome/custom_biome.json`)
- BiomeExtension: `yourmodid:custom_biome` (registered via this API)

**Helper Methods:**
- `BiomeAPI.parseHeightmap(String)` - Get TFC heightmap by name
- `BiomeAPI.parseSurface(String)` - Get surface enum from name
- `BiomeAPI.parseRiverBlend(String)` - Get river blend from name
- `BiomeAPI.parseLayer(String)` - Get layer enum from name

---

## Technical Details

### How It Works

1. **Registration**: Biomes are registered to TFC's `BiomeExtension` registry using TFC's `BiomeBuilder`
2. **Layer Allocation**: TFC assigns each biome a unique integer layer ID
3. **TFC Worldgen**: TFC generates climate data (temp/rainfall) and picks base biomes via `ChooseBiomes`
4. **Injection**: Mixin runs **after** TFC, applies your placement rules, replaces matching points
5. **Result**: Custom biomes integrated into TFC worldgen

### Climate Filter Logic

For each world position, your biome spawns if:
1. `replaces_layer` matches (if specified) **AND**
2. Temperature is within your range **AND**
3. Rainfall is within your range **AND**
4. Random roll < `rarity`

**Example:** `rarity: 0.05` = 5% chance to replace matching positions.

### Rule priority
If multiple rules match the same position, **later rules override earlier ones** (rules are evaluated in registration order).

---

## Requirements

- Minecraft **1.21.1**
- NeoForge **21.1.218+**
- TerraFirmaCraft **4.0.0+**

---

## License

MIT
