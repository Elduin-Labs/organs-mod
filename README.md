# Organs

<img src="src/main/resources/assets/organs/icon.png" width="128" align="right" alt="">

You have four organs. Different kinds of damage wreck different ones, and a damaged organ
changes how you play until it heals.

## The organs

| Organ | Injured by | When it's failing |
|---|---|---|
| **Heart** | Mobs, players, arrows, cactus — ordinary violence | Slowness, Weakness |
| **Lungs** | Drowning, suffocation, fire, lava | Your breath capacity is capped to what's left of them; Mining Fatigue while sprinting |
| **Liver** | Magic, wither, starvation, dragon breath, sonic boom | You burn food fast, then can't keep it down (Hunger) |
| **Brain** | Falls, anvils, flying into walls, stalactites | Nausea in waves, then bursts of Blindness |

Explosions split their damage between brain and lungs.

Each organ runs 0–100. Below **40** it's *impaired*; below **20** it's *failing*, and the
penalties get serious. You get a warning in the action bar when an organ crosses a threshold —
injuries are never silent.

## Healing

Organs mend on their own, but slowly, and only once you've been unhurt for 10 seconds:

- **1 point every 15 seconds** while your hunger bar is at 16 or above.
- **1 point every 3 seconds** under a Regeneration effect — potions are the real treatment.

Dying gives you a fresh body: organs reset to full on respawn. Damage otherwise persists across
logouts and saves.

## The HUD

Four small bars sit just above your health bar — heart, lungs, liver, brain, left to right.
They only appear when something is actually damaged, so a healthy player's screen stays vanilla.
A failing organ's bar pulses white so you notice it without reading numbers.

Requires the mod on the client. On a server, vanilla clients still get organs and penalties —
they just won't see the bars, and use `/organs` instead.

## Commands

```
/organs                       your own readout, with colored bars
/organs <player>              someone else's         (permission level 2)
/organs heal                  back to full           (permission level 2)
/organs set <organ> <0-100>   for testing            (permission level 2)
```

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.0+
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java 21

## Installation

Drop `organs-1.1.0.jar` into your `mods` folder alongside Fabric API.

All the logic runs on the server, so on a multiplayer server only the server strictly needs it —
vanilla clients can connect and will still have organs, minus the HUD bars. Install it on the
client too to see the bars.

## Building

```bash
./gradlew build
```

## License

MIT — see [LICENSE](LICENSE).
