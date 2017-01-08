package net.pl3x.bukkit.mobdrops.listener;

import net.pl3x.bukkit.mobdrops.Drop;
import net.pl3x.bukkit.mobdrops.Logger;
import net.pl3x.bukkit.mobdrops.MobDrops;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class EntityListener implements Listener {
    private final Map<UUID, Map<Drop, List<Long>>> diminishedReturns = new HashMap<>();
    private final Random random = new Random();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            return; // player was killed
        }

        // check if entity has a custom name (weeds out stacked mobs, custom npc/pets, etc)
        if (entity.getCustomName() != null && !entity.getCustomName().equals(entity.getName())) {
            return; // entity has a custom name set, ignore
        }

        // check if killed by player
        if (entity.getKiller() == null) {
            return; // entity was not killed by player
        }

        Player player = entity.getKiller();
        Object entityVariant = getVariant(entity);
        int armor = getArmorValue(entity);

        boolean clearDrops = false;
        Set<ItemStack> drops = new HashSet<>();
        Map<Drop, List<Long>> diminishedMap = diminishedReturns.containsKey(player.getUniqueId()) ? diminishedReturns.get(player.getUniqueId()) : new HashMap<>();

        for (Drop drop : Config.DROPS) {
            // check type
            if (drop.getEntityType() != entity.getType()) {
                // silent
                continue;
            }

            // check variant
            if (drop.getEntityVariant() != null && drop.getEntityVariant() != entityVariant) {
                Logger.debug("Not correct variant: " + entityVariant + " != " + drop.getEntityVariant());
                continue;
            }

            // check AI
            boolean hasAI = MobDrops.getPlugin().getNBTHandler().hasAI(entity);
            if (drop.hasAI() != null && drop.hasAI() != hasAI) {
                Logger.debug("Entity hasAI mismatch: " + drop.hasAI() + " != " + hasAI);
                continue;
            }

            // check armor
            if (armor < drop.getMinArmor()) {
                Logger.debug("Armor too low: " + armor + " < " + drop.getMinArmor());
                continue;
            }
            if (armor > drop.getMaxArmor()) {
                Logger.debug("Armor too high: " + armor + " > " + drop.getMaxArmor());
                continue;
            }

            if (drop.isClearAllDrops()) {
                clearDrops = true;
            }

            // chance and times
            double chance = drop.getChance();
            Logger.debug("Chance: " + chance);

            long now = System.currentTimeMillis(); // current time (epoch seconds)
            List<Long> times = drop.getDiminishTime() > 0 && diminishedMap.containsKey(drop) ? diminishedMap.get(drop) : new ArrayList<>();

            // check diminishing returns and re-calculate chance
            if (!times.isEmpty()) {
                // removed expired times
                for (Iterator<Long> iter = times.iterator(); iter.hasNext(); ) {
                    if (iter.next() + drop.getDiminishTime() < now) {
                        iter.remove();
                    }
                }

                int increment = Math.floorDiv(times.size(), drop.getDiminishIncrement());

                Logger.debug("Diminished Increment: " + increment);
                if (increment > 0) {
                    Logger.debug("Diminished Loss: " + drop.getDiminishLoss());
                    double loss = 1.0D - drop.getDiminishLoss();
                    for (int i = 0; i < increment; i++) {
                        chance *= loss;
                    }
                    Logger.debug("Diminished Chance: " + chance);
                }
            }

            double randNum = random.nextDouble();
            if (chance < 1D && randNum > chance) {
                Logger.debug("Missed chance: " + randNum + " > " + chance);
                continue; // chance failed
            }

            // add item clone to drops set
            Logger.debug("Success! Dropping " + drop);
            if (drop.getDiminishTime() > 0) {
                times.add(now);
                diminishedMap.put(drop, times);
            }
            drops.add(drop.getItemStack().clone());
        }

        if (clearDrops) {
            Logger.debug("Cleared drops");
            event.getDrops().clear();
        }

        // update diminishedReturns count
        if (drops.size() > 0) {
            diminishedReturns.put(player.getUniqueId(), diminishedMap);
        }

        // drop the items
        for (ItemStack drop : drops) {
            entity.getWorld().dropItem(entity.getLocation(), drop);
        }
    }

    private Object getVariant(LivingEntity entity) {
        switch (entity.getType()) {
            case OCELOT:
                return ((Ocelot) entity).getCatType();
            case RABBIT:
                return ((Rabbit) entity).getRabbitType();
            case VILLAGER:
                return ((Villager) entity).getProfession();
            case ZOMBIE_VILLAGER:
                return ((ZombieVillager) entity).getVillagerProfession();
            default:
                return null;
        }
    }

    private int getArmorValue(LivingEntity entity) {
        int armorValue = 0;
        for (ItemStack armorPiece : entity.getEquipment().getArmorContents()) {
            if (armorPiece == null) {
                continue;
            }
            switch (armorPiece.getType()) {
                case LEATHER_BOOTS:
                    armorValue += 1;
                    break;
                case LEATHER_LEGGINGS:
                    armorValue += 2;
                    break;
                case LEATHER_CHESTPLATE:
                    armorValue += 3;
                    break;
                case LEATHER_HELMET:
                    armorValue += 1;
                    break;
                case GOLD_BOOTS:
                    armorValue += 1;
                    break;
                case GOLD_LEGGINGS:
                    armorValue += 3;
                    break;
                case GOLD_CHESTPLATE:
                    armorValue += 5;
                    break;
                case GOLD_HELMET:
                    armorValue += 2;
                    break;
                case CHAINMAIL_BOOTS:
                    armorValue += 1;
                    break;
                case CHAINMAIL_LEGGINGS:
                    armorValue += 4;
                    break;
                case CHAINMAIL_CHESTPLATE:
                    armorValue += 5;
                    break;
                case CHAINMAIL_HELMET:
                    armorValue += 2;
                    break;
                case IRON_BOOTS:
                    armorValue += 2;
                    break;
                case IRON_LEGGINGS:
                    armorValue += 5;
                    break;
                case IRON_CHESTPLATE:
                    armorValue += 6;
                    break;
                case IRON_HELMET:
                    armorValue += 2;
                    break;
                case DIAMOND_BOOTS:
                    armorValue += 3;
                    break;
                case DIAMOND_LEGGINGS:
                    armorValue += 6;
                    break;
                case DIAMOND_CHESTPLATE:
                    armorValue += 8;
                    break;
                case DIAMOND_HELMET:
                    armorValue += 3;
                    break;
            }
        }
        return armorValue;
    }
}
