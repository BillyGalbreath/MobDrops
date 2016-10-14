package net.pl3x.bukkit.mobdrops;

import net.pl3x.bukkit.mobdrops.api.NBT;
import net.pl3x.bukkit.mobdrops.command.CmdMobDrops;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import net.pl3x.bukkit.mobdrops.configuration.Lang;
import net.pl3x.bukkit.mobdrops.listener.EntityListener;
import net.pl3x.bukkit.mobdrops.nms.NBTHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MobDrops extends JavaPlugin {
    private NBT nbtHandler;

    public MobDrops() {
        nbtHandler = new NBTHandler();
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);

        getCommand("mobdrops").setExecutor(new CmdMobDrops(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        Logger.info(getName() + " disabled.");
    }

    public static MobDrops getPlugin() {
        return MobDrops.getPlugin(MobDrops.class);
    }

    public NBT getNBTHandler() {
        return nbtHandler;
    }
}
