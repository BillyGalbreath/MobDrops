package net.pl3x.bukkit.mobdrops;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_15_R1.MojangsonParser;
import net.pl3x.bukkit.mobdrops.configuration.Lang;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtil {
    public static ItemStack setItemNBT(ItemStack bukkitItem, String nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return bukkitItem; // nothing to parse
        }

        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        try {
            nmsItem.setTag(MojangsonParser.parse(nbt));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static ItemStack getItemStack(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        Material material = getMaterial(section.getString("material"));
        if (material == null) {
            return null;
        }

        ItemStack itemStack = new ItemStack(material, section.getInt("amount", 1), (short) section.getInt("data", 0));

        itemStack = setItemNBT(itemStack, section.getString("nbt"));

        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = section.getString("name");
        if (name != null && !name.isEmpty()) {
            itemMeta.setDisplayName(Lang.colorize(name));
        }

        List<String> lore = section.getStringList("lore");
        if (!lore.isEmpty()) {
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, Lang.colorize(lore.get(i)));
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
