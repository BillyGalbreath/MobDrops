package net.pl3x.bukkit.mobdrops;

import net.pl3x.bukkit.mobdrops.api.NBT;
import net.pl3x.bukkit.mobdrops.command.CmdMobDrops;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import net.pl3x.bukkit.mobdrops.configuration.Lang;
import net.pl3x.bukkit.mobdrops.listener.EntityListener;
import net.pl3x.bukkit.mobdrops.nms.NBTHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class MobDrops extends JavaPlugin {
    private final NBT nbtHandler;

    public MobDrops() {
        nbtHandler = new NBTHandler();
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        try {
            Entity.class.getMethod("fromMobSpawner");
        } catch (NoSuchMethodException e) {
            Logger.error("# Missing needed classes/methods!");
            Logger.error("# This plugin is only compatible with Paper servers!");
            return;
        }

        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);

        getCommand("mobdrops").setExecutor(new CmdMobDrops(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        Logger.info(getName() + " disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4" + getName() + " is disabled. See console log for more information."));
        return true;
    }

    public static MobDrops getPlugin() {
        return MobDrops.getPlugin(MobDrops.class);
    }

    public NBT getNBTHandler() {
        return nbtHandler;
    }
}
