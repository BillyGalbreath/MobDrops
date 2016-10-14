package net.pl3x.bukkit.mobdrops.configuration;

import net.pl3x.bukkit.mobdrops.Drop;
import net.pl3x.bukkit.mobdrops.ItemUtil;
import net.pl3x.bukkit.mobdrops.Logger;
import net.pl3x.bukkit.mobdrops.MobDrops;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class Config {
    public static boolean COLOR_LOGS = true;
    public static boolean DEBUG_MODE = false;
    public static String LANGUAGE_FILE = "lang-en.yml";
    public static Collection<Drop> DROPS = new HashSet<>();

    public static void reload() {
        MobDrops plugin = MobDrops.getPlugin();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");

        // load up the drops
        buildDrops(config);
    }

    private static void buildDrops(FileConfiguration config) {
        DROPS.clear(); // clear the old drops (prevents duplicates on reload)
        ConfigurationSection topSection = config.getConfigurationSection("drops");
        drops:
        for (String sectionName : topSection.getKeys(false)) {
            Logger.debug("Loading drop: " + sectionName);
            ConfigurationSection dropSection = topSection.getConfigurationSection(sectionName);

            ItemStack itemStack = ItemUtil.getItemStack(dropSection.getConfigurationSection("item"));
            if (itemStack == null) {
                Logger.error("Problem loading drop! Could not parse item: " + dropSection);
                continue;
            }

            // check master chance
            double masterChance = -1;
            if (dropSection.isSet("chance")) {
                try {
                    masterChance = dropSection.getDouble("chance");
                } catch (Exception e) {
                    Logger.error("Problem loading drop! Invalid chance: " + dropSection);
                    continue;
                }
            }

            // setup the valid drops
            Collection<Drop> validDrops = new HashSet<>();

            // cycle entities and populate drops to register
            for (Map<?, ?> map : dropSection.getMapList("entities")) {
                String entityName = map.get("type").toString().toUpperCase();

                // check entity type
                EntityType entityType;
                try {
                    entityType = EntityType.valueOf(entityName.toUpperCase());
                } catch (Exception e) {
                    Logger.error("Problem loading drop! Invalid entity type: " + dropSection + " -> " + entityName);
                    continue drops;
                }

                // check entity variant
                Object entityVariant = null;
                Object setVariant = map.get("variant");
                if (setVariant != null) {
                    String variant;
                    try {
                        variant = setVariant.toString();
                    } catch (Exception e) {
                        Logger.error("Problem loading drop! Invalid entity variant: " + dropSection + " -> " + entityName);
                        continue drops;
                    }

                    if (variant != null && !variant.isEmpty()) {
                        entityVariant = getVariant(entityType, variant);
                    }

                    if (entityVariant == null) {
                        Logger.error("Problem loading drop! Invalid entity variant: " + dropSection + " -> " + entityName);
                        continue drops;
                    }
                }

                // calculate chance
                double chance = masterChance;
                Object setChance = map.get("chance");
                if (setChance != null) {
                    try {
                        chance = Double.valueOf(setChance.toString());
                    } catch (Exception e) {
                        Logger.error("Problem loading drop! Invalid chance: " + dropSection + " -> " + entityName);
                        continue drops;
                    }
                }

                // calculate minimum armor
                int minArmor = 0;
                Object setMinArmor = map.get("min-armor");
                if (setMinArmor != null) {
                    try {
                        minArmor = Integer.valueOf(setMinArmor.toString());
                    } catch (Exception e) {
                        Logger.error("Problem loading drop! Invalid minimum armor: " + dropSection + " -> " + entityName);
                        continue drops;
                    }
                }

                // calculate maximum armor
                int maxArmor = 20;
                Object setMaxArmor = map.get("max-armor");
                if (setMaxArmor != null) {
                    try {
                        maxArmor = Integer.valueOf(setMaxArmor.toString());
                    } catch (Exception e) {
                        Logger.error("Problem loading drop! Invalid maximum armor: " + dropSection + " -> " + entityName);
                        continue drops;
                    }
                }

                // build the drop
                Logger.debug("Registered drop: " + entityType + ", " + entityVariant + ", " + minArmor + ", " + maxArmor + ", " + chance + ", " + itemStack);
                validDrops.add(new Drop(entityType, entityVariant, minArmor, maxArmor, chance, itemStack));
            }

            // finally register the valid drops
            if (!validDrops.isEmpty()) {
                DROPS.addAll(validDrops);
            }
        }
    }

    private static Object getVariant(EntityType entityType, String variant) {
        switch (entityType) {
            case GUARDIAN:
                switch (variant) {
                    case "NORMAL":
                        return false;
                    case "ELDER":
                        return true;
                }
                return null;
            case HORSE:
                switch (variant) {
                    case "DONKEY":
                        return Horse.Variant.DONKEY;
                    case "HORSE":
                        return Horse.Variant.HORSE;
                    case "MULE":
                        return Horse.Variant.MULE;
                    case "SKELETON_HORSE":
                        return Horse.Variant.SKELETON_HORSE;
                    case "UNDEAD_HORSE":
                        return Horse.Variant.UNDEAD_HORSE;
                }
                return null;
            case OCELOT:
                switch (variant) {
                    case "BLACK_CAT":
                        return Ocelot.Type.BLACK_CAT;
                    case "RED_CAT":
                        return Ocelot.Type.RED_CAT;
                    case "SIAMESE_CAT":
                        return Ocelot.Type.SIAMESE_CAT;
                    case "WILD_OCELOT":
                        return Ocelot.Type.WILD_OCELOT;
                }
                return null;
            case RABBIT:
                switch (variant) {
                    case "BLACK":
                        return Rabbit.Type.BLACK;
                    case "BLACK_AND_WHITE":
                        return Rabbit.Type.BLACK_AND_WHITE;
                    case "BROWN":
                        return Rabbit.Type.BROWN;
                    case "GOLD":
                        return Rabbit.Type.GOLD;
                    case "SALT_AND_PEPPER":
                        return Rabbit.Type.SALT_AND_PEPPER;
                    case "THE_KILLER_BUNNY":
                        return Rabbit.Type.THE_KILLER_BUNNY;
                    case "WHITE":
                        return Rabbit.Type.WHITE;
                }
                return null;
            case SKELETON:
                switch (variant) {
                    case "NORMAL":
                        return Skeleton.SkeletonType.NORMAL;
                    case "STRAY":
                        return Skeleton.SkeletonType.STRAY;
                    case "WITHER":
                        return Skeleton.SkeletonType.WITHER;
                }
                return null;
            case VILLAGER:
                switch (variant) {
                    case "BLACKSMITH":
                        return Villager.Profession.BLACKSMITH;
                    case "BUTCHER":
                        return Villager.Profession.BUTCHER;
                    case "FARMER":
                        return Villager.Profession.FARMER;
                    case "LIBRARIAN":
                        return Villager.Profession.LIBRARIAN;
                    case "PRIEST":
                        return Villager.Profession.PRIEST;
                }
                return null;
            case ZOMBIE:
                switch (variant) {
                    case "BLACKSMITH":
                        return Villager.Profession.BLACKSMITH;
                    case "BUTCHER":
                        return Villager.Profession.BUTCHER;
                    case "FARMER":
                        return Villager.Profession.FARMER;
                    case "HUSK":
                        return Villager.Profession.HUSK;
                    case "LIBRARIAN":
                        return Villager.Profession.LIBRARIAN;
                    case "NORMAL":
                        return Villager.Profession.NORMAL;
                    case "PRIEST":
                        return Villager.Profession.PRIEST;
                }
                return null;
        }
        return null;
    }
}
