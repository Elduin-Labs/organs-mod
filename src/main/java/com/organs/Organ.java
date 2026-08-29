package com.organs;

import net.minecraft.util.Formatting;

/**
 * The four organs a player has. Each runs 0..100 — 100 is healthy, 0 is failed.
 */
public enum Organ {
    HEART("heart", Formatting.RED),
    LUNGS("lungs", Formatting.AQUA),
    LIVER("liver", Formatting.GOLD),
    BRAIN("brain", Formatting.LIGHT_PURPLE);

    public static final int MAX = 100;
    /** Below this an organ is impaired. */
    public static final int IMPAIRED = 40;
    /** Below this an organ is failing, and the penalties get serious. */
    public static final int CRITICAL = 20;

    private final String id;
    private final Formatting color;

    Organ(String id, Formatting color) {
        this.id = id;
        this.color = color;
    }

    public String id() {
        return id;
    }

    public Formatting color() {
        return color;
    }

    public String translationKey() {
        return "organ.organs." + id;
    }

    public static Organ byId(String id) {
        for (Organ organ : values()) {
            if (organ.id.equals(id)) {
                return organ;
            }
        }
        return null;
    }
}
