package com.organs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.EnumMap;
import java.util.Map;

/**
 * A player's organ condition. Immutable — every change produces a new instance, which keeps
 * the attachment API happy and makes the "did anything change?" check trivial.
 */
public record OrganData(int heart, int lungs, int liver, int brain) {
    public static final Codec<OrganData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("heart", Organ.MAX).forGetter(OrganData::heart),
            Codec.INT.optionalFieldOf("lungs", Organ.MAX).forGetter(OrganData::lungs),
            Codec.INT.optionalFieldOf("liver", Organ.MAX).forGetter(OrganData::liver),
            Codec.INT.optionalFieldOf("brain", Organ.MAX).forGetter(OrganData::brain)
    ).apply(instance, OrganData::new));

    public static OrganData healthy() {
        return new OrganData(Organ.MAX, Organ.MAX, Organ.MAX, Organ.MAX);
    }

    public int get(Organ organ) {
        return switch (organ) {
            case HEART -> heart;
            case LUNGS -> lungs;
            case LIVER -> liver;
            case BRAIN -> brain;
        };
    }

    public OrganData with(Organ organ, int value) {
        int v = Math.clamp(value, 0, Organ.MAX);
        return switch (organ) {
            case HEART -> new OrganData(v, lungs, liver, brain);
            case LUNGS -> new OrganData(heart, v, liver, brain);
            case LIVER -> new OrganData(heart, lungs, v, brain);
            case BRAIN -> new OrganData(heart, lungs, liver, v);
        };
    }

    public OrganData injure(Organ organ, int amount) {
        return with(organ, get(organ) - amount);
    }

    /** Heals every organ by the same amount. */
    public OrganData mend(int amount) {
        OrganData result = this;
        for (Organ organ : Organ.values()) {
            result = result.with(organ, result.get(organ) + amount);
        }
        return result;
    }

    public Map<Organ, Integer> asMap() {
        Map<Organ, Integer> map = new EnumMap<>(Organ.class);
        for (Organ organ : Organ.values()) {
            map.put(organ, get(organ));
        }
        return map;
    }

    public boolean isFullyHealthy() {
        return heart == Organ.MAX && lungs == Organ.MAX && liver == Organ.MAX && brain == Organ.MAX;
    }
}
