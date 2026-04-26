<p align="center">
  <a href="https://modrinth.com/user/earlystream">
    <img
      src="https://cdn.modrinth.com/data/cached_images/2df5ae65196aa7a4a0aef20e208c0005ff06471f.png"
      alt="Modrinth profile"
      style="image-rendering: pixelated;"
    />
  </a>

  <a href="https://github.com/earlystream">
    <img
      src="https://cdn.modrinth.com/data/cached_images/14bb5f6380dbf0e9a0bc20179ef4d9728b0f88d9.png"
      alt="GitHub profile"
      style="image-rendering: pixelated;"
    />
  </a>
</p>


# Find My Villager

**Ctrl+F for your trading hall.** Find any villager trade you've seen before — search by item, navigate to the villager, and never lose a good trade again.

---

## How it works

1. Open any villager or wandering trader's trade screen as normal — Find My Villager silently records their offers.
2. Press **V** to open the search UI.
3. Type what you're looking for (`mending`, `diamond`, `silk`, anything).
4. Click a trade to set it as your target — a HUD compass shows the direction and distance to that villager.
5. The targeted villager glows so you can spot them the moment they're on screen.

Your last selected villager and trade are remembered when you close and reopen the UI.

---

## Features

- **Instant search** across every villager you've ever opened in the current world
- **Per-trade targeting** — select the exact trade you want, not just the villager
- **Live position tracking** — the compass follows the villager as they move, not just their workstation
- **Glow highlight** on the targeted villager
- **Stock status** — see at a glance whether a trade is in stock or sold out
- **Delete / Clear All** with confirmation — remove individual villagers or wipe the whole list
- **Client-side only** — no server mod required, no packets sent, no trades automated
- Saves data locally under `config/tradecompass/worlds/`

---

## Supported versions

| Minecraft | Loader |
|-----------|--------|
| 1.21.11   | Fabric |
| 26.1      | Fabric |
| 26.1.1    | Fabric |
| 26.1.2    | Fabric |

---

## Building

```sh
./gradlew build
```

Output JARs are written to each version's `build/libs/` directory.

| Version folder | Minecraft | Java |
|----------------|-----------|------|
| `versions/1.21.11` | 1.21.11 | 21 |
| `versions/26.1`    | 26.1    | 25 |
| `versions/26.1.1`  | 26.1.1  | 25 |
| `versions/26.1.2`  | 26.1.2  | 25 |
| `versions/26.1.x`  | >=26.1.1 <=26.1.2 (range build) | 25 |

---

## License

[MPL-2.0](LICENSE)
