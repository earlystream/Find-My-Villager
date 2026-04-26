# Trade Compass

A client-side villager trade finder for searchable trading halls.

Trade Compass remembers villager and wandering trader offers after the player opens the normal vanilla trade screen. Press `V` to search trades already seen in the current world or server, select a result, and use the HUD distance hint to return to the last known merchant location.

## Builds

- `versions/1.21.11`: Minecraft `1.21.11`, Loom `1.14.10`, Java 21 bytecode, Fabric Loader `>=0.18.1`
- `versions/26.1`: Minecraft `26.1`, unobfuscated Loom `1.15.5`, Java 25 bytecode, Fabric Loader `>=0.18.4`
- `versions/26.1.1`: Minecraft `26.1.1`, unobfuscated Loom `1.15.5`, Java 25 bytecode, Fabric Loader `>=0.18.4`
- `versions/26.1.2`: Minecraft `26.1.2`, unobfuscated Loom `1.15.5`, Java 25 bytecode, Fabric Loader `>=0.18.4`
- `versions/26.1.x`: compatibility-range build for Minecraft `>=26.1.1 <=26.1.2`

Run all targets:

```sh
./gradlew build
```

Output jars are written under each version folder's `build/libs` directory.

## Scope

This is a client-only quality-of-life mod. It stores local JSON data under `config/tradecompass/worlds/`, does not require server installation, does not send custom packets, and does not modify or automate trades.
