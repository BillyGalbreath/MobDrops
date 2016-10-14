package net.pl3x.bukkit.mobdrops;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtil {
    public static ItemStack getItemStack(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        Material material = getMaterial(section.getString("material"));
        if (material == null) {
            return null;
        }

        ItemStack itemStack = new ItemStack(material);
        itemStack.setAmount(section.getInt("amount", 1));

        itemStack = MobDrops.getPlugin().getNBTHandler()
                .setItemNBT(itemStack, section.getString("nbt"), section.getCurrentPath());

        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = section.getString("name");
        if (name != null && !name.isEmpty()) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        List<String> lore = section.getStringList("lore");
        if (lore != null && !lore.isEmpty()) {
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
            }
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private static Material getMaterial(String materialName) {
        if (materialName == null) {
            return null;
        }

        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            return null;
        }

        return material;
    }
}
