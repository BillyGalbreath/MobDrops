package net.pl3x.bukkit.mobdrops.listener;

import net.pl3x.bukkit.mobdrops.Drop;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class EntityListener implements Listener {
    private final Map<UUID, Map<Drop, List<Long>>> diminishedReturns = new HashMap<>();
    private final Random random = new Random();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

        // check from spawner
        if (entity.fromMobSpawner()) {
            return; // mob came from spawner cage
        }

        Player player = entity.getKiller();
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

            // check villager type and profession
            if (entity.getType() == EntityType.VILLAGER) {
                Villager villager = (Villager) entity;
                if (drop.getVillagerType() != null && villager.getVillagerType() != drop.getVillagerType()) {
                    continue;
                }
                if (drop.getVillagerProfession() != null && villager.getProfession() != drop.getVillagerProfession()) {
                    continue;
                }
            }

            // check cat type and color color
            if (entity.getType() == EntityType.CAT) {
                Cat cat = (Cat) entity;
                if (drop.getCatType() != null && cat.getCatType() != drop.getCatType()) {
                    continue;
                }
            }

            // check AI
            boolean hasAI = entity.hasAI();
            if (drop.hasAI() != null && drop.hasAI() != hasAI) {
                continue;
            }

            // check armor
            if (armor < drop.getMinArmor()) {
                continue;
            }
            if (armor > drop.getMaxArmor()) {
                continue;
            }

            if (drop.isClearAllDrops()) {
                clearDrops = true;
            }

            // chance and times
            double chance = drop.getChance();

            long now = System.currentTimeMillis(); // current time (epoch seconds)
            List<Long> times = drop.getDiminishTime() > 0 && diminishedMap.containsKey(drop) ? diminishedMap.get(drop) : new ArrayList<>();

            // check diminishing returns and re-calculate chance
            if (!times.isEmpty()) {
                // removed expired times
                times.removeIf(aLong -> aLong + drop.getDiminishTime() < now);

                int increment = Math.floorDiv(times.size(), drop.getDiminishIncrement());

                if (increment > 0) {
                    double loss = 1.0D - drop.getDiminishLoss();
                    for (int i = 0; i < increment; i++) {
                        chance *= loss;
                    }
                }
            }

            double randNum = random.nextDouble();
            if (chance < 1D && randNum > chance) {
                continue; // chance failed
            }

            // add item clone to drops set
            if (drop.getDiminishTime() > 0) {
                times.add(now);
                diminishedMap.put(drop, times);
            }
            drops.add(drop.getItemStack().clone());
        }

        if (clearDrops) {
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

    private int getArmorValue(LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        int armorValue = 0;
        if (equipment == null) {
            return armorValue;
        }
        for (ItemStack armorPiece : equipment.getArmorContents()) {
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
                case GOLDEN_BOOTS:
                    armorValue += 1;
                    break;
                case GOLDEN_LEGGINGS:
                    armorValue += 3;
                    break;
                case GOLDEN_CHESTPLATE:
                    armorValue += 5;
                    break;
                case GOLDEN_HELMET:
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
