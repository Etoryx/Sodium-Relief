<img src="assets/logo/sodium-relief-logo-100.png" width="80" height="80" alt="" />

# Sodium Relief

A client-side Fabric mod that reduces redundant tooltip work and smooths hovering in inventories, chests, and other container screens. It complements Sodium rather than replacing it, and does not aim to improve open-world FPS.

[Modrinth](https://modrinth.com/mod/sodium-relief) · [GitHub Releases](https://github.com/Etoryx/Sodium-Relief/releases) · [Report an issue](https://github.com/Etoryx/Sodium-Relief/issues)

## Installation

1. Install Fabric Loader and Fabric API for your Minecraft version. Sodium is recommended, but not required. Mod Menu is optional.
2. Download the Sodium Relief JAR matching your Minecraft version from Modrinth or GitHub Releases.
3. Put the JAR in your client instance's `mods/` folder. Remove the old Sodium Relief JAR when upgrading. There is nothing to install on the server.

All builds below are for **Fabric**. Use only one Sodium Relief JAR per instance. In the filenames, `<version>` is the Sodium Relief version.

| Minecraft | Java | JAR |
| --- | ---: | --- |
| 26.3 | 25 | `sodiumrelief-mc263x-<version>.jar` |
| 26.2 | 25 | `sodiumrelief-mc262x-<version>.jar` |
| 26.1.x | 25 | `sodiumrelief-mc261x-<version>.jar` |
| 1.21.11 | 21 | `sodiumrelief-mc12111-<version>.jar` |
| 1.21.9–1.21.10 | 21 | `sodiumrelief-mc12110-<version>.jar` |
| 1.21.6–1.21.8 | 21 | `sodiumrelief-mc1218-<version>.jar` |
| 1.21.5 | 21 | `sodiumrelief-mc1215-<version>.jar` |
| 1.21.4 | 21 | `sodiumrelief-mc1214-<version>.jar` |
| 1.21.2–1.21.3 | 21 | `sodiumrelief-mc1213-<version>.jar` |
| 1.21–1.21.1 | 21 | `sodiumrelief-mc1211-<version>.jar` |

The 26.3 build was tested with Sodium 0.9.3. Minecraft 26.4 snapshots and pre-releases are not supported.

## What It Optimizes

- **Tooltip caching:** reuses prepared tooltip lines when the hovered item and its tracked tooltip inputs have not changed.
- **Hover smoothing:** avoids redundant work as the pointer moves across inventory slots.
- **Text width caching:** reuses measurements for repeated text, such as item stack counts.

### Enchantment Descriptions

With `require_keybind` enabled, the description depends on whether Shift is held, even when the item has not changed. In Sodium Relief 1.3.1, enchanted items and enchanted books bypass tooltip reuse when Enchantment Descriptions is installed. Unenchanted tooltips and the text width cache are unaffected.

If you previously disabled `enableTooltipLayoutCache` to work around this issue, you can turn it back on. Leaving it off disables both cached and fallback tooltip reuse.

## Configuration

Open Sodium Relief through **Mod Menu**, the **Sodium Relief** button on the vanilla **Options** screen when Mod Menu is absent, or Sodium's options page on **Minecraft 1.21.11**. The UI is available in English, Russian, and Ukrainian.

The config file is `config/sodiumrelief.json`. Choose **Export Benchmark** in the config screen to write a JSON snapshot to `config/benchmarks/sodiumrelief/`.

## Measured Results

The 1.3.1 client game test opened a real inventory in **Minecraft 26.3** with **Sodium 0.9.3** and **Enchantment Descriptions 26.3.0.2**. It counted actual tooltip builds on the optimized path.

| Scenario | Tooltip requests | Fresh builds | Reused | Reuse rate |
| --- | ---: | ---: | ---: | ---: |
| Hover over one item | 2,417 | 1 | 2,416 | 99.96% |
| Scan 36 slots, three passes | 1,281 | 35 | 1,246 | 97.27% |

These are reuse rates from one run, not FPS gains or CPU savings. Request counts vary with frame rate. A separate regression test verified that holding and releasing Shift updates the description on an enchanted sword and book without moving the cursor, while the cache remains enabled.

## Build and Test

Building all targets requires JDK 21 and JDK 25. Point `org.gradle.java.installations.paths` in `gradle.properties` at your local JDKs, then run:

```sh
./gradlew test assemble
./gradlew :sodium-relief-mc263x:runClientGameTest -PtestEnchantmentDescriptions
```

On Windows, use `.\gradlew.bat` in place of `./gradlew`. The first command builds the versioned JARs under each module's `build/libs/`. The second runs the inventory benchmark and the Shift regression in a real client.

## License

[MIT](LICENSE). Sodium Relief is an independent mod and is not affiliated with or endorsed by [CaffeineMC](https://github.com/CaffeineMC) or the Sodium project. It does not include or modify Sodium's code.
