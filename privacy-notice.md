# Find My Villager Privacy Notice

**Find My Villager** is designed as a privacy-first, local-client mod for Minecraft. Core features—including villager searching, merchant capturing, filtering, and database management—operate entirely on your local machine and do not require an internet connection.

Online sharing is a strictly **optional** feature that is disabled by default.

---

## 1. Privacy by Design: Local-First
If you do not enable Online Sharing:
- The mod makes no connections to the sharing relay.
- No villager data, world metadata, or player identifiers are ever transmitted.
- You cannot send or receive share invites.

## 2. Optional Online Sharing
When you choose to use Online Sharing, the mod communicates with a dedicated Cloudflare Worker relay:
`https://findmyvillagershare.earlystream.workers.dev`

This relay facilitates the temporary transfer of villager data between players. It is built with **Privacy by Design** principles, ensuring that data is only transmitted when explicitly requested and accepted by both parties.

## 3. Identity Protection & Data Hardening
To protect player privacy, Find My Villager implements **identity hashing**:
- **No Cleartext UUIDs**: Your raw Minecraft Player UUID is never sent to the relay. Instead, it is transformed into a scoped SHA-256 hash before leaving your computer.
- **No Tracking**: These hashes prevent the relay or third parties from trivially mapping sharing activity back to your persistent Minecraft identity or server history.

## 4. Transparent Sharing Workflow
Data is never "synced" or uploaded automatically. A transfer only occurs when:
1. **Sender Action**: The sender enables Online Sharing and explicitly selects villagers to share.
2. **Consent**: The sender acknowledges a privacy warning and chooses a specific recipient.
3. **Receiver Action**: The receiver must have Online Sharing and "Incoming Invites" enabled.
4. **Approval**: The receiver must manually accept the incoming share request.
5. **Preview**: The receiver previews the data and chooses whether to merge or replace their local data.

## 5. What Data is Shared?
When a share is successfully initiated, the following selected data is transmitted:
- **Villager Metadata**: Names (detected or manual), professions, levels, and experience.
- **Trading Data**: Item offers, prices (including discounts), and stock status.
- **World Context**: Coordinates (X, Y, Z), dimension names, and last-seen timestamps.
- **Mod Metadata**: Favorites, custom notes (if selected), and hashed world/server identifiers.
- **Identity**: Minecraft usernames and **hashed** player UUIDs for routing.

## 6. What is NEVER Collected?
Find My Villager does not access, collect, or transmit:
- Microsoft or Minecraft account passwords.
- Session tokens, access tokens, or login credentials.
- Payment information or real-world identities (names, emails, etc.).
- Full world maps or player inventories.

**Security Tip:** Never enter your Minecraft password or session tokens into any mod interface.

## 7. Infrastructure & Metadata
The relay uses Cloudflare Workers. Like most web services, Cloudflare may process standard connection metadata to ensure service stability and prevent abuse:
- IP address and approximate geographic region.
- Request timestamps and HTTP status codes.
- User-Agent and Cloudflare request identifiers.

The relay is configured to minimize logging. We do not log full villager payloads, trade data, coordinates, or player identities.

## 8. Data Retention
The relay is a **temporary delivery service**, not a storage platform.
- **Short-Lived Sessions**: Shared data exists only as long as needed for delivery (typically less than 2 minutes).
- **Automatic Cleanup**: Payloads are deleted immediately after delivery or upon session expiration/decline.

## 9. Import Security
Shared data is treated as untrusted. The mod validates every incoming payload for structural integrity and safety before import. Receivers always maintain full control over whether to merge shared villagers into their hall or perform a full database replacement.

## 10. Contact & Transparency
Find My Villager is open about its data practices. You can review the source code or contact us at the following locations:
- **GitHub**: [https://github.com/earlystream/Find-My-Villager](https://github.com/earlystream/Find-My-Villager)
- **Modrinth**: [https://modrinth.com/mod/find-my-villager](https://modrinth.com/mod/find-my-villager)
- **CurseForge**: [https://www.curseforge.com/minecraft/mc-mods/find-my-villager](https://www.curseforge.com/minecraft/mc-mods/find-my-villager)
