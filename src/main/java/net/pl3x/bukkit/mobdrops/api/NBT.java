package net.pl3x.bukkit.mobdrops.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public interface NBT {
    ItemStack setItemNBT(ItemStack bukkitItem, String nbt, String path);

    boolean hasAI(LivingEntity entity);
}
