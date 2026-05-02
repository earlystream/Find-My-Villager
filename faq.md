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

# Find My Villager FAQ

Common questions about Find My Villager, saved data, multiplayer support, file names, backups, and compatibility.

---

## Why are some files still named `tradecompass`?

Find My Villager was originally called **Trade Compass**.

Some older JAR names, config folders, internal paths, or file names may still use:

```text
tradecompass
```

This is expected. The file name does not affect the mod.

The old folder name is also kept to avoid breaking existing saved databases.

---

## Is this mod client-side only?

Yes.

Find My Villager is client-side only. You can use it without the server installing the mod.

---

## Does it work on multiplayer servers?

Yes.

The mod can work on multiplayer servers because it runs on the client. The server does not need to install anything.

As usual, only use client-side mods that are allowed by the server you play on.

---

## Does it change villager trades?

No.

Find My Villager does not change villager trades, prices, stock, professions, AI, or server behavior.

It only saves trade information you have already seen.

---

## Does it automate trading?

No.

The mod does not auto-buy, auto-sell, reroll trades, restock villagers, or interact with villagers for you.

It is a search and navigation tool, not an automation mod.

---

## Does it send data to a server?

No.

Saved villager data stays on your computer.

Find My Villager does not upload your saved databases.

---

## Where is my data stored?

Saved data is stored locally under:

```text
config/tradecompass/
```

World and server databases are stored under:

```text
config/tradecompass/worlds/
```

The folder is still called `tradecompass` for compatibility with older versions.

---

## How does the mod know a villager's trades?

The mod saves trade data when you open a villager or wandering trader trade screen.

If you have never opened that villager before, the mod will not know their trades yet.

---

## Can I search for enchanted books?

Yes.

You can search by enchantment name, item name, profession, villager name, location, and other saved trade details.

For example, you can search for things like:

```text
Mending
Protection
Efficiency
Silk Touch
Unbreaking
```

---

## Can I target one specific trade?

Yes.

The mod is designed to help you find the specific saved trade you want, not just the villager.

After selecting a result, the HUD compass points you back to that villager.

---

## What does the glow highlight do?

When the targeted villager is visible, they glow so you can spot them more easily.

This helps in crowded trading halls where many villagers have the same profession outfit.

---

## Can I favorite villagers?

Yes.

You can star important villagers as favorites.

Favorites are easier to find later and can be sorted ahead of normal saved villagers.

---

## Can I give villagers custom names?

Yes.

Saved villagers can have custom names.

This is useful for labels like:

```text
Mending Librarian
Cheap Farmer
Armor Smith
Raid Emerald Trader
```

Custom names make large trading halls much easier to manage.

---

## Does it support name-tagged villagers?

Yes, the mod is intended to help identify saved villagers using names where available.

You can also use custom saved names for your own organization.

---

## Does it support wandering traders?

Yes.

Wandering trader trade screens can also be read and saved when opened.

---

## Can I export my database?

Yes.

The Data Manager lets you export the active database as JSON.

This is useful for backups, moving instances, or keeping a copy before testing new versions.

---

## Can I import a database?

Yes.

The Data Manager supports JSON imports with validation and safer merge behavior.

The mod creates a backup before importing so you have a recovery point if something goes wrong.

---

## Can I manually back up my data?

Yes.

You can use **Backup Now** in the Data Manager, or copy the local config folder manually.

The main folder is:

```text
config/tradecompass/
```

---

## Can I move my data to another launcher or instance?

Yes.

Use the export/import tools, or manually copy the relevant database files from:

```text
config/tradecompass/worlds/
```

Using the in-game Data Manager is safer because it validates imports and creates backups.

---

## Can I reset only one world or server database?

Yes.

The Data Manager has a reset option for the current active database.

It asks for confirmation first.

---

## Why does the config folder still say `tradecompass`?

Because the mod used to be called **Trade Compass**.

Keeping the old folder name helps prevent older saved databases from breaking after the rename.

---

## Does this require commands?

No.

You do not need commands.

Open villagers normally, then press **V** when you want to search.

---

## Does this work without placing markers?

Yes.

You do not need to place markers manually.

The mod remembers villagers after you open their trade screen.

---

## What key opens the search screen?

By default, the search screen opens with:

```text
V
```

You can change the keybind in Minecraft's controls menu if needed.

---

## Why is a villager missing from search?

Usually, it means you have not opened that villager's trade screen yet on the current world or server database.

Open the villager once, then search again.

---

## Why is the location wrong?

Villagers can move, be transported, die, or be replaced.

The mod saves the last known location based on the data it has seen. If the villager moved after that, you may need to open them again so the saved data updates.

---

## Does this work across different worlds?

Databases are separated by world or server.

This prevents trades from one world mixing with another.

---

## Does this work across different servers?

Yes, but each server database is stored separately.

This keeps server data from being mixed together.

---

## Is this a cheat?

Find My Villager does not automate gameplay, reveal unknown trades, modify server behavior, or interact with villagers for you.

It only helps you search trades you have already opened and seen.

Server rules can still vary, so check the rules of the server you play on.

---

## What license does the project use?

Find My Villager is licensed under **MPL-2.0**.

See:

```text
LICENSE
```
