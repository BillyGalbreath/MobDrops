package net.pl3x.bukkit.mobdrops;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Drop {
    private final EntityType entityType;
    private final Object entityVariant;
    private final Boolean hasAI;
    private final int minArmor;
    private final int maxArmor;
    private final double chance;
    private final int diminishTime;
    private final int diminishIncrement;
    private final double diminishLoss;
    private final ItemStack itemStack;
    private final boolean clearAllDrops;

    public Drop(EntityType entityType, Object entityVariant, Boolean hasAI, int minArmor, int maxArmor, double chance, int diminishTime, int diminishIncrement, double diminishLoss, ItemStack itemStack, boolean clearAllDrops) {
        this.entityType = entityType;
        this.entityVariant = entityVariant;
        this.hasAI = hasAI;
        this.minArmor = minArmor;
        this.maxArmor = maxArmor;
        this.chance = chance / 100D; // convert to percent
        this.diminishTime = diminishTime * 1000; // convert seconds to millis
        this.diminishIncrement = diminishIncrement;
        this.diminishLoss = diminishLoss / 100D; // convert to percent
        this.itemStack = itemStack;
        this.clearAllDrops = clearAllDrops;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Object getEntityVariant() {
        return entityVariant;
    }

    public Boolean hasAI() {
        return hasAI;
    }

    public int getMinArmor() {
        return minArmor;
    }

    public int getMaxArmor() {
        return maxArmor;
    }

    public double getChance() {
        return chance;
    }

    public int getDiminishTime() {
        return diminishTime;
    }

    public int getDiminishIncrement() {
        return diminishIncrement;
    }

    public double getDiminishLoss() {
        return diminishLoss;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isClearAllDrops() {
        return clearAllDrops;
    }
}
