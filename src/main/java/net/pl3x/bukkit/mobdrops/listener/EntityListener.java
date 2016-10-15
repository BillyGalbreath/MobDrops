package net.pl3x.bukkit.mobdrops.listener;

import net.pl3x.bukkit.mobdrops.Drop;
import net.pl3x.bukkit.mobdrops.Logger;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EntityListener implements Listener {
    private Random random = new Random();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // check if killed by player
        if (entity.getKiller() == null) {
            return; // no special drops unless killed by player
        }

        EntityType entityType = entity.getType();
        Object entityVariant = null;
        switch (entityType) {
            case GUARDIAN:
                entityVariant = ((Guardian) entity).isElder();
                break;
            case HORSE:
                entityVariant = ((Horse) entity).getVariant();
                break;
            case OCELOT:
                entityVariant = ((Ocelot) entity).getCatType();
                break;
            case RABBIT:
                entityVariant = ((Rabbit) entity).getRabbitType();
                break;
            case SKELETON:
                entityVariant = ((Skeleton) entity).getSkeletonType();
                break;
            case VILLAGER:
                entityVariant = ((Villager) entity).getProfession();
                break;
            case ZOMBIE:
                entityVariant = ((Zombie) entity).getVillagerProfession();
                break;
        }

        int armor = 0;
        for (ItemStack armorPiece : entity.getEquipment().getArmorContents()) {
            if (armorPiece == null) {
                continue;
            }
            switch (armorPiece.getType()) {
                case LEATHER_BOOTS:
                    armor += 1;
                    break;
                case LEATHER_LEGGINGS:
                    armor += 2;
                    break;
                case LEATHER_CHESTPLATE:
                    armor += 3;
                    break;
                case LEATHER_HELMET:
                    armor += 1;
                    break;
                case GOLD_BOOTS:
                    armor += 1;
                    break;
                case GOLD_LEGGINGS:
                    armor += 3;
                    break;
                case GOLD_CHESTPLATE:
                    armor += 5;
                    break;
                case GOLD_HELMET:
                    armor += 2;
                    break;
                case CHAINMAIL_BOOTS:
                    armor += 1;
                    break;
                case CHAINMAIL_LEGGINGS:
                    armor += 4;
                    break;
                case CHAINMAIL_CHESTPLATE:
                    armor += 5;
                    break;
                case CHAINMAIL_HELMET:
                    armor += 2;
                    break;
                case IRON_BOOTS:
                    armor += 2;
                    break;
                case IRON_LEGGINGS:
                    armor += 5;
                    break;
                case IRON_CHESTPLATE:
                    armor += 6;
                    break;
                case IRON_HELMET:
                    armor += 2;
                    break;
                case DIAMOND_BOOTS:
                    armor += 3;
                    break;
                case DIAMOND_LEGGINGS:
                    armor += 6;
                    break;
                case DIAMOND_CHESTPLATE:
                    armor += 8;
                    break;
                case DIAMOND_HELMET:
                    armor += 3;
                    break;
            }
        }

        Set<ItemStack> drops = new HashSet<>();

        for (Drop drop : Config.DROPS) {
            if (drop.getEntityType() != entityType) {
                // silent
                continue;
            }
            if (drop.getEntityVariant() != null && drop.getEntityVariant() != entityVariant) {
                Logger.debug("Not correct variant: " + entityVariant + " != " + drop.getEntityVariant());
                continue;
            }
            if (armor < drop.getMinArmor()) {
                Logger.debug("Armor too low: " + armor + " < " + drop.getMinArmor());
                continue;
            }
            if (armor > drop.getMaxArmor()) {
                Logger.debug("Armor too high: " + armor + " > " + drop.getMaxArmor());
                continue;
            }

            double chance = drop.getChance() / 100D;
            double randNum = random.nextDouble();
            if (chance < 1D && randNum > chance) {
                Logger.debug("Missed chance: " + randNum + " > " + chance);
                continue; // chance failed
            }

            Logger.debug("Success! Dropping " + drop);
            drops.add(drop.getItemStack());
        }

        for (ItemStack drop : drops) {
            entity.getWorld().dropItem(entity.getLocation(), drop.clone());
        }
    }
}
