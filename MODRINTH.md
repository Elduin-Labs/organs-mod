# Modrinth listing — organs-mod

Everything below is ready to paste. Create the project at
https://modrinth.com/dashboard/projects, then run the command at the bottom.

- **Name:** Organs
- **Slug:** `organs`
- **Summary:** You have a heart, lungs, a liver and a brain. Different damage hurts different organs, and it changes how you play.
- **Categories:** Game Mechanics, Adventure
- **Environment:** client and server (required on both)
- **License:** MIT
- **Source:** https://github.com/Elduin-Labs/organs-mod
- **Icon:** `src/main/resources/assets/organs/icon.png`
- **Minecraft:** 1.21.11 · **Loader:** Fabric

---

## Description (paste into the body)

You have a **heart**, **lungs**, a **liver** and a **brain**. Different kinds of
damage injure different ones, and a hurt organ changes how you play until it
mends.

Falling doesn't just take hearts off the bar any more. It rattles your brain,
and you feel it afterwards.

### What hurts what

| damage | organ |
| --- | --- |
| falling, flying into a wall, anvils and falling blocks | **Brain** |
| drowning, suffocating, fire, lava, hot floor, campfire | **Lungs** |
| magic, wither, starvation, dragon breath, sonic boom | **Liver** |
| explosions | **Brain and Lungs**, half each |
| everything else | **Heart** |

### What it does to you

Each organ has two stages — *impaired*, then *failing* — and warns you at each.

- **Heart** — you slow down and hit weakly
- **Lungs** — mining fatigue; you tire out
- **Liver** — hunger drains faster
- **Brain** — nausea, then blindness

They mend slowly with rest and food. `/organs` shows all four.

---

## Then wire it up

```
gh variable set MODRINTH_PROJECT_ID --repo Elduin-Labs/organs-mod --body "<project id>"
cd ~/organs-mod && git tag v1.1.0 && git push origin v1.1.0
```
