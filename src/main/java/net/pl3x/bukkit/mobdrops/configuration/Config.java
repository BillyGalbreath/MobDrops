package net.pl3x.bukkit.mobdrops.configuration;

import com.google.common.base.Throwables;
import net.pl3x.bukkit.mobdrops.Drop;
import net.pl3x.bukkit.mobdrops.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

public class Config {
    public static String LANGUAGE_FILE = "lang-en.yml";

    public static final Collection<Drop> DROPS = new HashSet<>();

    private static void init(Plugin plugin) {
        LANGUAGE_FILE = getString("language-file", LANGUAGE_FILE);

        buildDrops(plugin);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the configuration file
     */
    public static void reload(Plugin plugin) {
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load config.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the configuration file for " + plugin.getName());
        config.options().copyDefaults(true);

        Config.init(plugin);

        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static void buildDrops(Plugin plugin) {
        DROPS.clear(); // clear the old drops (prevents duplicates on reload)

        ConfigurationSection topSection = config.getConfigurationSection("drops");
        if (topSection == null) {
            return;
        }
        for (String sectionName : topSection.getKeys(false)) {
            try {
                ConfigurationSection dropSection = topSection.getConfigurationSection(sectionName);
                if (dropSection == null) {
                    error(plugin, "Problem loading drop! Section null: " + sectionName);
                    continue;
                }

                ItemStack itemStack = ItemUtil.getItemStack(dropSection.getConfigurationSection("item"));
                if (itemStack == null) {
                    error(plugin, "Problem loading drop! Could not parse item: " + dropSection);
                    continue;
                }

                // check master chance
                double masterChance = -1;
                if (dropSection.isSet("chance")) {
                    try {
                        masterChance = dropSection.getDouble("chance");
                    } catch (Exception e) {
                        error(plugin, "Problem loading drop! Invalid chance: " + dropSection);
                        continue;
                    }
                }

                // check diminish time
                int diminishTime = 0;
                if (dropSection.isSet("diminishing-returns.time")) {
                    try {
                        diminishTime = dropSection.getInt("diminishing-returns.time");
                    } catch (Exception e) {
                        error(plugin, "Problem loading drop! Invalid diminishing-returns.time: " + dropSection);
                        continue;
                    }
                }

                // check diminish increment
                int diminishIncrement = 0;
                if (dropSection.isSet("diminishing-returns.increment")) {
                    try {
                        diminishIncrement = dropSection.getInt("diminishing-returns.increment");
                    } catch (Exception e) {
                        error(plugin, "Problem loading drop! Invalid diminishing-returns.increment: " + dropSection);
                        continue;
                    }
                }

                // check diminish time
                double diminishLoss = 0;
                if (dropSection.isSet("diminishing-returns.loss")) {
                    try {
                        diminishLoss = dropSection.getDouble("diminishing-returns.loss");
                    } catch (Exception e) {
                        error(plugin, "Problem loading drop! Invalid diminishing-returns.loss: " + dropSection);
                        continue;
                    }
                }

                // cycle entities and populate drops
                for (Map<?, ?> map : dropSection.getMapList("entities")) {
                    Drop drops = buildDrops(plugin, map, dropSection, masterChance, diminishTime, diminishIncrement, diminishLoss, itemStack);
                    if (drops != null) {
                        DROPS.add(drops);
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    private static Drop buildDrops(Plugin plugin, Map<?, ?> map, ConfigurationSection dropSection, double chance, int diminishTime, int diminishIncrement, double diminishLoss, ItemStack itemStack) {
        String entityName = map.get("type").toString().toUpperCase();

        // check entity type
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(entityName.toUpperCase());
        } catch (Exception e) {
            error(plugin, "Problem loading drop! Invalid entity type: " + dropSection + " -> " + entityName);
            return null;
        }

        // has-ai
        Boolean hasAI = null;
        Object setHasAI = map.get("has-ai");
        if (setHasAI != null) {
            try {
                hasAI = Boolean.valueOf(setHasAI.toString());
            } catch (Exception e) {
                error(plugin, "Problem loading drop! Invalid has-ai flag: " + dropSection + " -> " + entityName);
                return null;
            }
        }

        // calculate chance
        Object setChance = map.get("chance");
        if (setChance != null) {
            try {
                chance = Double.valueOf(setChance.toString());
            } catch (Exception e) {
                error(plugin, "Problem loading drop! Invalid chance: " + dropSection + " -> " + entityName);
                return null;
            }
        }

        // calculate minimum armor
        int minArmor = 0;
        Object setMinArmor = map.get("min-armor");
        if (setMinArmor != null) {
            try {
                minArmor = Integer.valueOf(setMinArmor.toString());
            } catch (Exception e) {
                error(plugin, "Problem loading drop! Invalid minimum armor: " + dropSection + " -> " + entityName);
                return null;
            }
        }

        // calculate maximum armor
        int maxArmor = 20;
        Object setMaxArmor = map.get("max-armor");
        if (setMaxArmor != null) {
            try {
                maxArmor = Integer.valueOf(setMaxArmor.toString());
            } catch (Exception e) {
                error(plugin, "Problem loading drop! Invalid maximum armor: " + dropSection + " -> " + entityName);
                return null;
            }
        }

        // clear all normal drops
        boolean clearDrops = false;
        Object setClearDrops = map.get("clear-drops");
        if (setClearDrops != null) {
            try {
                clearDrops = Boolean.valueOf(setClearDrops.toString());
            } catch (Exception e) {
                error(plugin, "Problem loading drop! Invalid clear-drops: " + dropSection + " -> " + entityName);
                return null;
            }
        }

        return new Drop(entityType, hasAI, minArmor, maxArmor, chance, diminishTime, diminishIncrement, diminishLoss, itemStack, clearDrops);
    }

    private static void error(Plugin plugin, String error) {
        plugin.getLogger().severe(error);
    }
}
