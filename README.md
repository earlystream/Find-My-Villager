<p align="center">
  <a href="https://modrinth.com/user/earlystream>
    <img
      src="https://cdn.modrinth.com/data/cached_images/2df5ae65196aa7a4a0aef20e208c0005ff06471f.png"
      alt="Modrinth profile"
      style="image-rendering: pixelate
    />
  </a>

  <a href="https://github.com/earlystream">
    <img
      src="https://cdn.modrinth.com/data/cached_images/14bb5f6380dbf0e9a0bc20179ef4d9728b0f88d9.png"
      alt="GitHub profile"
      style="image-rendering: pixelated;"
    />
  </a>

  <a href="https://www.curseforge.com/minecraft/mc-mods/find-my-villager">
    <img
      src="https://cdn.modrinth.com/data/cached_images/46f03a0afcdb64ea549e111a7a87e2bbc00f24c1_0.webp"
      alt="CurseForge profile"
      width="60"
      height="64"
      style="image-rendering: pixelated;"
    />
  </a>
</p>

# Find My Villager

**Ctrl+F for your trading hall.** Find any villager trade you've seen before, navigate straight to them, and never lose a good librarian again.

---

## What It Does

Every time you open a villager's trade screen, Find My Villager quietly records their offers for the current world or server. Later, press **V** to open the search UI and type an item name, enchantment, profession, villager name, or other saved detail.

Pick a result, and a HUD compass points you straight to that villager with a live distance readout. The targeted villager glows the moment they're visible.

No commands. No markers to place. No server install. Just open villagers as normal and the mod does the rest.

---

## Features

- **Search your trading hall** by item name, enchantment, profession, villager name, or location
- **Target a specific trade** — not just the villager, the exact offer you want
- **Live compass + distance** on your HUD pointing to the villager
- **Glow highlight** for the targeted villager through walls and crowds
- **Stock indicator** showing whether each trade is available or sold out
- **Favorite villagers** with star icons and favorite-first sorting
- **Custom villager names** so important traders are easier to recognize
- **Villager detail panel** with trades, level progress, location, and last-seen status
- **Delete or Clear All** to remove saved villagers from the active database
- **Client-side only** — works on servers without requiring a server-side mod

---

## Data Tools

Find My Villager includes a Data Manager screen for local saved databases.

- **Export JSON** for the current world/server database
- **Import JSON** with validation and safe merge behavior
- **Automatic backup before import**
- **Backup Now** for manual snapshots
- **Database stats** for villagers, trades, favorites, known databases, and last backup
- **Reset Current Database** with confirmation
- **Safer writes** using temporary files before replacing saved data

Backups, exports, and imports are stored under the mod's local config data folder.

---

## How To Use

1. Open any villager or wandering trader trade screen as you normally would.
2. Press **V** to open Find My Villager.
3. Search for an item, enchantment, profession, or villager name.
4. Click a trade card to target that villager.
5. Follow the compass on your HUD.
6. Click the star icon to favorite important villagers.
7. Open **Data** to export, import, back up, or reset the active database.

---

## Client-Side & Privacy

This mod stores data locally in `config/tradecompass/`.

World/server databases are stored under:

```text
config/tradecompass/worlds/
```

The mod does not send packets, does not require a server-side install, and does not automate or modify trades.

---

## Supported Versions

| Minecraft | Fabric | Forge | NeoForge |
|-----------|--------|-------|----------|
| 1.21.1    | Yes    | Yes   | Yes      |
| 1.21.4    | Yes    | Yes   | No       |
| 1.21.8    | Yes    | Yes   | No       |
| 1.21.11   | Yes    | Yes   | Yes      |
| 26.1      | Yes    | No    | No       |
| 26.1.1    | Yes    | Yes   | No       |
| 26.1.2    | Yes    | Yes   | No       |
| 26.1.x    | Yes    | No    | No       |

---

## Building

Build individual version folders with their Gradle wrapper or with the root wrapper when the included builds are available.

Fabric source folders are under `versions/`.
Forge source folders are under `forgeversions/`.

Output JARs are written to each version's `build/libs/` directory.

---

## License

[MPL-2.0](LICENSE)
