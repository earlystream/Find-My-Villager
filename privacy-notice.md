# Find My Villager Online Sharing Privacy Notice

Find My Villager is primarily a local, client-side Minecraft mod.

The mod works without online sharing. Online sharing is optional.

## Online Sharing Is Disabled by Default

Online Sharing and Incoming Invites are disabled by default.

If Online Sharing is disabled:

- Find My Villager does not connect to the online sharing relay for online sharing
- You cannot send online share invites
- You cannot receive online share invites
- No villager data is uploaded through online sharing

## What Online Sharing Does

Find My Villager Online Sharing lets one player send selected saved villager data to another player.

Online sharing uses a temporary Cloudflare Worker relay:

`https://findmyvillagershare.earlystream.workers.dev`

The relay is used to help deliver temporary villager share sessions between players who choose to use the feature.

## When Data Is Sent

Villager data is not uploaded automatically.

Villager data is only uploaded after all of these happen:

1. The sender enables Online Sharing.
2. The sender opens **Data → Share Villagers**.
3. The sender selects a player.
4. The sender selects which villagers to share.
5. The sender confirms the online sharing warning.
6. The receiver has Online Sharing and Incoming Invites enabled.
7. The receiver accepts the share request.

The receiver still has to preview the shared data and choose an import mode before anything is imported.

## Data That May Be Shared

Shared villager data may include:

- Villager names
- Villager professions
- Villager levels
- Trades
- Trade prices
- Stock/sold-out state
- Favorites
- Notes, if included
- Dimensions
- Coordinates
- Last-seen timestamps
- Database/world/server identifiers used by the mod
- Sender and receiver Minecraft usernames/UUIDs

Only selected villager data is shared. The whole database is not sent unless the user chooses to share all saved villagers.

## Data That Is Not Collected

Find My Villager Online Sharing does not collect or ask for:

- Microsoft account passwords
- Minecraft login passwords
- Minecraft session tokens
- Access tokens
- Payment information
- Real names
- Email addresses

Do not enter private account information into Find My Villager.

## Cloudflare and Connection Metadata

Online sharing uses Cloudflare Workers.

When Online Sharing is used, Cloudflare and the relay may process basic connection metadata needed to operate the service, such as:

- IP address
- request time
- request path
- user agent
- approximate region/country
- HTTP status codes
- Cloudflare request identifiers

This metadata may appear in Cloudflare operational logs or observability tools.

Find My Villager should not log full villager payloads, notes, coordinates, trades, passwords, tokens, or full shared databases.

## Temporary Relay Sessions

Online shares are temporary.

The relay is designed to use short-lived share sessions. Shared payloads should expire after a short timeout, delivery, decline, or session cleanup.

The relay is not intended to permanently store villager databases.

## Import Safety

Receiving shared villagers does not automatically modify your data.

Before import, the receiver can preview the shared villagers and choose:

- **Partial Import** — merge shared villagers into the current database
- **Replace Database** — replace only the current active world/server database after creating a backup

Shared data is treated as untrusted. The mod validates shared payloads before importing them.

## User Control

You can disable Online Sharing at any time.

You can also keep Online Sharing enabled while keeping Incoming Invites disabled if you only want to send shares and not receive share requests.

## Important Notes

Only share villagers with players you trust.

Shared villager data may reveal coordinates, trading hall layouts, notes, and other local information about your world/server.

Server owners cannot normally see Find My Villager relay payloads unless the data is also sent through Minecraft chat, commands, server packets, or another server-visible method.

Cloudflare, network providers, or other infrastructure involved in internet traffic may process connection metadata.

## Contact

For issues, questions, or source code, use the project links:

- Modrinth: `https://modrinth.com/mod/find-my-villager`
- CurseForge: `https://www.curseforge.com/minecraft/mc-mods/find-my-villager`
- GitHub: `https://github.com/earlystream/Find-My-Villager`
