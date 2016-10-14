package net.pl3x.bukkit.mobdrops;

import net.pl3x.bukkit.mobdrops.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {
    private static void log(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', "&3[&d" + MobDrops.getPlugin().getName() + "&3]&r " + msg);
        if (!Config.COLOR_LOGS) {
            msg = ChatColor.stripColor(msg);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public static void debug(String msg) {
        if (Config.DEBUG_MODE) {
            Logger.log("&7[&eDEBUG&7]&e " + msg);
        }
    }

    public static void warn(String msg) {
        Logger.log("&e[&6WARN&e]&6 " + msg);
    }

    public static void error(String msg) {
        Logger.log("&e[&4ERROR&e]&4 " + msg);
    }

    public static void info(String msg) {
        Logger.log("&e[&fINFO&e]&r " + msg);
    }
}
