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

# Find My Villager

> **Ctrl+F for your trading hall.**  
> Find any villager trade you've seen before, navigate straight to them, and never lose a good librarian again.

---

## File Name Note

Some current JAR files may still be named:

```text
tradecompass-0.3.0+...jar
```

This is expected. The project was originally called **Trade Compass** before being renamed to **Find My Villager**.

The file name does **not** affect the mod.

---

## What It Does

**Find My Villager** remembers villager trades after you open their trade screen.

Once a villager has been seen, press **V** to open the search screen and look them up later by:

- Item name
- Enchantment
- Profession
- Villager name
- Location
- Saved trade details

When you select a result, the mod gives you a HUD compass with a live distance readout so you can walk straight back to that villager.

Once the villager is visible, they glow, making them easier to find inside busy trading halls.

No commands.  
No markers to place.  
No server install.  
Just open villagers normally and the mod keeps track of them locally.

---

## Why Use It?

Trading halls get messy fast.

You might remember that you had a **Mending** librarian somewhere, but not which villager it was. You might have dozens of farmers, armorers, clerics, or librarians packed into one area. You might also be playing on a server where you cannot use commands or server-side tools.

**Find My Villager** solves that problem without changing how villagers work.

You still discover trades normally.  
The mod just remembers what you have already seen and gives you a way to search it later.

---

## Main Features

### Trade Search

Search through saved villagers using real trade information.

You can search by:

- Item name
- Enchantment
- Profession
- Villager name
- Location
- Saved trade data

This is built for actual trading halls, not just small setups. You can look for the exact trade you want instead of clicking through every villager again.

---

### Villager Compass

After selecting a saved trade, a compass appears on your HUD and points toward that villager.

The compass includes a live distance readout, so you can quickly tell if you are getting closer or walking the wrong way.

---

### Glow Highlight

When the targeted villager is visible, they glow.

This helps you find the correct villager inside:

- Crowded trading halls
- Villager cells
- Market builds
- Underground halls
- Areas where many villagers share the same outfit

---

### Favorites

Important villagers can be marked as favorites.

Favorites are easier to find later and are sorted first, so your best traders do not get buried inside a large database.

Useful for villagers like:

- Mending librarians
- Cheap emerald farmers
- Important enchanted book sellers
- Max-level traders
- Rare or hard-to-replace villagers

---

### Custom Names

Saved villagers can have custom names.

This helps when you want to label important traders yourself, or when villagers already have names from name tags or other mods.

Example names:

```text
Mending Librarian
Cheap Farmer
Armor Smith
Raid Emerald Trader
```

---

### Trade Details

The villager detail screen shows more than just the villager location.

It can show:

- Saved trades
- Villager profession
- Villager level progress
- Trade stock status
- Location
- Last-seen information
- Favorite status
- Custom name

This makes it easier to check what a villager had without walking back to them first.

---

### Local Data Tools

Find My Villager includes a **Data Manager** screen for managing saved databases.

You can use it to:

- Export the current database
- Import another database
- Create backups
- View database stats
- Reset the current database
- Safely merge imported data

---

## Data Manager

The Data Manager is built for players who move worlds, switch instances, test versions, or just want backups.

Exported databases are saved as JSON.

Imports are validated before being applied, and the mod creates a backup before importing so you have a recovery point if something goes wrong.

### Data Manager Features

| Tool | What It Does |
| ---- | ------------ |
| **Export JSON** | Saves the current world or server database as a JSON file |
| **Import JSON** | Imports another database with validation |
| **Auto Backup** | Creates a backup before importing |
| **Backup Now** | Manually creates a backup |
| **Database Stats** | Shows saved villagers, trades, favorites, databases, and backup info |
| **Reset Current Database** | Clears only the active database after confirmation |
| **Safe Writes** | Uses temporary files before replacing saved data |

---

## How To Use

1. Open a villager or wandering trader trade screen like normal.
2. Press **V** to open Find My Villager.
3. Search for an item, enchantment, profession, or villager name.
4. Click the result you want to target.
5. Follow the HUD compass.
6. Use the glow highlight to identify the correct villager.
7. Star important villagers as favorites.
8. Open **Data** when you want to export, import, back up, or reset your saved database.

---

## Client-Side & Privacy

Find My Villager is **client-side only**.

It does not:

- Require a server-side install
- Send extra packets
- Upload your saved data
- Automate trades
- Modify trades
- Change villager behavior

Saved data is stored locally on your machine under:

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

### Source Folders

| Loader | Folder |
| ------ | ------ |
| Fabric | `versions/` |
| Forge | `forgeversions/` |
| NeoForge | `neoforge/` |

### Output Folder

Built JAR files are written to each version's:

```text
build/libs/
```

---

## Project Notes

Find My Villager was originally called **Trade Compass**.

Because of that, some older file names, internal folders, config paths, or JAR names may still use:

```text
tradecompass
```

This is normal and does not affect the mod.

---

## License

This project is licensed under the **MPL-2.0** license.

See:

```text
LICENSE
```
