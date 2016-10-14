package net.pl3x.bukkit.mobdrops;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Drop {
    private final EntityType entityType;
    private final Object entityVariant;
    private final int minArmor;
    private final int maxArmor;
    private final double chance;
    private final ItemStack itemStack;

    public Drop(EntityType entityType, Object entityVariant, int minArmor, int maxArmor, double chance, ItemStack itemStack) {
        this.entityType = entityType;
        this.entityVariant = entityVariant;
        this.minArmor = minArmor;
        this.maxArmor = maxArmor;
        this.chance = chance;
        this.itemStack = itemStack;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Object getEntityVariant() {
        return entityVariant;
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

    public ItemStack getItemStack() {
        return itemStack;
    }
}
