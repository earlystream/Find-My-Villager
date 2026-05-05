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
      src="https://cdn.modrinth.com/data/cached_images/14bb5f6380dbf0e9a0bc20179ef4d9728b0f88d9a0bc20179ef4d9728b0f88d9.png"
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

Common questions about Find My Villager, saved data, multiplayer support, LAN and Essential sharing, online sharing, backups, compatibility, and licensing.

---

## What is Find My Villager?

Find My Villager is a client-side Minecraft mod for saving, searching, and finding villager trades you have already opened.

It is meant for trading halls, large villages, multiplayer bases, long-term worlds, and servers where you want to remember which villager has which trade.

You can search saved villagers by trade, item, enchantment, profession, name, custom name, favorite status, and location details.

---

## Why are some files still named `tradecompass`?

Find My Villager was originally called **Trade Compass**.

Some older JAR names, internal paths, class names, or file names may still use:

```text
tradecompass
```

This is expected. The old name does not mean you installed the wrong mod.

Older versions stored data under:

```text
config/tradecompass/
```

Newer versions, such as 0.4.0 and later, migrate the data folder to:

```text
config/find-my-villager/
```

If you updated from an older version, the mod should move existing saved data forward instead of starting from an empty database.

---

## Is this mod client-side only?

Yes.

Find My Villager is client-side only. You can use it without the server installing the mod.

The mod reads information from screens and client-side game state that your client already receives. It does not require a server plugin, datapack, command block, or server-side mod.

---

## Does the server need to install it?

No.

For normal use, only you need to install the mod.

Other players do not need the mod unless they also want to search their own saved villagers or use the online sharing features with you.

---

## Does it work on multiplayer servers?

Yes.

The mod can work on multiplayer servers because it runs on the client. The server does not need to install anything.

As usual, only use client-side mods that are allowed by the server you play on. Server rules can vary.

---

## Does it work in singleplayer?

Yes.

Singleplayer worlds are supported. The mod saves villager data locally for that world.

---

## Does it work on LAN worlds?

Yes.

The normal search features work in LAN worlds because the mod runs on your client.

Online invite sharing can also work in LAN or Essential-style sessions, but it is important to understand how that works:

- The villager database is still local to each player.
- The mod does not directly copy files over your LAN network.
- The online invite flow uses the official relay server to deliver the invite and temporary shared payload.
- The recipient still chooses whether to accept and preview/import the shared data.
- Copy Code / Paste Code can be used instead if you do not want to use the relay.

LAN sharing in this FAQ means sharing villager data while playing together in a LAN-style world. It does not mean the mod opens a direct LAN file-transfer server between players.

---

## Does it work with Essential-hosted worlds?

It is intended to work with Essential/LAN-style sessions.

Essential-hosted worlds can behave differently from dedicated servers because the host may see the world as a local singleplayer world while the joining player sees it as a multiplayer connection.

Find My Villager handles online invites by routing them to the selected recipient instead of relying only on the local world identity. This helps invites arrive even when the host and recipient see different world/session keys.

The shared villager payload still keeps the real database/world hash for safety checks during preview/import.

---

## Does it change villager trades?

No.

Find My Villager does not change villager trades, prices, stock, professions, AI, restocking, pathfinding, or server behavior.

It only saves trade information you have already seen.

---

## Does it automate trading?

No.

The mod does not auto-buy, auto-sell, reroll trades, restock villagers, move villagers, or interact with villagers for you.

It is a search and navigation tool, not an automation mod.

---

## Does it reveal unknown trades?

No.

The mod only knows trades after you open a villager or wandering trader trade screen.

If you have never opened that villager before, the mod will not know their trades yet.

---

## Does this require commands?

No.

You do not need commands.

Open villagers normally, then press the search key when you want to search.

---

## What key opens the search screen?

By default, the search screen opens with:

```text
V
```

You can change the keybind in Minecraft's controls menu.

---

## Does this work without placing markers?

Yes.

You do not need to place markers manually.

The mod remembers villagers after you open their trade screen.

---

## How does the mod know a villager's trades?

The mod saves trade data when you open a villager or wandering trader trade screen.

When the trade screen opens, the client can see the visible trade offers. Find My Villager stores the relevant details locally so you can search them later.

---

## What villager data can be saved?

Depending on what the mod can see and what features you use, saved records may include:

- villager or wandering trader identity
- profession
- profession id
- level
- visible trades
- input items
- output items
- enchanted book enchantments
- prices
- uses and max uses
- disabled trade state
- last known dimension
- last known coordinates
- detected name where available
- custom saved name
- favorite status
- notes, if you add them

The exact data available can depend on the Minecraft version, mod version, and what the client has seen.

---

## Can I search for enchanted books?

Yes.

You can search by enchantment name, item name, profession, villager name, location, and other saved trade details.

Examples:

```text
Mending
Protection
Efficiency
Silk Touch
Unbreaking
Fortune
Feather Falling
```

---

## Can I target one specific trade?

Yes.

The mod is designed to help you find the specific saved trade you want, not just the villager.

After selecting a result, the HUD compass points you back to that villager.

---

## What does the compass do?

The compass HUD points toward the selected saved villager's last known location.

This is useful when you know the trade exists but forgot where the villager is in a trading hall, village, base, or server build.

---

## What does the glow highlight do?

When the targeted villager is visible, they can glow so you can spot them more easily.

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

Examples:

```text
Mending Librarian
Cheap Farmer
Armor Smith
Raid Emerald Trader
Book Hall Row 3
```

Custom names make large trading halls easier to manage.

---

## Does it support name-tagged villagers?

Yes, the mod is intended to help identify saved villagers using names where available.

You can also use custom saved names for your own organization.

---

## Does it support wandering traders?

Yes.

Wandering trader trade screens can also be read and saved when opened.

---

## Why is a villager missing from search?

Usually, it means you have not opened that villager's trade screen yet on the current world or server database.

Open the villager once, then search again.

Other possible reasons:

- you are in a different world or server database
- the database was reset
- the data was imported into a different database
- the villager was never saved on this client
- the villager was saved before a version change and needs to be opened again
- the search filter is too specific

---

## Why is the location wrong?

Villagers can move, be transported, die, be replaced, or change dimension.

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

## Where is my data stored?

Newer versions, such as 0.4.0 and later, store saved data locally under:

```text
config/find-my-villager/
```

World and server databases are stored under:

```text
config/find-my-villager/worlds/
```

Older versions used:

```text
config/tradecompass/
```

If you updated from an older version, the mod should migrate existing data from the old `tradecompass` folder to the newer `find-my-villager` folder.

---

## Why did my config folder change from `tradecompass` to `find-my-villager`?

Find My Villager was originally called **Trade Compass**.

Older versions stored data under:

```text
config/tradecompass/
```

Newer versions, such as 0.4.0 and later, migrate the folder to:

```text
config/find-my-villager/
```

This rename matches the current mod name. The migration is intended to preserve existing saved databases, settings, backups, and world/server data.

If you still see `config/tradecompass/`, it may be left over from an older version, another instance, a backup, or files that were not removed after migration.

---

## Can I export my database?

Yes.

The Data Manager lets you export the active database as JSON.

This is useful for backups, moving instances, sharing data manually, or keeping a copy before testing new versions.

---

## Can I import a database?

Yes.

The Data Manager supports JSON imports with validation and safer merge behavior.

The mod creates a backup before importing so you have a recovery point if something goes wrong.

---

## Can I manually back up my data?

Yes.

You can use **Backup Now** in the Data Manager, or copy the local config folder manually.

For newer versions, such as 0.4.0 and later, the main folder is:

```text
config/find-my-villager/
```

Older versions used:

```text
config/tradecompass/
```

Using the in-game backup/export tools is safer because the mod knows which database is active.

---

## Can I move my data to another launcher or instance?

Yes.

Use the export/import tools, or manually copy the relevant database files.

For newer versions, such as 0.4.0 and later, world and server databases are stored under:

```text
config/find-my-villager/worlds/
```

Older versions used:

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

# Online Sharing

## Does Find My Villager send data to a server?

For normal local use, no.

Your saved villager databases stay on your computer.

However, Find My Villager includes an optional online sharing feature. If you choose to use online sharing, the mod connects to the official relay server:

```text
https://findmyvillagershare.earlystream.workers.dev
```

The relay is used to pass share invites and temporary shared villager payloads between players.

Normal villager searching works locally without online sharing.

---

## Is online sharing required?

No.

Online sharing is optional.

The main search, compass, glow highlight, favorites, custom names, import/export, and backup features work locally without using the online relay.

---

## What is online sharing for?

Online sharing lets one player send selected saved villager data to another player from inside the game.

It is useful when:

- one player scanned a trading hall and wants to share the saved trades
- a LAN/Essential host wants to send villager data to a friend
- a server player wants to share selected villager records with another player
- you want the recipient to preview selected data before importing
- you do not want to manually export and send JSON files

---

## Does online sharing automatically share my whole database?

No.

Online sharing is based on selected data.

The sender chooses what to share. The receiver still has to accept, download, preview, and choose how to import.

---

## Does the receiver import automatically?

No.

Receiving an invite does not immediately change the receiver's database.

The flow is:

1. Sender creates a share invite.
2. Receiver accepts the invite.
3. Sender uploads the selected payload.
4. Receiver downloads and previews the payload.
5. Receiver chooses how to import or merge it.

The receiver stays in control.

---

## What data is sent during online sharing?

When online sharing is used, the relay receives temporary invite metadata and, after the recipient accepts, the selected shared villager payload.

Depending on the sender's share settings, the payload may include selected villager records, trades, item names, enchantments, professions, custom names, notes, favorites, and locations.

The mod does **not** send raw Minecraft UUIDs in new share payloads. Player identity values are replaced with scoped SHA-256 hashes before being sent.

The relay is designed to avoid logging sensitive share contents such as player names, raw UUIDs, server addresses, world names, invite tokens, full payload contents, villager data, trade data, coordinates, item names, enchantments, prices, or professions.

---

## What is not sent during normal local use?

During normal searching, targeting, favoriting, naming, and local database use, the mod does not need the relay.

Your saved database remains local unless you export it, copy a share code, or use online sharing.

---

## How long does online sharing data stay on the relay?

Online share invites are temporary.

The relay stores invite/session data only long enough for the recipient to accept and download the shared payload. Shared payloads are deleted after download or when the session expires.

Online sharing is intended for short live transfers, not cloud backup or long-term storage.

---

## Is the relay a cloud database?

No.

The relay is not meant to permanently store your villager database.

It is a temporary delivery service for invite/session state and accepted share payloads.

For long-term backups, use export or Backup Now.

---

## How does online invite sharing work?

The basic flow is:

1. Both players install Find My Villager.
2. Both players enable Online Sharing.
3. The receiver enables Incoming Invites.
4. The sender selects villagers/trades to share.
5. The sender chooses the recipient.
6. The sender sends an invite.
7. The receiver sees the incoming invite.
8. The receiver accepts.
9. The sender uploads the selected payload.
10. The receiver downloads and previews it.
11. The receiver chooses how to import it.

The villager payload is not uploaded until the receiver accepts.

---

## How does LAN or Essential sharing work?

LAN and Essential-style sharing use the same online sharing system.

That means:

- The mod does not directly send files across the LAN.
- The mod does not require the LAN host to install a server plugin.
- The official relay carries the temporary invite and payload.
- The selected recipient receives the invite through the relay.
- The receiver previews/imports the data locally.

This matters because LAN and Essential sessions may not have the same world identity on both clients. The host may see the world as local singleplayer, while the joining player may see it as a multiplayer session.

Find My Villager routes online invites to the selected recipient so the invite can still arrive even when the host and receiver do not have the exact same local world key.

The actual shared payload still includes database identity information so the receiver can be warned when importing data that appears to come from a different world or server database.

---

## Does LAN sharing work without internet?

The online invite system requires access to the relay server.

If you are on LAN but do not want to use internet/relay sharing, use manual sharing instead:

- Copy Code
- Paste Code
- Export JSON
- Import JSON

Those methods do not require the online invite relay.

---

## What is Copy Code / Paste Code?

Copy Code / Paste Code is a manual sharing method.

Instead of using the relay invite flow, the sender copies a share code and gives it to the receiver by any method they choose, such as chat, Discord, a text file, or direct message.

The receiver pastes the code into the mod and previews/imports the shared data.

This is useful when:

- online sharing is disabled
- the relay is unavailable
- invites are rate-limited
- players are not online at the same time
- you prefer manual sharing

---

## What is Save JSON / Import JSON?

Save JSON exports the active database or selected share data to a file.

Import JSON lets another instance import that file later.

This is better for larger backups, moving launcher instances, or preserving a database outside of Minecraft.

---

## Which sharing method should I use?

Use **Online Invite** when both players are online and want an in-game flow.

Use **Copy Code / Paste Code** when you want a quick manual transfer without relying on invite delivery.

Use **Export JSON / Import JSON** when you want a file backup, launcher migration, or long-term copy.

---

## Why is my invite not appearing?

Check these first:

- Both players must have Find My Villager installed.
- Both players must have Online Sharing enabled.
- The recipient must have Incoming Invites enabled.
- The recipient should be in a world/server.
- The recipient can open Share Villagers and press Check for Invites.
- The sender should keep the share session active until the receiver accepts.
- Invites are temporary and can expire.
- The relay may be unavailable or rate-limited.
- The selected recipient must be the correct player.
- If using a server, both players should be connected with valid player identities.

For LAN and Essential-style sessions, invites are routed to the selected recipient instead of being tied only to the local world key. This avoids the common case where the host has a singleplayer world key and the joining player has a multiplayer session key.

If invites still do not appear, use Copy Code / Paste Code as a fallback.

---

## Why does the sender say the invite was sent, but the receiver sees nothing?

Possible causes:

- receiver has Incoming Invites disabled
- receiver has Online Sharing disabled
- receiver is not currently polling for invites
- receiver is not in a world/server
- sender selected the wrong recipient
- invite expired before the receiver checked
- relay was temporarily unavailable
- relay rate limiting was active
- the receiver is using an older incompatible version

The sender sending an invite only creates the temporary invite session. The receiver still has to poll and accept it before the selected villager payload is uploaded.

---

## Why does the receiver accept, but no data arrives?

After the receiver accepts, the sender must upload the selected payload.

This usually happens automatically while the sender's share session is still active. If the sender closes Minecraft, leaves the screen too early, disconnects, or loses relay access before upload, the receiver may keep waiting.

Try sending the invite again, or use Copy Code / Paste Code.

---

## Why does the import preview warn about a different world/server?

Databases are separated by world or server.

If the shared payload appears to come from a different database than the one you currently have active, the mod can warn you before importing.

This warning is intentional. It helps prevent accidentally mixing trades from different worlds or servers.

In LAN/Essential-style sessions, invite delivery is routed by recipient so the invite can arrive reliably, but the payload still keeps database identity information for this safety check.

---

## Can someone send me data without permission?

A sender can create an invite for a recipient, but receiving an invite does not automatically import anything.

You still choose whether to accept, download, preview, and import.

---

## Can someone overwrite my database through online sharing?

Not automatically.

The receiver controls import behavior from the preview/import screen. The mod uses validation and safer import behavior, and creates backups for import operations where applicable.

---

## Is online sharing encrypted?

Online sharing uses HTTPS to communicate with the official relay URL.

The relay is still involved in temporary delivery, so do not treat online sharing as end-to-end encrypted private storage.

Only share data you are comfortable sending through the relay.

---

## Can I disable online sharing completely?

Yes.

Online sharing is optional and can be disabled.

When disabled, use local search, export/import, backup, Copy Code, or Paste Code instead.

---

# Backups and Safety

## Does the mod create backups?

The Data Manager includes backup tools, and imports are designed to be safer by validating data and creating a recovery point before changing stored data.

You can also manually copy the config folder.

---

## Should I back up before updating?

For important worlds or large trading halls, yes.

Use Backup Now or export your active database before testing new versions.

---

## Can I recover from a bad import?

If a backup was created before import, you can restore from backup.

For important data, keep manual exports too.

---

## Can I edit the JSON files manually?

You can, but it is not recommended unless you know what you are doing.

Invalid JSON, wrong fields, or mismatched database data may cause import failure or bad search results.

Use the Data Manager when possible.

---

# Compatibility

## Is this compatible with other villager mods?

Usually, if the trade screen behaves like a normal Minecraft villager or wandering trader screen, Find My Villager has a better chance of reading it.

Compatibility can vary with mods that heavily replace trade screens, change networking, change villager behavior, or use custom trading systems.

---

## Is this compatible with resource packs?

Generally yes.

Resource packs should not affect saved trade data unless they significantly change text display in a way that affects what the client sees.

---

## Is this compatible with shaders?

Generally yes.

Shaders should not affect the saved trade database.

Visual HUD elements or glow effects may look different depending on shader behavior.

---

## Does this work with modded villagers or custom trades?

It may work if the modded trade appears through supported trade screens and uses item/trade data the client can read.

Custom systems that do not use normal villager-style trade screens may not be supported.

---

## Does this work with servers that hide trade information?

The mod can only save information your client actually receives and displays.

If a server or another mod hides or changes trade data, Find My Villager cannot recover information that was never available to the client.

---

# Rules and Fair Use

## Is this a cheat?

Find My Villager does not automate gameplay, reveal unknown trades, modify server behavior, or interact with villagers for you.

It only helps you search trades you have already opened and seen.

Server rules can still vary, so check the rules of the server you play on.

---

## Can server owners forbid it?

Yes.

Even client-side utility mods can be restricted by server rules.

If you are unsure, ask the server staff.

---

## Does this give an unfair advantage?

It depends on the server's rules and expectations.

The mod is designed as an organization/search tool for information you already saw, similar to keeping notes about villagers. It does not discover unknown trades or perform actions for you.

---

# Licensing and Distribution

## What license does the project use?

Versions 0.4.0 and newer are licensed under the **Earlystream Source-Available License v1.0**.

This project is source-available for transparency, portfolio review, security review, compatibility review, and educational reading.

You may use official builds for personal gameplay. You may not reupload, redistribute, rename, publish modified builds, copy substantial source code, or create competing clones without written permission.

Older versions released under **MPL-2.0** remain under **MPL-2.0**.

See:

```text
LICENSE
```

---

## Can I reupload or redistribute the mod?

No.

Official downloads are only allowed from the official Modrinth page, CurseForge page, GitHub repository, or other locations explicitly approved by earlystream.

Reuploads, renamed copies, modified builds, mirrors, unofficial ports, and third-party redistribution are not allowed without written permission.

---

## Can I use this in a modpack?

Yes, but only through official platform dependency/download systems.

Use the official Modrinth or CurseForge project page.

Do not manually rehost the JAR in a modpack archive, launcher CDN, Discord server, file host, or third-party mirror without written permission.

---

## Can I fork the project?

You may read the source under the license terms.

You may not publish competing clones, renamed builds, modified builds, unofficial ports, or redistributed versions without written permission.

Check the license before using any source code.

---

## Where should I download the mod?

Use the official project pages:

- Modrinth
- CurseForge
- GitHub repository/releases when provided by earlystream

Avoid random mirrors, reuploads, renamed files, or unofficial builds.
