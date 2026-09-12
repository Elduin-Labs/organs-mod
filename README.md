<img src="src/main/resources/assets/organs/icon.png" width="128" align="right" alt="">

# Organs

You have a heart, lungs, a liver and a brain. Different kinds of damage injure
different organs, and a hurt organ changes how you play until it mends.

Falling doesn't just take hearts off the bar any more — it rattles your brain,
and you feel it afterwards.

- **Minecraft:** 1.21.11 · **Loader:** Fabric · **Mod id:** `organs`

## What hurts what

| damage | organ |
|---|---|
| falling, flying into a wall, anvils and falling blocks | **Brain** |
| drowning, suffocating, fire, lava, hot floor, campfire | **Lungs** |
| magic, wither, starvation, dragon breath, sonic boom | **Liver** |
| explosions | **Brain and Lungs**, half each |
| everything else | **Heart** |

## What a hurt organ does to you

Each organ has two stages — *impaired*, then *failing* — and warns you when it
crosses either.

- **Heart** — you slow down and hit weakly
- **Lungs** — mining fatigue; you tire out
- **Liver** — hunger drains faster
- **Brain** — nausea, then blindness

They mend slowly with rest and food. `/organs` shows the state of all four.

`/Elduin-Labs` opens the Elduin-Labs GitHub page, where all of Elduin's mods live.

## Building

```bash
./gradlew build
```

## Releasing

Push a tag (`v1.1.1`). The workflow builds the jar and attaches it to a GitHub
Release, and uploads to Modrinth once `MODRINTH_PROJECT_ID` is set.
