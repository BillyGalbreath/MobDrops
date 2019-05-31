package net.pl3x.bukkit.mobdrops;

import net.pl3x.bukkit.mobdrops.command.CmdMobDrops;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import net.pl3x.bukkit.mobdrops.configuration.Lang;
import net.pl3x.bukkit.mobdrops.listener.EntityListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MobDrops extends JavaPlugin {
    @Override
    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);

        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);

        getCommand("mobdrops").setExecutor(new CmdMobDrops(this));
    }
}
