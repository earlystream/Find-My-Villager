<p align="center">
  <a href="https://modrinth.com/mod/find-my-villager">
    <img
      src="https://cdn.modrinth.com/data/cached_images/2df5ae65196aa7a4a0aef20e208c0005ff06471f.png"
      alt="Modrinth"
      style="image-rendering: pixelated;"
    />
  </a>

  <a href="https://github.com/earlystream/Find-My-Villager/">
    <img
      src="https://cdn.modrinth.com/data/cached_images/14bb5f6380dbf0e9a0bc20179ef4d9728b0f88d9.png"
      alt="GitHub"
      style="image-rendering: pixelated;"
    />
  </a>

  <a href="https://www.curseforge.com/minecraft/mc-mods/find-my-villager">
    <img
      src="https://cdn.modrinth.com/data/cached_images/46f03a0afcdb64ea549e111a7a87e2bbc00f24c1_0.webp"
      alt="CurseForge"
      width="60"
      height="64"
      style="image-rendering: pixelated;"
    />
  </a>
</p>

## License

Versions 0.4.0 and newer are licensed under the **Earlystream Source-Available License v1.0**.

This project is source-available for transparency, portfolio review, security review, compatibility review, and educational reading.

You may use official builds for personal gameplay. You may not reupload, redistribute, rename, publish modified builds, copy substantial source code, or create competing clones without written permission.

Older versions released under MPL-2.0 remain under MPL-2.0.


# Find My Villager

**Ctrl+F for your trading hall.**  
Find any villager trade you've seen before, navigate straight to them, and never lose a good librarian again.

Find My Villager remembers trades after you open a villager or wandering trader trade screen. Later, press **V** to search through saved villagers by item, enchantment, profession, villager name, location, or saved trade details.

When you select a result, the mod points you back to that villager with a HUD compass and live distance readout. Once the villager is visible, they glow so you can find the correct one inside crowded trading halls.

No commands. No manual markers. No server install.  
Just open villagers normally and the mod keeps track of them locally.

> **Need more details?**  
> Read the [FAQ](https://github.com/earlystream/Find-My-Villager/blob/main/faq.md) for file names, privacy, multiplayer support, saved data, imports, backups, and common questions.

---

## What It Does

Trading halls get messy fast.

You might remember that you had a **Mending** librarian somewhere, but not which villager it was. You might have dozens of farmers, armorers, clerics, librarians, or wandering traders saved across different worlds and servers.

Find My Villager is made for that exact problem.

The mod does not change how villagers work. You still discover trades normally by opening trade screens. The mod simply remembers what you have already seen and gives you a clean way to search it later.

---

## Features

- Search saved villagers by **item**, **enchantment**, **profession**, **villager name**, **location**, or saved trade data
- Target a specific trade and follow a HUD compass back to the right villager
- See a live distance readout while walking toward the target
- Glow highlight for the selected villager when visible
- Favorite important villagers so they appear first
- Add custom names to saved villagers
- View trade details, villager level progress, stock status, location, and last-seen info
- Export, import, back up, reset, and manage local databases
- Works client-side without needing anything installed on the server

---

## How To Use

1. Open a villager or wandering trader trade screen.
2. Press **V** to open Find My Villager.
3. Search for an item, enchantment, profession, villager name, or location.
4. Click the trade or villager you want to target.
5. Follow the HUD compass.
6. Look for the glow highlight when the villager is visible.
7. Star important villagers as favorites if you want them easier to find later.

---

## Data Manager

Find My Villager includes a Data Manager screen for saved databases.

You can export your current world or server database as JSON, import another database, create backups, view database stats, reset the current database, and safely merge imported data.

This is useful if you switch launchers, move worlds, test multiple Minecraft versions, or just want a backup before changing anything.

---

## Client-Side & Privacy

Find My Villager is client-side only.

It does not upload your data, does not automate trades, does not modify villager behavior, and does not require a server-side install.

Saved data is stored locally in:

```text
config/tradecompass/
```

World and server databases are stored under:

```text
config/tradecompass/worlds/
```

The `tradecompass` folder name is kept for compatibility with older versions of the mod.

---

## Supported Versions

| Minecraft | Fabric | Forge | NeoForge |
| --------- | :----: | :---: | :------: |
| 1.21.1    | Yes    | Yes   | Yes      |
| 1.21.4    | Yes    | Yes   | Yes      |
| 1.21.8    | Yes    | Yes   | Yes      |
| 1.21.11   | Yes    | Yes   | Yes      |
| 26.1      | Yes    | No    | No       |
| 26.1.1    | Yes    | Yes   | Yes      |
| 26.1.2    | Yes    | Yes   | Yes      |
| 26.1.x    | Yes    | No    | No       |

---

## Building

Build individual version folders with their Gradle wrapper, or use the root wrapper when the included builds are available.

| Loader | Source Folder |
| ------ | ------------- |
| Fabric | `versions/` |
| Forge | `forgeversions/` |

Built JARs are written to each version's:

```text
build/libs/
```

---

## Project Notes

Find My Villager was originally called **Trade Compass**.

Because of that, some older JAR names, folders, config paths, or internal names may still use:

```text
tradecompass
```

This is normal and does not affect the mod.

---

## FAQ

Common questions are answered here:

[Read the FAQ](https://github.com/earlystream/Find-My-Villager/blob/main/faq.md)

---

## License

This project is licensed under **MPL-2.0**.

See:

```text
LICENSE
```
